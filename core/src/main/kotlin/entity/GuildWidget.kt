package dev.jombi.kordsb.core.entity

import dev.jombi.kordsb.common.entity.Snowflake
import dev.jombi.kordsb.core.Kord
import dev.jombi.kordsb.core.KordObject
import dev.jombi.kordsb.core.behavior.GuildBehavior
import dev.jombi.kordsb.core.behavior.channel.ChannelBehavior
import dev.jombi.kordsb.core.cache.data.GuildWidgetData
import dev.jombi.kordsb.core.entity.channel.Channel
import dev.jombi.kordsb.core.entity.channel.TopGuildChannel
import dev.jombi.kordsb.core.supplier.EntitySupplier
import dev.jombi.kordsb.core.supplier.EntitySupplyStrategy
import dev.jombi.kordsb.core.supplier.getChannelOfOrNull
import dev.jombi.kordsb.rest.builder.guild.GuildWidgetModifyBuilder
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

public class GuildWidget(
    public val data: GuildWidgetData,
    public val guildId: Snowflake,
    override val kord: Kord,
    override val supplier: EntitySupplier = kord.defaultSupplier,
) : KordObject, Strategizable {

    public val isEnabled: Boolean get() = data.enabled

    public val guild: GuildBehavior get() = GuildBehavior(guildId, kord)

    public val channelId: Snowflake? get() = data.channelId

    public val channel: ChannelBehavior? get() = data.channelId?.let { ChannelBehavior(it, kord) }

    public suspend fun getGuild(): Guild = supplier.getGuild(guildId)

    public suspend fun getGuildOrNull(): Guild? = supplier.getGuildOrNull(guildId)

    public suspend fun getChannelOrNull(): TopGuildChannel? = data.channelId?.let { supplier.getChannelOfOrNull(it) }

    public suspend inline fun <reified T : Channel> getChannelOfOrNull(): T? =
        data.channelId?.let { supplier.getChannelOfOrNull(it) }

    public suspend inline fun edit(builder: GuildWidgetModifyBuilder.() -> Unit): GuildWidget {
        contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
        return GuildWidget(GuildWidgetData.from(kord.rest.guild.modifyGuildWidget(guildId, builder)), guildId, kord)
    }

    override fun withStrategy(strategy: EntitySupplyStrategy<*>): GuildWidget =
        GuildWidget(data, guildId, kord, strategy.supply(kord))

}
