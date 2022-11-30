package dev.jombi.kordsb.core.event.message

import dev.jombi.kordsb.common.entity.DiscordPartialMessage
import dev.jombi.kordsb.common.entity.Snowflake
import dev.jombi.kordsb.core.Kord
import dev.jombi.kordsb.core.behavior.MessageBehavior
import dev.jombi.kordsb.core.behavior.channel.MessageChannelBehavior
import dev.jombi.kordsb.core.entity.Message
import dev.jombi.kordsb.core.entity.Strategizable
import dev.jombi.kordsb.core.event.Event
import dev.jombi.kordsb.core.supplier.EntitySupplier
import dev.jombi.kordsb.core.supplier.EntitySupplyStrategy

public class MessageUpdateEvent(
    public val messageId: Snowflake,
    public val channelId: Snowflake,
    public val new: DiscordPartialMessage,
    public val old: Message?,
    override val kord: Kord,
    override val customContext: Any?,
    override val supplier: EntitySupplier = kord.defaultSupplier,
) : Event, Strategizable {

    /**
     * The behavior of the message that was updated.
     */
    public val message: MessageBehavior get() = MessageBehavior(messageId = messageId, channelId = channelId, kord = kord)

    /**
     * The behavior of the channel in which the message was updated.
     */
    public val channel: MessageChannelBehavior get() = MessageChannelBehavior(id = channelId, kord = kord)


    public suspend fun getMessage(): Message = supplier.getMessage(channelId = channelId, messageId = messageId)

    public suspend fun getMessageOrNull(): Message? = supplier.getMessageOrNull(channelId = channelId, messageId = messageId)

    override fun withStrategy(strategy: EntitySupplyStrategy<*>): MessageUpdateEvent =
        MessageUpdateEvent(messageId, channelId, new, old, kord, customContext, strategy.supply(kord))

    override fun toString(): String {
        return "MessageUpdateEvent(messageId=$messageId, channelId=$channelId, new=$new, old=$old, kord=$kord, supplier=$supplier)"
    }
}
