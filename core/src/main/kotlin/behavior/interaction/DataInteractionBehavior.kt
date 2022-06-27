package dev.jombi.kordsb.core.behavior.interaction

import dev.jombi.kordsb.core.entity.interaction.DataInteraction
import dev.jombi.kordsb.core.supplier.EntitySupplyStrategy

/** The behavior of a [DataInteraction]. */
public interface DataInteractionBehavior : InteractionBehavior {

    override fun withStrategy(strategy: EntitySupplyStrategy<*>): DataInteractionBehavior
}
