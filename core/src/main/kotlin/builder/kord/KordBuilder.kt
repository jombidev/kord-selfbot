package dev.jombi.kordsb.core.builder.kord

import dev.jombi.kordsb.common.KordConstants
import dev.jombi.kordsb.common.annotation.KordExperimental
import dev.jombi.kordsb.common.entity.Snowflake
import dev.jombi.kordsb.common.ratelimit.IntervalRateLimiter
import dev.jombi.kordsb.core.ClientResources
import dev.jombi.kordsb.core.Kord
import dev.jombi.kordsb.core.cache.KordCacheBuilder
import dev.jombi.kordsb.core.cache.registerKordData
import dev.jombi.kordsb.core.event.Event
import dev.jombi.kordsb.core.exception.KordInitializationException
import dev.jombi.kordsb.core.gateway.DefaultMasterGateway
import dev.jombi.kordsb.core.gateway.handler.DefaultGatewayEventInterceptor
import dev.jombi.kordsb.core.gateway.handler.GatewayEventInterceptor
import dev.jombi.kordsb.core.supplier.EntitySupplyStrategy
import dev.jombi.kordsb.gateway.DefaultGateway
import dev.jombi.kordsb.gateway.Gateway
import dev.jombi.kordsb.gateway.retry.LinearRetry
import dev.jombi.kordsb.gateway.retry.Retry
import dev.jombi.kordsb.rest.json.response.BotGatewayResponse
import dev.jombi.kordsb.rest.ratelimit.ExclusionRequestRateLimiter
import dev.jombi.kordsb.rest.request.*
import dev.jombi.kordsb.rest.route.Route
import dev.jombi.kordsb.rest.service.RestClient
import dev.kord.cache.api.DataCache
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.http.HttpHeaders.Authorization
import io.ktor.http.HttpHeaders.UserAgent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import mu.KotlinLogging
import kotlin.concurrent.thread
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract
import kotlin.time.Duration.Companion.seconds

public operator fun DefaultGateway.Companion.invoke(
    resources: ClientResources,
    retry: Retry = LinearRetry(2.seconds, 60.seconds, 10)
): DefaultGateway {
    return DefaultGateway {
        client = resources.httpClient
        reconnectRetry = retry
        sendRateLimiter = IntervalRateLimiter(limit = 120, interval = 60.seconds)
        identifyRateLimiter = IntervalRateLimiter(limit = 1, interval = 5.seconds)
    }
}

private val logger = KotlinLogging.logger { }
private val gatewayInfoJson = Json { ignoreUnknownKeys = true }

public class KordBuilder(public val token: String) {
    private var handlerBuilder: (resources: ClientResources) -> RequestHandler =
        { KtorRequestHandler(it.httpClient, ExclusionRequestRateLimiter(), token = token) }
    private var cacheBuilder: KordCacheBuilder.(resources: ClientResources) -> Unit = {}

    /**
     * Enables stack trace recovery on the currently defined [RequestHandler].
     *
     * @throws IllegalStateException if the [RequestHandler] is not a [KtorRequestHandler]
     *
     * @see StackTraceRecoveringKtorRequestHandler
     * @see withStackTraceRecovery
     */
    public var stackTraceRecovery: Boolean = false

    /**
     * Enable adding a [Runtime.addShutdownHook] to log out of the [Gateway] when the process is killed.
     */
    public var enableShutdownHook: Boolean = true

    /**
     * The event flow used by [Kord.eventFlow] to publish [events][Kord.events].
     *
     *
     * By default, a [MutableSharedFlow] with an `extraBufferCapacity` of `Int.MAX_VALUE` is used.
     */
    public var eventFlow: MutableSharedFlow<Event> = MutableSharedFlow(
        extraBufferCapacity = Int.MAX_VALUE
    )

    /**
     * The [CoroutineDispatcher] kord uses to launch suspending tasks. [Dispatchers.Default] by default.
     */
    public var defaultDispatcher: CoroutineDispatcher = Dispatchers.Default

    /**
     * The default strategy used by entities to retrieve entities. [EntitySupplyStrategy.cacheWithRestFallback] by default.
     */
    public var defaultStrategy: EntitySupplyStrategy<*> = EntitySupplyStrategy.cacheWithRestFallback

    /**
     * The client used for building [Gateways][Gateway] and [RequestHandlers][RequestHandler]. A default implementation
     * will be used when not set.
     */
    public var httpClient: HttpClient? = null

    public var applicationId: Snowflake? = null

