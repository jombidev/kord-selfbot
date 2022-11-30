package dev.jombi.kordsb.core.event.guild

import dev.jombi.kordsb.core.Kord
import dev.jombi.kordsb.core.entity.Guild
import dev.jombi.kordsb.core.event.Event

public class GuildCreateEvent(
    public val guild: Guild,
    override val customContext: Any?,
) : Event {
    override val kord: Kord get() = guild.kord

    override fun toString(): String {
        return "GuildCreateEvent(guild=$guild)"
    }
}
