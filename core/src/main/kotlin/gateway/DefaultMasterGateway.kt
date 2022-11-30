package dev.jombi.kordsb.core.gateway

import dev.jombi.kordsb.gateway.Gateway
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlin.time.Duration
import kotlin.time.Duration.Companion.microseconds

public class DefaultMasterGateway(
    override val gateway: Gateway,
): MasterGateway {

    /**
     * Calculates the average [Gateway.ping] of all running [gateway].
     *
     * Gateways that return `null` are not counted into the average, if all [gateway]
     * return `null` then this property will return `null` as well.
     */
    override val averagePing: Duration?
        get(): Duration? {
            return gateway.ping.value?.inWholeMicroseconds?.microseconds
        }

    override val events: Flow<ShardEvent> = gateway.events.map { ShardEvent(it, gateway) }

    override fun toString(): String {
        return "MasterGateway(gateways=$gateway)"
    }

}
