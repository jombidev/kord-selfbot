package dev.jombi.kordsb.core.entity.interaction

import dev.jombi.kordsb.core.behavior.interaction.GlobalInteractionBehavior
import dev.jombi.kordsb.core.entity.User
import dev.jombi.kordsb.core.supplier.EntitySupplyStrategy

/** An [Interaction] that took place in a global context (e.g. a DM). */
public sealed interface GlobalInteraction : Interaction, GlobalInteractionBehavior {

    override val user: User get() = User(data.user.value!!, kord)

    override fun withStrategy(strategy: EntitySupplyStrategy<*>): GlobalInteraction
}
