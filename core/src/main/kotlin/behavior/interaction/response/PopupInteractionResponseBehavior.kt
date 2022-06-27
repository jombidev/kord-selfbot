package dev.jombi.kordsb.core.behavior.interaction.response

import dev.jombi.kordsb.common.entity.Snowflake
import dev.jombi.kordsb.core.Kord
import dev.jombi.kordsb.core.behavior.interaction.ModalParentInteractionBehavior
import dev.jombi.kordsb.core.behavior.interaction.modal
import dev.jombi.kordsb.core.supplier.EntitySupplier
import dev.jombi.kordsb.core.supplier.EntitySupplyStrategy

/**
 * An [InteractionResponseBehavior] returned when using [ModalParentInteractionBehavior.modal].
 *
 * This handle supports sending followup messages to the interaction.
 */
public interface PopupInteractionResponseBehavior : FollowupPermittingInteractionResponseBehavior {

    override fun withStrategy(strategy: EntitySupplyStrategy<*>): PopupInteractionResponseBehavior =
        PopupInteractionResponseBehavior(applicationId, token, kord, strategy.supply(kord))
}

public fun PopupInteractionResponseBehavior(
    applicationId: Snowflake,
    token: String,
    kord: Kord,
    supplier: EntitySupplier = kord.defaultSupplier,
): PopupInteractionResponseBehavior = object : PopupInteractionResponseBehavior {
    override val applicationId: Snowflake = applicationId
    override val token: String = token
    override val kord: Kord = kord
    override val supplier: EntitySupplier = supplier
}
