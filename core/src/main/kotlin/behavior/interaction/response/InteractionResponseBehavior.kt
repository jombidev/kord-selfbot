package dev.jombi.kordsb.core.behavior.interaction.response

import dev.jombi.kordsb.common.entity.Snowflake
import dev.jombi.kordsb.core.KordObject
import dev.jombi.kordsb.core.cache.data.toData
import dev.jombi.kordsb.core.entity.Message
import dev.jombi.kordsb.core.entity.Strategizable
import dev.jombi.kordsb.core.entity.interaction.Interaction
import dev.jombi.kordsb.core.entity.interaction.followup.EphemeralFollowupMessage
import dev.jombi.kordsb.core.entity.interaction.followup.FollowupMessage
import dev.jombi.kordsb.core.entity.interaction.followup.PublicFollowupMessage
import dev.jombi.kordsb.core.exception.EntityNotFoundException
import dev.jombi.kordsb.core.supplier.EntitySupplyStrategy
import dev.jombi.kordsb.rest.builder.message.create.FollowupMessageCreateBuilder
import dev.jombi.kordsb.rest.request.RestRequestException
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

/**
 * A handle for operations that can follow an
 * [Interaction Response](https://discord.com/developers/docs/interactions/receiving-and-responding#responding-to-an-interaction).
 */
public sealed interface InteractionResponseBehavior : KordObject, Strategizable {

    /**
     * Copied from the [Interaction] the response is for.
     * @see [Interaction.applicationId].
     */
    public val applicationId: Snowflake

    /**
     * Copied from the [Interaction] the response is for.
     * @see [Interaction.token].
     */
    public val token: String

    /**
     * Returns a followup message for an interaction response or `null` if it was not found.
     *
     * @throws RestRequestException if something went wrong during the request.
     */
    public suspend fun getFollowupMessageOrNull(messageId: Snowflake): FollowupMessage? =
        supplier.getFollowupMessageOrNull(applicationId, token, messageId)

    /**
     * Returns a followup message for an interaction response.
     *
     * @throws RestRequestException if something went wrong during the request.
     * @throws EntityNotFoundException if the followup message was not found.
     */
    public suspend fun getFollowupMessage(messageId: Snowflake): FollowupMessage =
        supplier.getFollowupMessage(applicationId, token, messageId)

    override fun withStrategy(strategy: EntitySupplyStrategy<*>): InteractionResponseBehavior
}

/**
 * Follows up an interaction response without the [Ephemeral flag][dev.jombi.kordsb.common.entity.MessageFlag.Ephemeral].
 */
@Deprecated(
    "Followups are no longer supported for all 'InteractionResponseBehavior' types.",
    ReplaceWith(
        "if (this is FollowupPermittingInteractionResponseBehavior) this.createPublicFollowup { builder() }",
        "dev.jombi.kordsb.core.behavior.interaction.response.FollowupPermittingInteractionResponseBehavior",
        "dev.jombi.kordsb.core.behavior.interaction.response.createPublicFollowup",
    ),
    DeprecationLevel.HIDDEN,
)
public suspend inline fun InteractionResponseBehavior.followUp(builder: FollowupMessageCreateBuilder.() -> Unit): PublicFollowupMessage {
    contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
    val message = kord.rest.interaction.createFollowupMessage(applicationId, token, ephemeral = false, builder)
    return PublicFollowupMessage(Message(message.toData(), kord), applicationId, token, kord)
}

/**
 * Follows up an interaction response with the [Ephemeral flag][dev.jombi.kordsb.common.entity.MessageFlag.Ephemeral].
 */
@Deprecated(
    "Followups are no longer supported for all 'InteractionResponseBehavior' types.",
    ReplaceWith(
        "if (this is FollowupPermittingInteractionResponseBehavior) this.createEphemeralFollowup { builder() }",
        "dev.jombi.kordsb.core.behavior.interaction.response.FollowupPermittingInteractionResponseBehavior",
        "dev.jombi.kordsb.core.behavior.interaction.response.createEphemeralFollowup",
    ),
    DeprecationLevel.HIDDEN,
)
public suspend inline fun InteractionResponseBehavior.followUpEphemeral(builder: FollowupMessageCreateBuilder.() -> Unit): EphemeralFollowupMessage {
    contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
    val message = kord.rest.interaction.createFollowupMessage(applicationId, token, ephemeral = true, builder)
    return EphemeralFollowupMessage(Message(message.toData(), kord), applicationId, token, kord)
}
