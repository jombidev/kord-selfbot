package dev.jombi.kordsb.core.event.user

import dev.jombi.kordsb.core.Kord
import dev.jombi.kordsb.core.entity.VoiceState
import dev.jombi.kordsb.core.event.Event
import dev.jombi.kordsb.core.event.kordCoroutineScope
import kotlinx.coroutines.CoroutineScope
import kotlin.coroutines.CoroutineContext

public class VoiceStateUpdateEvent(
    public val old: VoiceState?,
    public val state: VoiceState,
    public val coroutineScope: CoroutineScope = kordCoroutineScope(state.kord)
) : Event, CoroutineScope by coroutineScope{
    override val kord: Kord get() = state.kord

    override fun toString(): String {
        return "VoiceStateUpdateEvent(old=$old, state=$state)"
    }
}
