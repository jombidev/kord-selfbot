package dev.jombi.kordsb.core.event.guild

import dev.jombi.kordsb.common.entity.Snowflake
import dev.jombi.kordsb.core.Kord
import dev.jombi.kordsb.core.entity.Guild
import dev.jombi.kordsb.core.event.Event

public class GuildDeleteEvent(
    public val guildId: Snowflake,
    public val unavailable: Boolean,
    public val guild: Guild?,
    override val kord: Kord,
    override val customContext: Any?,
) : Event {

    override fun toString(): String {
        return "GuildDeleteEvent(guildId=$guildId, unavailable=$unavailable, guild=$guild, kord=$kord)"
    }

}
