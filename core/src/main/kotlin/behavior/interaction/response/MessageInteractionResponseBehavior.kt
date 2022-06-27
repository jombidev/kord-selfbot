package dev.jombi.kordsb.core.behavior.interaction.response

import dev.jombi.kordsb.core.behavior.interaction.*
import dev.jombi.kordsb.core.entity.interaction.response.MessageInteractionResponse
import dev.jombi.kordsb.core.supplier.EntitySupplyStrategy
import dev.jombi.kordsb.rest.builder.message.modify.InteractionResponseModifyBuilder
import dev.jombi.kordsb.rest.request.RestRequestException
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

/**
 * An [InteractionResponseBehavior] returned when using [respondPublic][ActionInteractionBehavior.respondPublic],
 * [respondEphemeral][ActionInteractionBehavior.respondEphemeral],
 * [respond][DeferredMessageInteractionResponseBehavior.respond],
 * [deferPublicMessageUpdate][ComponentInteractionBehavior.deferPublicMessageUpdate],
 * [deferEphemeralMessageUpdate][ComponentInteractionBehavior.deferEphemeralMessageUpdate],
 * [updatePublicMessage][ComponentInteractionBehavior.updatePublicMessage] or
 * [updateEphemeralMessage][ComponentInteractionBehavior.updateEphemeralMessage].
 *
 * This is the handle to a message, it supports [editing][MessageInteractionResponseBehavior.edit] and sending followup
 * messages to the interaction.
 */
public interface MessageInteractionResponseBehavior : FollowupPermittingInteractionResponseBehavior {

    override fun withStrategy(strategy: EntitySupplyStrategy<*>): MessageInteractionResponseBehavior
}

/**
 * Requests to edit this [MessageInteractionResponseBehavior].
 *
 * @return The edited [MessageInteractionResponse].
 *
 * @throws RestRequestException if something went wrong during the request.
 */
public suspend inline fun MessageInteractionResponseBehavior.edit(
    builder: InteractionResponseModifyBuilder.() -> Unit,
): MessageInteractionResponse {
    contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }

    return editOriginalResponseWithUnknownVisibility(builder)
}
