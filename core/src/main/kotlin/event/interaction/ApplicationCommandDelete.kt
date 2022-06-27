package dev.jombi.kordsb.core.event.interaction

import dev.jombi.kordsb.core.Kord
import dev.jombi.kordsb.core.entity.application.*
import dev.jombi.kordsb.core.event.Event
import dev.jombi.kordsb.core.event.kordCoroutineScope
import kotlinx.coroutines.CoroutineScope
import kotlin.coroutines.CoroutineContext


public sealed interface ApplicationCommandDeleteEvent : Event {
    public val command: GuildApplicationCommand
}

public class ChatInputCommandDeleteEvent(
    override val command: GuildChatInputCommand,
    override val kord: Kord,
    public val coroutineScope: CoroutineScope = kordCoroutineScope(kord)
) : ApplicationCommandDeleteEvent, CoroutineScope by coroutineScope


public class UserCommandDeleteEvent(
    override val command: GuildUserCommand,
    override val kord: Kord,
    public val coroutineScope: CoroutineScope = kordCoroutineScope(kord)
) : ApplicationCommandDeleteEvent, CoroutineScope by coroutineScope


public class MessageCommandDeleteEvent(
    override val command: GuildMessageCommand,
    override val kord: Kord,
    public  val coroutineScope: CoroutineScope = kordCoroutineScope(kord)
) : ApplicationCommandDeleteEvent, CoroutineScope by coroutineScope


public class UnknownApplicationCommandDeleteEvent(
    override val command: UnknownGuildApplicationCommand,
    override val kord: Kord,
    public val coroutineScope: CoroutineScope = kordCoroutineScope(kord)
) : ApplicationCommandDeleteEvent, CoroutineScope by coroutineScope
