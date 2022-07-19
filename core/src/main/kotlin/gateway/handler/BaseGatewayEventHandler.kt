package dev.jombi.kordsb.core.gateway.handler

import dev.jombi.kordsb.core.Kord
import dev.kord.cache.api.DataCache
import kotlinx.coroutines.CoroutineScope
import dev.jombi.kordsb.core.event.Event as CoreEvent
import dev.jombi.kordsb.gateway.Event as GatewayEvent

public abstract class BaseGatewayEventHandler(
    protected val cache: DataCache
) {

    public abstract suspend fun handle(event: GatewayEvent, kord: Kord, coroutineScope: CoroutineScope): CoreEvent?

}
