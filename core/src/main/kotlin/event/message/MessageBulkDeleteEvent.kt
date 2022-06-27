package dev.jombi.kordsb.core.event.message

import dev.jombi.kordsb.common.entity.Snowflake
import dev.jombi.kordsb.core.Kord
import dev.jombi.kordsb.core.behavior.GuildBehavior
import dev.jombi.kordsb.core.behavior.channel.MessageChannelBehavior
import dev.jombi.kordsb.core.entity.Guild
import dev.jombi.kordsb.core.entity.Message
import dev.jombi.kordsb.core.entity.Strategizable
import dev.jombi.kordsb.core.entity.channel.MessageChannel
import dev.jombi.kordsb.core.event.Event
import dev.jombi.kordsb.core.event.kordCoroutineScope
import dev.jombi.kordsb.core.supplier.EntitySupplier
import dev.jombi.kordsb.core.supplier.EntitySupplyStrategy
import dev.jombi.kordsb.core.supplier.getChannelOf
import dev.jombi.kordsb.core.supplier.getChannelOfOrNull
import kotlinx.coroutines.CoroutineScope
import kotlin.coroutines.CoroutineContext

public class MessageBulkDeleteEvent(
    public val messageIds: Set<Snowflake>,
    public val messages: Set<Message>,
    public val channelId: Snowflake,
    public val guildId: Snowflake?,
    override val kord: Kord,
    override val supplier: EntitySupplier = kord.defaultSupplier,
    public val coroutineScope: CoroutineScope = kordCoroutineScope(kord)
) : Event, CoroutineScope by coroutineScope, Strategizable {

    public val channel: MessageChannelBehavior get() = MessageChannelBehavior(channelId, kord)

    public val guild: GuildBehavior? get() = guildId?.let { GuildBehavior(it, kord) }

    public suspend fun getChannel(): MessageChannel = supplier.getChannelOf(channelId)

    public suspend fun getChannelOrNull(): MessageChannel? = supplier.getChannelOfOrNull(channelId)

    public suspend fun getGuild(): Guild? = guildId?.let { supplier.getGuildOrNull(it) }

    override fun withStrategy(strategy: EntitySupplyStrategy<*>): MessageBulkDeleteEvent =
        MessageBulkDeleteEvent(messageIds, messages, channelId, guildId, kord, strategy.supply(kord))

    override fun toString(): String {
        return "MessageBulkDeleteEvent(messageIds=$messageIds, messages=$messages, channelId=$channelId, guildId=$guildId, kord=$kord, supplier=$supplier)"
    }

}
