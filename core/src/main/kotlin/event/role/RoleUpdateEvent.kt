package dev.jombi.kordsb.core.event.role

import dev.jombi.kordsb.common.entity.Snowflake
import dev.jombi.kordsb.core.Kord
import dev.jombi.kordsb.core.behavior.GuildBehavior
import dev.jombi.kordsb.core.entity.Guild
import dev.jombi.kordsb.core.entity.Role
import dev.jombi.kordsb.core.entity.Strategizable
import dev.jombi.kordsb.core.event.Event
import dev.jombi.kordsb.core.event.kordCoroutineScope
import dev.jombi.kordsb.core.supplier.EntitySupplier
import dev.jombi.kordsb.core.supplier.EntitySupplyStrategy
import kotlinx.coroutines.CoroutineScope
import kotlin.coroutines.CoroutineContext

public class RoleUpdateEvent(
    public val role: Role,
    public val old: Role?,
    override val supplier: EntitySupplier = role.kord.defaultSupplier,
    public val coroutineScope: CoroutineScope = kordCoroutineScope(role.kord)
) : Event, CoroutineScope by coroutineScope, Strategizable {

    override val kord: Kord get() = role.kord

    public val guildId: Snowflake get() = role.guildId

    public val guild: GuildBehavior get() = GuildBehavior(guildId, kord)

    public suspend fun getGuild(): Guild = supplier.getGuild(guildId)

    public suspend fun getGuildOrNull(): Guild? = supplier.getGuildOrNull(guildId)

    override fun withStrategy(strategy: EntitySupplyStrategy<*>): RoleUpdateEvent =
        RoleUpdateEvent(role, old, strategy.supply(kord))

    override fun toString(): String {
        return "RoleUpdateEvent(role=$role, old=$old, supplier=$supplier)"
    }
}
