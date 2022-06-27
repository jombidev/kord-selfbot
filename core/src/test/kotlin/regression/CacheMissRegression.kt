package regression

import dev.kord.cache.api.put
import dev.kord.cache.map.MapDataCache
import dev.jombi.kordsb.common.entity.ChannelType
import dev.jombi.kordsb.common.entity.Snowflake
import dev.jombi.kordsb.core.ClientResources
import dev.jombi.kordsb.core.Kord
import dev.jombi.kordsb.core.builder.kord.configure
import dev.jombi.kordsb.core.builder.kord.getBotIdFromToken
import dev.jombi.kordsb.core.cache.data.ChannelData
import dev.jombi.kordsb.core.cache.registerKordData
import dev.jombi.kordsb.core.gateway.DefaultMasterGateway
import dev.jombi.kordsb.core.supplier.EntitySupplyStrategy
import dev.jombi.kordsb.gateway.Command
import dev.jombi.kordsb.gateway.Event
import dev.jombi.kordsb.gateway.Gateway
import dev.jombi.kordsb.gateway.GatewayConfiguration
import dev.jombi.kordsb.rest.request.JsonRequest
import dev.jombi.kordsb.rest.request.MultipartRequest
import dev.jombi.kordsb.rest.request.Request
import dev.jombi.kordsb.rest.request.RequestHandler
import dev.jombi.kordsb.rest.route.Route
import dev.jombi.kordsb.rest.service.RestClient
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.client.statement.*
import io.ktor.content.*
import io.ktor.http.*
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.test.BeforeTest
import kotlin.time.Duration


private val parser = Json {
    encodeDefaults = false
    allowStructuredMapKeys = true
    ignoreUnknownKeys = true
    isLenient = true
}

object FakeGateway : Gateway {

    val deferred = CompletableDeferred<Unit>()

    override val events: SharedFlow<Event> = MutableSharedFlow<Event>()

    override val ping: StateFlow<Duration?> = MutableStateFlow(null)

    override suspend fun detach() {}

    override suspend fun send(command: Command) {}
    override suspend fun start(configuration: GatewayConfiguration) {
        deferred.await()
    }

    override suspend fun stop() {
        deferred.complete(Unit)
    }

    override val coroutineContext: CoroutineContext = SupervisorJob() + EmptyCoroutineContext
}

class CrashingHandler(val client: HttpClient, override val token: String) : RequestHandler {
    override suspend fun <B : Any, R> handle(request: Request<B, R>): R {
        if (request.route != Route.CurrentUserGet) throw IllegalStateException("shouldn't do a request")
        val response = client.request {
            method = request.route.method
            headers.appendAll(request.headers)

            url {
                url.takeFrom(Route.baseUrl)
                encodedPath += request.path
                parameters.appendAll(request.parameters)
            }


            request.body?.let {
                @Suppress("UNCHECKED_CAST")
                when (request) {
                    is MultipartRequest<*, *> -> {
                        headers.append(
                            "payload_json",
                            parser.encodeToString(it.strategy as SerializationStrategy<Any>, it.body)
                        )
                        setBody(MultiPartFormDataContent(request.data))
                    }

                    is JsonRequest<*, *> -> {
                        val json = parser.encodeToString(it.strategy as SerializationStrategy<Any>, it.body)
                        setBody(TextContent(json, ContentType.Application.Json))
                    }
                }
            }
        }

        return request.route.mapper.deserialize(parser, response.bodyAsText())
    }
}

@EnabledIfEnvironmentVariable(named = "KORD_TEST_TOKEN", matches = ".+")
class CacheMissingRegressions {
    lateinit var kord: Kord

    @BeforeTest
    fun setup() = runTest { //TODO, move this over to entity supplier tests instead, eventually.
        val token = System.getenv("KORD_TEST_TOKEN")
        val resources = ClientResources(
            token,
            null.configure(),
            EntitySupplyStrategy.cacheWithRestFallback,
        )
        kord = Kord(
            resources,
            MapDataCache().also { it.registerKordData() },
            DefaultMasterGateway(FakeGateway),
            RestClient(CrashingHandler(resources.httpClient, resources.token)),
            getBotIdFromToken(token),
            MutableSharedFlow(extraBufferCapacity = Int.MAX_VALUE),
            Dispatchers.Default
        )
    }


    @Test
    fun `if data not in cache explode`() {
        val id = 5uL
        assertThrows<IllegalStateException> {
            runBlocking {
                kord.getChannel(Snowflake(id))
            }
        }
    }

    @Test
    fun `if data in cache don't fetch from rest`() {
        runBlocking {
            val id = Snowflake(5uL)
            kord.cache.put(ChannelData(id, ChannelType.GuildText))

            kord.getChannel(id)
        }
    }

}
