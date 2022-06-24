package dev.kord.core.gateway

import dev.kord.gateway.*
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlin.time.Duration

public data class ShardEvent(val event: Event, val gateway: Gateway)

public interface MasterGateway {
    public val gateway: Gateway

    /**
     * Calculates the average [Gateway.ping] of all running [gateway].
     *
     * Gateways that return `null` are not counted into the average, if all [gateway]
     * return `null` then this property will return `null` as well.
     */
    public val averagePing: Duration?


    public val events: Flow<ShardEvent>

    public suspend fun startWithConfig(configuration: GatewayConfiguration): Unit = coroutineScope {
        val config = configuration.copy()
        launch {
            gateway.start(config)
        }
    }
}

public suspend inline fun MasterGateway.start(token: String, config: GatewayConfigurationBuilder.() -> Unit = {}) {
    val builder = GatewayConfigurationBuilder(token)
    builder.apply(config)
    startWithConfig(builder.build())
}
