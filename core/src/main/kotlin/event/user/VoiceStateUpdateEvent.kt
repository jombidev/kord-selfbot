package dev.jombi.kordsb.core.event.user

import dev.jombi.kordsb.core.Kord
import dev.jombi.kordsb.core.entity.VoiceState
import dev.jombi.kordsb.core.event.Event

public class VoiceStateUpdateEvent(
    public val old: VoiceState?,
    public val state: VoiceState,
    override val customContext: Any?,
) : Event {
    override val kord: Kord get() = state.kord

    override fun toString(): String {
        return "VoiceStateUpdateEvent(old=$old, state=$state)"
    }
}
