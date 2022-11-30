package dev.jombi.kordsb.core.event.interaction

import dev.jombi.kordsb.core.Kord
import dev.jombi.kordsb.core.entity.interaction.AutoCompleteInteraction
import dev.jombi.kordsb.core.entity.interaction.GlobalAutoCompleteInteraction
import dev.jombi.kordsb.core.entity.interaction.GuildAutoCompleteInteraction
import dev.jombi.kordsb.core.event.Event

/** An [Event] that fires when an [AutoCompleteInteraction] is created. */
public sealed interface AutoCompleteInteractionCreateEvent : DataInteractionCreateEvent {
    override val interaction: AutoCompleteInteraction
}

/** An [Event] that fires when a [GlobalAutoCompleteInteraction] is created. */
public class GlobalAutoCompleteInteractionCreateEvent(
    override val kord: Kord,
    override val interaction: GlobalAutoCompleteInteraction,
    override val customContext: Any?,
) : AutoCompleteInteractionCreateEvent

/** An [Event] that fires when a [GuildAutoCompleteInteraction] is created. */
public class GuildAutoCompleteInteractionCreateEvent(
    override val kord: Kord,
    override val interaction: GuildAutoCompleteInteraction,
    override val customContext: Any?,
) : AutoCompleteInteractionCreateEvent
