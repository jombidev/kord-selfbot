package dev.jombi.kordsb.core.event.interaction

import dev.jombi.kordsb.core.Kord
import dev.jombi.kordsb.core.entity.application.*
import dev.jombi.kordsb.core.event.Event

public sealed interface ApplicationCommandCreateEvent : Event {
    public val command: GuildApplicationCommand
}

public class ChatInputCommandCreateEvent(
    override val command: GuildChatInputCommand,
    override val kord: Kord,
    override val customContext: Any?,
) : ApplicationCommandCreateEvent


public class UserCommandCreateEvent(
    override val command: GuildUserCommand,
    override val kord: Kord,
    override val customContext: Any?,
) : ApplicationCommandCreateEvent


public class MessageCommandCreateEvent(
    override val command: GuildMessageCommand,
    override val kord: Kord,
    override val customContext: Any?,
) : ApplicationCommandCreateEvent


public class UnknownApplicationCommandCreateEvent(
    override val command: UnknownGuildApplicationCommand,
    override val kord: Kord,
    override val customContext: Any?,
) : ApplicationCommandCreateEvent
