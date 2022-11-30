package dev.jombi.kordsb.core.event.interaction

import dev.jombi.kordsb.core.Kord
import dev.jombi.kordsb.core.entity.interaction.GlobalModalSubmitInteraction
import dev.jombi.kordsb.core.entity.interaction.GuildModalSubmitInteraction
import dev.jombi.kordsb.core.entity.interaction.ModalSubmitInteraction
import dev.jombi.kordsb.core.event.Event

/** An [Event] that fires when a [ModalSubmitInteraction] is created. */
public sealed interface ModalSubmitInteractionCreateEvent : ActionInteractionCreateEvent {
    override val interaction: ModalSubmitInteraction
}

/** An [Event] that fires when a [GuildModalSubmitInteraction] is created. */
public class GuildModalSubmitInteractionCreateEvent(
    override val interaction: GuildModalSubmitInteraction,
    override val kord: Kord,
    override val customContext: Any?,
) : ModalSubmitInteractionCreateEvent

/** An [Event] that fires when a [GlobalModalSubmitInteraction] is created. */
public class GlobalModalSubmitInteractionCreateEvent(
    override val interaction: GlobalModalSubmitInteraction,
    override val kord: Kord,
    override val customContext: Any?,
) : ModalSubmitInteractionCreateEvent
