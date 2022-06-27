package dev.jombi.kordsb.core.behavior.interaction.followup

import dev.jombi.kordsb.common.entity.Snowflake
import dev.jombi.kordsb.core.behavior.channel.MessageChannelBehavior
import dev.jombi.kordsb.core.cache.data.toData
import dev.jombi.kordsb.core.entity.KordEntity
import dev.jombi.kordsb.core.entity.Message
import dev.jombi.kordsb.core.entity.Strategizable
import dev.jombi.kordsb.core.entity.channel.MessageChannel
import dev.jombi.kordsb.core.entity.interaction.followup.EphemeralFollowupMessage
import dev.jombi.kordsb.core.entity.interaction.followup.FollowupMessage
import dev.jombi.kordsb.core.entity.interaction.followup.PublicFollowupMessage
import dev.jombi.kordsb.core.supplier.EntitySupplyStrategy
import dev.jombi.kordsb.core.supplier.getChannelOf
import dev.jombi.kordsb.core.supplier.getChannelOfOrNull
import dev.jombi.kordsb.rest.builder.message.modify.FollowupMessageModifyBuilder
import dev.jombi.kordsb.rest.request.RestRequestException
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

/**
 * The behavior of a [Followup Message](https://discord.com/developers/docs/interactions/receiving-and-responding#followup-messages)
 */
public interface FollowupMessageBehavior : KordEntity, Strategizable {

    public val applicationId: Snowflake
    public val token: String
    public val channelId: Snowflake

    public val channel: MessageChannelBehavior get() = MessageChannelBehavior(channelId, kord)

    public suspend fun getChannel(): MessageChannel = supplier.getChannelOf(channelId)

    public suspend fun getChannelOrNull(): MessageChannel? = supplier.getChannelOfOrNull(channelId)

    override fun withStrategy(strategy: EntitySupplyStrategy<*>): FollowupMessageBehavior
}

/**
 * Requests to edit this followup message.
 *
 * @return The edited [FollowupMessage] of the interaction response, either [public][PublicFollowupMessage] or
 * [ephemeral][EphemeralFollowupMessage].
 *
 * @throws RestRequestException if something went wrong during the request.
 */
public suspend inline fun FollowupMessageBehavior.edit(
    builder: FollowupMessageModifyBuilder.() -> Unit,
): FollowupMessage {
    contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
    val response = kord.rest.interaction.modifyFollowupMessage(applicationId, token, id, builder)

    return FollowupMessage(Message(response.toData(), kord), applicationId, token, kord)
}
