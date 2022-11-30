package dev.jombi.kordsb.core.gateway.handler

import dev.jombi.kordsb.core.Kord
import dev.jombi.kordsb.core.event.Event as CoreEvent
import dev.jombi.kordsb.gateway.Event as GatewayEvent

internal abstract class BaseGatewayEventHandler {
    abstract suspend fun handle(event: GatewayEvent, kord: Kord, context: LazyContext?): CoreEvent?
}
