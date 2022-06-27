package dev.jombi.kordsb.core.behavior.interaction.response

import dev.jombi.kordsb.core.behavior.interaction.InteractionBehavior
import dev.jombi.kordsb.core.entity.interaction.Interaction
import dev.jombi.kordsb.core.supplier.EntitySupplyStrategy

/**
 * An [InteractionResponseBehavior] for a public response to an [Interaction].
 *
 * The response is visible to all users in the [channel][InteractionBehavior.channel] the interaction was sent from.
 */
public sealed interface PublicInteractionResponseBehavior : InteractionResponseBehavior {

    override fun withStrategy(strategy: EntitySupplyStrategy<*>): PublicInteractionResponseBehavior
}
