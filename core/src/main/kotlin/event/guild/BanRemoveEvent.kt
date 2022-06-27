package dev.jombi.kordsb.core.event.guild

import dev.jombi.kordsb.common.entity.Snowflake
import dev.jombi.kordsb.core.Kord
import dev.jombi.kordsb.core.behavior.GuildBehavior
import dev.jombi.kordsb.core.entity.Guild
import dev.jombi.kordsb.core.entity.Strategizable
import dev.jombi.kordsb.core.entity.User
import dev.jombi.kordsb.core.event.Event
import dev.jombi.kordsb.core.event.kordCoroutineScope
import dev.jombi.kordsb.core.supplier.EntitySupplier
import dev.jombi.kordsb.core.supplier.EntitySupplyStrategy
import kotlinx.coroutines.CoroutineScope
import kotlin.coroutines.CoroutineContext

public class BanRemoveEvent(
    public val user: User,
    public val guildId: Snowflake,
    override val supplier: EntitySupplier = user.kord.defaultSupplier,
    public val coroutineScope: CoroutineScope = kordCoroutineScope(user.kord)
) : Event, CoroutineScope by coroutineScope, Strategizable {

    override val kord: Kord get() = user.kord

    public val guild: GuildBehavior get() = GuildBehavior(guildId, kord)

    public suspend fun getGuild(): Guild = supplier.getGuild(guildId)

    public suspend fun getGuildOrNull(): Guild? = supplier.getGuildOrNull(guildId)

    override fun withStrategy(strategy: EntitySupplyStrategy<*>): BanRemoveEvent =
        BanRemoveEvent(user, guildId, strategy.supply(kord))

    override fun toString(): String {
        return "BanRemoveEvent(user=$user, guildId=$guildId, supplier=$supplier)"
    }
}
