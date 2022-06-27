package dev.jombi.kordsb.core.entity.application

import dev.jombi.kordsb.common.entity.Snowflake
import dev.jombi.kordsb.core.behavior.*
import dev.jombi.kordsb.core.cache.data.ApplicationCommandData
import dev.jombi.kordsb.rest.service.InteractionService

public sealed interface UserCommand : ApplicationCommand, UserCommandBehavior

public class GlobalUserCommand(override val data: ApplicationCommandData, override val service: InteractionService) :
    GlobalApplicationCommand,
    UserCommand,
    GlobalUserCommandBehavior

public class GuildUserCommand(override val data: ApplicationCommandData, override val service: InteractionService) :
    GuildApplicationCommand,
    UserCommand,
    GuildUserCommandBehavior {
    override val guildId: Snowflake
        get() = data.guildId.value!!
}
