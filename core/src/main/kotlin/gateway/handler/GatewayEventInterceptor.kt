package dev.jombi.kordsb.core.gateway.handler

import dev.jombi.kordsb.core.Kord
import dev.jombi.kordsb.core.gateway.ShardEvent
import dev.jombi.kordsb.core.event.Event as CoreEvent

public interface GatewayEventInterceptor {

    public suspend fun handle(event: ShardEvent, kord: Kord): CoreEvent?
}
