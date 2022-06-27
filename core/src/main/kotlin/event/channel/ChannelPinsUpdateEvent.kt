package dev.jombi.kordsb.core.event.channel

import dev.jombi.kordsb.common.entity.Snowflake
import dev.jombi.kordsb.core.Kord
import dev.jombi.kordsb.core.behavior.GuildBehavior
import dev.jombi.kordsb.core.behavior.channel.MessageChannelBehavior
import dev.jombi.kordsb.core.entity.Strategizable
import dev.jombi.kordsb.core.entity.channel.MessageChannel
import dev.jombi.kordsb.core.event.Event
import dev.jombi.kordsb.core.event.channel.data.ChannelPinsUpdateEventData
import dev.jombi.kordsb.core.event.kordCoroutineScope
import dev.jombi.kordsb.core.supplier.EntitySupplier
import dev.jombi.kordsb.core.supplier.EntitySupplyStrategy
import dev.jombi.kordsb.core.supplier.getChannelOf
import dev.jombi.kordsb.core.supplier.getChannelOfOrNull
import kotlinx.coroutines.CoroutineScope
import kotlinx.datetime.Instant

public class ChannelPinsUpdateEvent(
    public val data: ChannelPinsUpdateEventData,
    override val kord: Kord,
    override val supplier: EntitySupplier = kord.defaultSupplier,
    public val coroutineScope: CoroutineScope = kordCoroutineScope(kord)
) : Event, CoroutineScope by coroutineScope,Strategizable {

    public val channelId: Snowflake get() = data.channelId

    public val guildId: Snowflake? get() = data.guildId.value

    public val lastPinTimestamp: Instant? get() = data.lastPinTimestamp.value

    public val guild: GuildBehavior? get() = guildId?.let { GuildBehavior(it, kord) }

    public val channel: MessageChannelBehavior get() = MessageChannelBehavior(channelId, kord)

    public suspend fun getChannel(): MessageChannel = supplier.getChannelOf(channelId)

    public suspend fun getChannelOrNull(): MessageChannel? = supplier.getChannelOfOrNull(channelId)

    override fun withStrategy(strategy: EntitySupplyStrategy<*>): ChannelPinsUpdateEvent =
        ChannelPinsUpdateEvent(data, kord, strategy.supply(kord))

    override fun toString(): String {
        return "ChannelPinsUpdateEvent(channelId=$channelId, lastPinTimestamp=$lastPinTimestamp, kord=$kord, supplier=$supplier)"
    }
}
