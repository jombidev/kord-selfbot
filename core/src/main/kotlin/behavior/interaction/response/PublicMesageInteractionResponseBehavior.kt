package dev.jombi.kordsb.core.behavior.interaction.response

import dev.jombi.kordsb.common.entity.Snowflake
import dev.jombi.kordsb.core.Kord
import dev.jombi.kordsb.core.behavior.interaction.*
import dev.jombi.kordsb.core.entity.interaction.response.PublicMessageInteractionResponse
import dev.jombi.kordsb.core.supplier.EntitySupplier
import dev.jombi.kordsb.core.supplier.EntitySupplyStrategy
import dev.jombi.kordsb.rest.builder.message.modify.InteractionResponseModifyBuilder
import dev.jombi.kordsb.rest.request.RestRequestException
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

/**
 * An [InteractionResponseBehavior] returned when using [respondPublic][ActionInteractionBehavior.respondPublic],
 * [respond][DeferredPublicMessageInteractionResponseBehavior.respond],
 * [deferPublicMessageUpdate][ComponentInteractionBehavior.deferPublicMessageUpdate] or
 * [updatePublicMessage][ComponentInteractionBehavior.updatePublicMessage].
 *
 * This is the handle to a public message, it supports [editing][PublicMessageInteractionResponseBehavior.edit],
 * [deleting][delete] and sending followup messages to the interaction.
 *
 * The message is visible to all users in the [channel][InteractionBehavior.channel] the interaction was sent from.
 */
public interface PublicMessageInteractionResponseBehavior :
    PublicInteractionResponseBehavior,
    MessageInteractionResponseBehavior {

    /**
     * Requests to delete the message.
     *
     * This [PublicMessageInteractionResponseBehavior] can still be used to send followup messages to the interaction.
     *
     * @throws RestRequestException if something went wrong during the request.
     */
    public suspend fun delete() {
        kord.rest.interaction.deleteOriginalInteractionResponse(applicationId, token)
    }

    override fun withStrategy(strategy: EntitySupplyStrategy<*>): PublicMessageInteractionResponseBehavior =
        PublicMessageInteractionResponseBehavior(applicationId, token, kord, strategy.supply(kord))
}

public fun PublicMessageInteractionResponseBehavior(
    applicationId: Snowflake,
    token: String,
    kord: Kord,
    supplier: EntitySupplier = kord.defaultSupplier,
): PublicMessageInteractionResponseBehavior = object : PublicMessageInteractionResponseBehavior {
    override val applicationId: Snowflake = applicationId
    override val token: String = token
    override val kord: Kord = kord
    override val supplier: EntitySupplier = supplier
}

/**
 * Requests to edit this [PublicMessageInteractionResponseBehavior].
 *
 * @return The edited [PublicMessageInteractionResponse].
 *
 * @throws RestRequestException if something went wrong during the request.
 */
public suspend inline fun PublicMessageInteractionResponseBehavior.edit(
    builder: InteractionResponseModifyBuilder.() -> Unit,
): PublicMessageInteractionResponse {
    contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }

    return editPublicOriginalResponse(builder)
}
