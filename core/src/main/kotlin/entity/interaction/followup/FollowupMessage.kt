package dev.jombi.kordsb.core.entity.interaction.followup

import dev.jombi.kordsb.common.entity.MessageFlag
import dev.jombi.kordsb.common.entity.Snowflake
import dev.jombi.kordsb.core.Kord
import dev.jombi.kordsb.core.behavior.interaction.followup.FollowupMessageBehavior
import dev.jombi.kordsb.core.entity.Message
import dev.jombi.kordsb.core.entity.channel.MessageChannel
import dev.jombi.kordsb.core.supplier.EntitySupplyStrategy

@Deprecated(
    "'InteractionFollowup' was renamed to 'FollowupMessage'.",
    ReplaceWith("FollowupMessage", "dev.jombi.kordsb.core.entity.interaction.FollowupMessage"),
    DeprecationLevel.ERROR,
)
public typealias InteractionFollowup = FollowupMessage

/**
 * Holds the followup [Message] resulting from an interaction followup
 * and behaves on it through [FollowupMessageBehavior].
 *
 * @param message The message created by this followup.
 * To use the message behavior your application must be authorized as a bot.
 */
public sealed class FollowupMessage(public val message: Message) : FollowupMessageBehavior {

    /**
     * The id of the followup message.
     */
    override val id: Snowflake get() = message.id

    /**
     * The id of the [MessageChannel] the followup message was sent in.
     */
    override val channelId: Snowflake get() = message.channelId

    abstract override fun withStrategy(strategy: EntitySupplyStrategy<*>): FollowupMessage
}


@PublishedApi
internal fun FollowupMessage(message: Message, applicationId: Snowflake, token: String, kord: Kord): FollowupMessage {
    val isEphemeral = message.flags?.contains(MessageFlag.Ephemeral) ?: false
    return when {
        isEphemeral -> EphemeralFollowupMessage(message, applicationId, token, kord)
        else -> PublicFollowupMessage(message, applicationId, token, kord)
    }
}
