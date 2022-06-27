package dev.jombi.kordsb.core.behavior.interaction

import dev.jombi.kordsb.core.entity.interaction.GlobalInteraction
import dev.jombi.kordsb.core.supplier.EntitySupplyStrategy

/** The behavior of a [GlobalInteraction]. */
public interface GlobalInteractionBehavior : InteractionBehavior {

    override fun withStrategy(strategy: EntitySupplyStrategy<*>): GlobalInteractionBehavior
}
