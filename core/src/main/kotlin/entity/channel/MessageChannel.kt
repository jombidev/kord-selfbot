package dev.jombi.kordsb.core.entity.channel

import dev.jombi.kordsb.common.entity.Snowflake
import dev.jombi.kordsb.common.entity.optional.value
import dev.jombi.kordsb.common.exception.RequestException
import dev.jombi.kordsb.core.behavior.MessageBehavior
import dev.jombi.kordsb.core.behavior.channel.MessageChannelBehavior
import dev.jombi.kordsb.core.entity.Message
import dev.jombi.kordsb.core.supplier.EntitySupplyStrategy
import kotlinx.datetime.Instant

/**
 * An instance of a Discord channel that can use messages.
 */
public interface MessageChannel : Channel, MessageChannelBehavior {

    /**
     * The id of the last message sent to this channel, if present.
     */
    public val lastMessageId: Snowflake? get() = data.lastMessageId.value

    /**
     * The behavior of the last message sent to this channel, if present.
     */
    public val lastMessage: MessageBehavior? get() = lastMessageId?.let { MessageBehavior(id, it, kord) }

    /**
     * The timestamp of the last pin
     */
    @Deprecated(
        "lastPinTimeStamp was renamed to lastPinTimestamp.",
        ReplaceWith("lastPinTimestamp"),
        DeprecationLevel.HIDDEN,
    )
    public val lastPinTimeStamp: Instant?
        get() = lastPinTimestamp

    /**
     * The timestamp of the last pin
     */
    public val lastPinTimestamp: Instant? get() = data.lastPinTimestamp.value

    /**
     * Requests to get the last message sent to this channel,
     * return null if no [lastMessageId] is present or if the message itself isn't present.
     *
     * @throws [RequestException] if something went wrong during the request.
     */
    public suspend fun getLastMessage(): Message? {
        val messageId = lastMessageId ?: return null

        return supplier.getMessageOrNull(id, messageId)
    }

    /**
     * Returns a new [MessageChannel] with the given [strategy].
     */
    override fun withStrategy(strategy: EntitySupplyStrategy<*>): MessageChannel

}
