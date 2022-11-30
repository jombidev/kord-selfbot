package dev.jombi.kordsb.core.event.guild

import dev.jombi.kordsb.common.entity.Snowflake
import dev.jombi.kordsb.core.Kord
import dev.jombi.kordsb.core.behavior.GuildBehavior
import dev.jombi.kordsb.core.entity.Guild
import dev.jombi.kordsb.core.entity.GuildEmoji
import dev.jombi.kordsb.core.entity.Strategizable
import dev.jombi.kordsb.core.event.Event
import dev.jombi.kordsb.core.supplier.EntitySupplier
import dev.jombi.kordsb.core.supplier.EntitySupplyStrategy

public class EmojisUpdateEvent(
    public val guildId: Snowflake,
    public val emojis: Set<GuildEmoji>,
    public val old: Set<GuildEmoji>?,
    override val kord: Kord,
    override val customContext: Any?,
    override val supplier: EntitySupplier = kord.defaultSupplier,
) : Event, Strategizable {

    public val guild: GuildBehavior get() = GuildBehavior(guildId, kord)

    public suspend fun getGuild(): Guild = supplier.getGuild(guildId)

    public suspend fun getGuildOrNull(): Guild? = supplier.getGuildOrNull(guildId)

    override fun withStrategy(strategy: EntitySupplyStrategy<*>): EmojisUpdateEvent =
        EmojisUpdateEvent(guildId, emojis, old, kord, customContext, strategy.supply(kord))

    override fun toString(): String {
        return "EmojisUpdateEvent(guildId=$guildId, emojis=$emojis, old=$old, kord=$kord, supplier=$supplier)"
    }
}
