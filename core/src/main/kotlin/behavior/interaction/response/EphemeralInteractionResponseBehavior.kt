package dev.jombi.kordsb.core.behavior.interaction.response

import dev.jombi.kordsb.core.entity.interaction.Interaction
import dev.jombi.kordsb.core.supplier.EntitySupplyStrategy

/**
 * An [InteractionResponseBehavior] for an ephemeral response to an [Interaction].
 *
 * The response is only visible to the [user][Interaction.user] who invoked the interaction.
 */
public sealed interface EphemeralInteractionResponseBehavior : InteractionResponseBehavior {

    override fun withStrategy(strategy: EntitySupplyStrategy<*>): EphemeralInteractionResponseBehavior
}
