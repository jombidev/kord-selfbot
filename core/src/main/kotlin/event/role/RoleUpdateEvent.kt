package dev.jombi.kordsb.core.event.role

import dev.jombi.kordsb.common.entity.Snowflake
import dev.jombi.kordsb.core.Kord
import dev.jombi.kordsb.core.behavior.GuildBehavior
import dev.jombi.kordsb.core.entity.Guild
import dev.jombi.kordsb.core.entity.Role
import dev.jombi.kordsb.core.entity.Strategizable
import dev.jombi.kordsb.core.event.Event
import dev.jombi.kordsb.core.supplier.EntitySupplier
import dev.jombi.kordsb.core.supplier.EntitySupplyStrategy

public class RoleUpdateEvent(
    public val role: Role,
    public val old: Role?,
    override val customContext: Any?,
    override val supplier: EntitySupplier = role.kord.defaultSupplier,
) : Event, Strategizable {

    override val kord: Kord get() = role.kord

    public val guildId: Snowflake get() = role.guildId

    public val guild: GuildBehavior get() = GuildBehavior(guildId, kord)

    public suspend fun getGuild(): Guild = supplier.getGuild(guildId)

    public suspend fun getGuildOrNull(): Guild? = supplier.getGuildOrNull(guildId)

    override fun withStrategy(strategy: EntitySupplyStrategy<*>): RoleUpdateEvent =
        RoleUpdateEvent(role, old, customContext, strategy.supply(kord))

    override fun toString(): String {
        return "RoleUpdateEvent(role=$role, old=$old, supplier=$supplier)"
    }
}
