package dev.jombi.kordsb.core.event.guild

import dev.jombi.kordsb.common.entity.Snowflake
import dev.jombi.kordsb.core.Kord
import dev.jombi.kordsb.core.behavior.GuildBehavior
import dev.jombi.kordsb.core.behavior.channel.TopGuildMessageChannelBehavior
import dev.jombi.kordsb.core.entity.Guild
import dev.jombi.kordsb.core.entity.Strategizable
import dev.jombi.kordsb.core.entity.channel.TopGuildMessageChannel
import dev.jombi.kordsb.core.event.Event
import dev.jombi.kordsb.core.supplier.EntitySupplier
import dev.jombi.kordsb.core.supplier.EntitySupplyStrategy
import dev.jombi.kordsb.core.supplier.getChannelOf
import dev.jombi.kordsb.core.supplier.getChannelOfOrNull

public class WebhookUpdateEvent(
    public val guildId: Snowflake,
    public val channelId: Snowflake,
    override val kord: Kord,
    override val customContext: Any?,
    override val supplier: EntitySupplier = kord.defaultSupplier,
) : Event, Strategizable {

    public val channel: TopGuildMessageChannelBehavior get() = TopGuildMessageChannelBehavior(guildId, channelId, kord)

    public val guild: GuildBehavior get() = GuildBehavior(guildId, kord)

    public suspend fun getChannel(): TopGuildMessageChannel = supplier.getChannelOf(channelId)

    public suspend fun getChannelOrNull(): TopGuildMessageChannel? = supplier.getChannelOfOrNull(channelId)

    public suspend fun getGuild(): Guild = guild.asGuild()

    public suspend fun getGuildOrNull(): Guild? = guild.asGuildOrNull()

    override fun withStrategy(strategy: EntitySupplyStrategy<*>): WebhookUpdateEvent =
        WebhookUpdateEvent(guildId, channelId, kord, customContext, strategy.supply(kord))

    override fun toString(): String {
        return "WebhookUpdateEvent(guildId=$guildId, channelId=$channelId, kord=$kord, supplier=$supplier)"
    }

}
