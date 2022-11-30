package dev.jombi.kordsb.core.event.guild

import dev.jombi.kordsb.common.entity.Snowflake
import dev.jombi.kordsb.core.Kord
import dev.jombi.kordsb.core.behavior.GuildBehavior
import dev.jombi.kordsb.core.entity.Guild
import dev.jombi.kordsb.core.entity.Member
import dev.jombi.kordsb.core.entity.Strategizable
import dev.jombi.kordsb.core.event.Event
import dev.jombi.kordsb.core.supplier.EntitySupplier
import dev.jombi.kordsb.core.supplier.EntitySupplyStrategy

public class MemberJoinEvent(
    public val member: Member,
    override val customContext: Any?,
    override val supplier: EntitySupplier = member.kord.defaultSupplier,
) : Event, Strategizable {

    override val kord: Kord get() = member.kord

    public val guildId: Snowflake get() = member.guildId

    public val guild: GuildBehavior get() = member.guild

    public suspend fun getGuild(): Guild = supplier.getGuild(guildId)

    public suspend fun getGuildOrNull(): Guild? = supplier.getGuildOrNull(guildId)

    override fun withStrategy(strategy: EntitySupplyStrategy<*>): MemberJoinEvent =
        MemberJoinEvent(member, customContext, strategy.supply(kord))

    override fun toString(): String {
        return "MemberJoinEvent(member=$member, supplier=$supplier)"
    }

}