    /**
     * The [GatewayEventInterceptor] used for converting [gateway events][dev.jombi.kordsb.gateway.Event] to
     * [core events][dev.jombi.kordsb.core.event.Event].
     *
     * [DefaultGatewayEventInterceptor] will be used when not set.
     */
    public var gatewayEventInterceptor: GatewayEventInterceptor? = null

    /**
     * Configures the [RequestHandler] for the [RestClient].
     *
     * ```
     * Kord(token) {
     *     { resources -> KtorRequestHandler(resources.httpClient, ExclusionRequestRateLimiter()) }
     * }
     * ```
     */
    public fun requestHandler(handlerBuilder: (resources: ClientResources) -> RequestHandler) {
        this.handlerBuilder = handlerBuilder
    }

    /**
     * Configures the [DataCache] for caching.
     *
     *  ```
     * Kord(token) {
     *     cache {
     *         defaultGenerator = lruCache()
     *         forDescription(MessageData.description) { cache, description -> DataEntryCache.none() }
     *         forDescription(UserData.description) { cache, description -> MapEntryCache(cache, description, MapLikeCollection.weakHashMap()) }
     *     }
     * }
     * ```
     */
    public fun cache(builder: KordCacheBuilder.(resources: ClientResources) -> Unit) {
        contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
        val old = cacheBuilder
        cacheBuilder = { resources: ClientResources ->
            old(resources)
            builder(resources)
        }
    }


    /**
     * Requests the gateway info for the bot, or throws a [KordInitializationException] when something went wrong.
     */
    @OptIn(KordExperimental::class)
    private suspend fun HttpClient.getGatewayInfo(): BotGatewayResponse {
        val response = get("${Route.baseUrl}${Route.GatewayBotGet.path}") {
            header(UserAgent, KordConstants.USER_AGENT)
            header(Authorization, token)
        }
        val responseBody = response.bodyAsText()
        if (response.isError) {
            val message = buildString {
                append("Something went wrong while initializing Kord")
                if (response.status == HttpStatusCode.Unauthorized) {
                    append(", make sure the bot token you entered is valid.")
                } else {
                    append('.')
                }

                appendLine(responseBody)
            }

            throw KordInitializationException(message)
        }

        return gatewayInfoJson.decodeFromString(BotGatewayResponse.serializer(), responseBody)
    }

    /**
     * @throws KordInitializationException if something went wrong while getting the bot's gateway information.
     */
    public suspend fun build(): Kord {
        val client = httpClient.configure()

        // prevent ConnectTimeoutException
        // Connect timeout has expired [url=https://discord.com/api/v10/gateway/bot, connect_timeout=unknown ms]

        //val recommendedShards = client.getGatewayInfo().shards
        //val shardsInfo = shardsBuilder(recommendedShards)
        //val shards = shardsInfo.indices.toList()
//
        //if (client.engine.config.threadsCount < shards.size + 1) {
        //    logger.warn {
        //        """
        //        kord's http client is currently using ${client.engine.config.threadsCount} threads,
        //        which is less than the advised thread count of ${shards.size + 1} (number of shards + 1)
        //        """.trimIndent()
        //    }
        //}

        val resources =
            ClientResources(token, client, defaultStrategy)
        val rawRequestHandler = handlerBuilder(resources)
        val requestHandler = if (stackTraceRecovery) {
            if (rawRequestHandler is KtorRequestHandler) {
                rawRequestHandler.withStackTraceRecovery()
            } else {
                error("stackTraceRecovery only works with KtorRequestHandlers, please set stackTraceRecovery = false or use a different RequestHandler")
            }
        } else {
            rawRequestHandler
        }
        val rest = RestClient(requestHandler)
        val cache = KordCacheBuilder().apply { cacheBuilder(resources) }.build()
        cache.registerKordData()
        val gateway = run {
            val rateLimiter = IntervalRateLimiter(limit = 1, interval = 5.seconds)

            DefaultMasterGateway(DefaultGateway {
                this.client = resources.httpClient
                identifyRateLimiter = rateLimiter
            })
        }

        val self = getBotIdFromToken(token)

        if (enableShutdownHook) {
            Runtime.getRuntime().addShutdownHook(thread(false) {
                runBlocking {
                    gateway.gateway.detach()
                }
            })
        }

        return Kord(
            resources = resources,
            cache = cache,
            gateway = gateway,
            rest = rest,
            selfId = self,
            eventFlow = eventFlow,
            dispatcher = defaultDispatcher,
            interceptor = gatewayEventInterceptor ?: DefaultGatewayEventInterceptor(),
        )
    }

}
