package dev.jombi.kordsb.core.entity.interaction

import dev.jombi.kordsb.core.Kord
import dev.jombi.kordsb.core.cache.data.InteractionData
import dev.jombi.kordsb.core.entity.Guild
import dev.jombi.kordsb.core.entity.component.ButtonComponent
import dev.jombi.kordsb.core.supplier.EntitySupplier
import dev.jombi.kordsb.core.supplier.EntitySupplyStrategy

/** A [ComponentInteraction] created when a user presses a [button][ButtonComponent]. */
public sealed interface ButtonInteraction : ComponentInteraction {

    override val component: ButtonComponent
        get() = message.actionRows.firstNotNullOf { it.interactionButtons[componentId] }

    override fun withStrategy(strategy: EntitySupplyStrategy<*>): ButtonInteraction
}

/** A [ButtonInteraction] that took place in a global context (e.g. a DM). */
public class GlobalButtonInteraction(
    override val data: InteractionData,
    override val kord: Kord,
    override val supplier: EntitySupplier
) : ButtonInteraction, GlobalComponentInteraction {

    override fun withStrategy(strategy: EntitySupplyStrategy<*>): GlobalButtonInteraction =
        GlobalButtonInteraction(data, kord, strategy.supply(kord))

    override fun equals(other: Any?): Boolean = other is GlobalButtonInteraction && this.id == other.id
    override fun hashCode(): Int = id.hashCode()
    override fun toString(): String =
        "GlobalButtonInteraction(data=$data, kord=$kord, supplier=$supplier)"
}

/** A [ButtonInteraction] that took place in the context of a [Guild]. */
public class GuildButtonInteraction(
    override val data: InteractionData,
    override val kord: Kord,
    override val supplier: EntitySupplier
) : ButtonInteraction, GuildComponentInteraction {

    override fun withStrategy(strategy: EntitySupplyStrategy<*>): GuildButtonInteraction =
        GuildButtonInteraction(data, kord, strategy.supply(kord))

    override fun equals(other: Any?): Boolean = other is GuildButtonInteraction && this.id == other.id
    override fun hashCode(): Int = id.hashCode()
    override fun toString(): String =
        "GuildButtonInteraction(data=$data, kord=$kord, supplier=$supplier)"
}
