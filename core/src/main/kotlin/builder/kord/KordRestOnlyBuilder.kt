package dev.jombi.kordsb.core.builder.kord

import dev.kord.cache.api.DataCache
import dev.jombi.kordsb.common.annotation.KordExperimental
import dev.jombi.kordsb.common.entity.Snowflake
import dev.jombi.kordsb.core.ClientResources
import dev.jombi.kordsb.core.Kord
import dev.jombi.kordsb.core.exception.KordInitializationException
import dev.jombi.kordsb.core.gateway.DefaultMasterGateway
import dev.jombi.kordsb.core.supplier.EntitySupplyStrategy
import dev.jombi.kordsb.gateway.Gateway
import dev.jombi.kordsb.rest.ratelimit.ExclusionRequestRateLimiter
import dev.jombi.kordsb.rest.request.KtorRequestHandler
import dev.jombi.kordsb.rest.request.RequestHandler
import dev.jombi.kordsb.rest.service.RestClient
import io.ktor.client.HttpClient
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow

/**
 * The rest only Kord builder. You probably want to invoke the [DSL builder][Kord.restOnly] instead.
 */
@KordExperimental
public class KordRestOnlyBuilder(public val token: String) {

    private var handlerBuilder: (resources: ClientResources) -> RequestHandler =
        { KtorRequestHandler(it.httpClient, ExclusionRequestRateLimiter(), token = it.token) }

    /**
     * The [CoroutineDispatcher] kord uses to launch suspending tasks. [Dispatchers.Default] by default.
     */
    public var defaultDispatcher: CoroutineDispatcher = Dispatchers.Default

    /**
     * The client used for building [Gateways][Gateway] and [RequestHandlers][RequestHandler]. A default implementation
     * will be used when not set.
     */
    public var httpClient: HttpClient? = null

    public var applicationId: Snowflake? = null

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
     * @throws KordInitializationException if something went wrong while getting the bot's gateway information.
     */
    public fun build(): Kord {
        val client = httpClient.configure()

        val resources = ClientResources(
            token,
            client,
            EntitySupplyStrategy.rest,
        )
        val rest = RestClient(handlerBuilder(resources))
        val selfId = getBotIdFromToken(token)

        return Kord(
            resources,
            DataCache.none(),
            DefaultMasterGateway(Gateway.none()),
            rest,
            selfId,
            MutableSharedFlow(),
            defaultDispatcher
        )
    }
}
