package dev.jombi.kordsb.core.behavior.interaction

import dev.jombi.kordsb.core.behavior.interaction.response.PopupInteractionResponseBehavior
import dev.jombi.kordsb.core.entity.interaction.ActionInteraction
import dev.jombi.kordsb.core.supplier.EntitySupplyStrategy
import dev.jombi.kordsb.rest.builder.interaction.ModalBuilder
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

/**
 * The behavior of an [ActionInteraction] that can be responded to with a popup modal.
 *
 * @see modal
 */
public interface ModalParentInteractionBehavior : ActionInteractionBehavior {

    override fun withStrategy(strategy: EntitySupplyStrategy<*>): ModalParentInteractionBehavior
}

/** Responds to the interaction with a popup modal. */
public suspend inline fun ModalParentInteractionBehavior.modal(
    title: String,
    customId: String,
    builder: ModalBuilder.() -> Unit,
): PopupInteractionResponseBehavior {
    contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }

    kord.rest.interaction.createModalInteractionResponse(id, token, title, customId, builder)

    return PopupInteractionResponseBehavior(applicationId, token, kord)
}
