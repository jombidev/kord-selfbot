package dev.jombi.kordsb.core.gateway.handler

import dev.jombi.kordsb.core.Kord
import dev.jombi.kordsb.core.gateway.ShardEvent
import dev.jombi.kordsb.core.event.Event as CoreEvent

/**
 * Instances of this type are used to convert [gateway events][dev.jombi.kordsb.gateway.Event] to
 * [core events][dev.jombi.kordsb.core.event.Event].
 */
public interface GatewayEventInterceptor {

    /**
     * Converts a [gateway event][dev.jombi.kordsb.gateway.Event] (in the form of a [ShardEvent]) to a
     * [core event][dev.jombi.kordsb.core.event.Event].
     *
     * This might also have side effects like updating the [cache][Kord.cache].
     */
    public suspend fun handle(event: ShardEvent, kord: Kord): CoreEvent?

    public companion object {
        private object None : GatewayEventInterceptor {
            override suspend fun handle(event: ShardEvent, kord: Kord) = null
        }

        /**
         * Returns a [GatewayEventInterceptor] with no-op behavior.
         *
         * [handle] will always return `null`.
         */
        public fun none(): GatewayEventInterceptor = None
    }
}
