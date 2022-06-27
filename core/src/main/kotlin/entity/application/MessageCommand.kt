package dev.jombi.kordsb.core.entity.application

import dev.jombi.kordsb.common.entity.Snowflake
import dev.jombi.kordsb.core.behavior.GlobalMessageCommandBehavior
import dev.jombi.kordsb.core.behavior.GuildMessageCommandBehavior
import dev.jombi.kordsb.core.behavior.MessageCommandBehavior
import dev.jombi.kordsb.core.cache.data.ApplicationCommandData
import dev.jombi.kordsb.rest.service.InteractionService

public sealed interface MessageCommand : ApplicationCommand, MessageCommandBehavior

public class GlobalMessageCommand(override val data: ApplicationCommandData, override val service: InteractionService) :
    GlobalApplicationCommand,
    MessageCommand,
    GlobalMessageCommandBehavior

public class GuildMessageCommand(override val data: ApplicationCommandData, override val service: InteractionService) :
    GuildApplicationCommand,
    MessageCommand,
    GuildMessageCommandBehavior {
    override val guildId: Snowflake
        get() = data.guildId.value!!
}
