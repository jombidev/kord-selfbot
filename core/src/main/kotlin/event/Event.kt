package dev.jombi.kordsb.core.event

import dev.jombi.kordsb.core.Kord
import dev.jombi.kordsb.gateway.Gateway
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.job

public interface Event : CoroutineScope {
    /**
     * The Gateway that spawned this event.
     */
    public val gateway: Gateway get() = kord.gateway.gateway

    public val kord: Kord
}

internal fun kordCoroutineScope(kord: Kord): CoroutineScope = CoroutineScope(kord.coroutineContext + SupervisorJob(kord.coroutineContext.job))
