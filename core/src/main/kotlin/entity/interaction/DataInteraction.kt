package dev.jombi.kordsb.core.entity.interaction

import dev.jombi.kordsb.core.behavior.interaction.DataInteractionBehavior
import dev.jombi.kordsb.core.supplier.EntitySupplyStrategy

/**
 * An [Interaction] created when Discord requests some form of data (e.g. for auto-complete).
 *
 * @see ActionInteraction
 */
public sealed interface DataInteraction : Interaction, DataInteractionBehavior {

    override fun withStrategy(strategy: EntitySupplyStrategy<*>): DataInteraction
}
