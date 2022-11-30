package dev.jombi.kordsb.core.event.guild

import dev.jombi.kordsb.core.Kord
import dev.jombi.kordsb.core.entity.Guild
import dev.jombi.kordsb.core.event.Event

public class GuildUpdateEvent(
    public val guild: Guild,
    public val old: Guild?,
    override val customContext: Any?,
) : Event {
    override val kord: Kord get() = guild.kord
    override fun toString(): String {
        return "GuildUpdateEvent(guild=$guild)"
    }
}
