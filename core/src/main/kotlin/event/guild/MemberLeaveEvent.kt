package dev.jombi.kordsb.core.event.guild

import dev.jombi.kordsb.common.entity.Snowflake
import dev.jombi.kordsb.core.Kord
import dev.jombi.kordsb.core.behavior.GuildBehavior
import dev.jombi.kordsb.core.entity.Guild
import dev.jombi.kordsb.core.entity.Member
import dev.jombi.kordsb.core.entity.User
import dev.jombi.kordsb.core.event.Event

public class MemberLeaveEvent(
    public val user: User,
    public val old: Member?,
    public val guildId: Snowflake,
    override val customContext: Any?,
) : Event {

    override val kord: Kord get() = user.kord

    public val guild: GuildBehavior get() = GuildBehavior(guildId, kord)

    public suspend fun getGuild(): Guild = guild.asGuild()

    public suspend fun getGuildOrNull(): Guild? = guild.asGuildOrNull()

    override fun toString(): String {
        return "MemberLeaveEvent(user=$user, old=$old, guildId=$guildId)"
    }

}
