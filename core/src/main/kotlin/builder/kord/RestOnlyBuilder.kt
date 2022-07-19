package dev.jombi.kordsb.core.builder.kord

import dev.kord.cache.api.DataCache
import dev.jombi.kordsb.common.entity.Snowflake
import dev.jombi.kordsb.core.ClientResources
import dev.jombi.kordsb.core.Kord
import dev.jombi.kordsb.core.gateway.DefaultMasterGateway
import dev.jombi.kordsb.core.supplier.EntitySupplyStrategy
import dev.jombi.kordsb.gateway.Gateway
import dev.jombi.kordsb.rest.ratelimit.ExclusionRequestRateLimiter
import dev.jombi.kordsb.rest.request.KtorRequestHandler
import dev.jombi.kordsb.rest.request.RequestHandler
import dev.jombi.kordsb.rest.service.RestClient
import io.ktor.client.*
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow

public abstract class RestOnlyBuilder {
    protected var handlerBuilder: (resources: ClientResources) -> RequestHandler =
        { KtorRequestHandler(it.httpClient, ExclusionRequestRateLimiter(), token = it.token) }

    protected abstract val token: String

    /**
     * The [CoroutineDispatcher] kord uses to launch suspending tasks. [Dispatchers.Default] by default.
     */
    public var defaultDispatcher: CoroutineDispatcher = Dispatchers.Default

    /**
     * The client used for building [Gateways][Gateway] and [RequestHandlers][RequestHandler]. A default implementation
     * will be used when not set.
     */
    public var httpClient: HttpClient? = null

    public abstract var applicationId: Snowflake

    /**
     * Configures the [RequestHandler] for the [RestClient].
     *
     * ```
     * Kord(token) {
     *   requestHandler  { resources -> KtorRequestHandler(resources.httpClient, ExclusionRequestRateLimiter()) }
     * }
     * ```
     */
    public fun requestHandler(handlerBuilder: (resources: ClientResources) -> RequestHandler) {
        this.handlerBuilder = handlerBuilder
    }

    public fun build(): Kord {
        val client = httpClient.configure()
        val selfId = applicationId

        val resources = ClientResources(
            token,
            client,
            EntitySupplyStrategy.rest,
        )

        val rest = RestClient(handlerBuilder(resources))

        return Kord(
            resources,
            @OptIn(ExperimentalCoroutinesApi::class)
            DataCache.none(),
            DefaultMasterGateway(Gateway.none()),
            rest,
            selfId,
            MutableSharedFlow(),
            defaultDispatcher,
        )
    }
}