package dev.jombi.kordsb.core.event.guild

import dev.jombi.kordsb.common.entity.Snowflake
import dev.jombi.kordsb.core.Kord
import dev.jombi.kordsb.core.entity.Guild
import dev.jombi.kordsb.core.event.Event
import dev.jombi.kordsb.core.event.kordCoroutineScope
import kotlinx.coroutines.CoroutineScope
import kotlin.coroutines.CoroutineContext

public class GuildDeleteEvent(
    public val guildId: Snowflake,
    public val unavailable: Boolean,
    public val guild: Guild?,
    override val kord: Kord,
    public val coroutineScope: CoroutineScope = kordCoroutineScope(kord)
) : Event, CoroutineScope by coroutineScope {

    override fun toString(): String {
        return "GuildDeleteEvent(guildId=$guildId, unavailable=$unavailable, guild=$guild, kord=$kord)"
    }

}
