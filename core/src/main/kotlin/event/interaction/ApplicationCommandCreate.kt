package dev.jombi.kordsb.core.event.interaction

import dev.jombi.kordsb.core.Kord
import dev.jombi.kordsb.core.entity.application.*
import dev.jombi.kordsb.core.event.Event
import dev.jombi.kordsb.core.event.kordCoroutineScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.job
import kotlin.coroutines.CoroutineContext


public sealed interface ApplicationCommandCreateEvent : Event {
    public val command: GuildApplicationCommand
}

public class ChatInputCommandCreateEvent(
    override val command: GuildChatInputCommand,
    override val kord: Kord,
    public val coroutineScope: CoroutineScope = CoroutineScope(kord.coroutineContext + SupervisorJob(kord.coroutineContext.job)),
) : ApplicationCommandCreateEvent, CoroutineScope by coroutineScope


public class UserCommandCreateEvent(
    override val command: GuildUserCommand,
    override val kord: Kord,
    public val coroutineScope: CoroutineScope = kordCoroutineScope(kord)
) : ApplicationCommandCreateEvent, CoroutineScope by coroutineScope


public class MessageCommandCreateEvent(
    override val command: GuildMessageCommand,
    override val kord: Kord,
    public val coroutineScope: CoroutineScope = kordCoroutineScope(kord)
) : ApplicationCommandCreateEvent, CoroutineScope by coroutineScope


public class UnknownApplicationCommandCreateEvent(
    override val command: UnknownGuildApplicationCommand,
    override val kord: Kord,
    public val coroutineScope: CoroutineScope = kordCoroutineScope(kord)
) : ApplicationCommandCreateEvent, CoroutineScope by coroutineScope
