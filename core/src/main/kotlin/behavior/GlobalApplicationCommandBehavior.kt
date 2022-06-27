package dev.jombi.kordsb.core.behavior

import dev.jombi.kordsb.common.entity.Snowflake
import dev.jombi.kordsb.core.entity.Entity
import dev.jombi.kordsb.rest.request.RestRequestException
import dev.jombi.kordsb.rest.service.InteractionService

/**
 * The behavior of an [Application Command](https://discord.com/developers/docs/interactions/application-commands).
 */
public interface ApplicationCommandBehavior : Entity {
    public val applicationId: Snowflake
    public val service: InteractionService

    /**
     * Requests to delete this command.
     *
     * @throws [RestRequestException] when something goes wrong during the request.
     */
    public suspend fun delete()

}


/**
 *  The behavior of an [Application Command](https://discord.com/developers/docs/interactions/application-commands) that can be used in DMs and Guilds.
 */
public interface GlobalApplicationCommandBehavior : ApplicationCommandBehavior {

    override suspend fun delete() {
        service.deleteGlobalApplicationCommand(applicationId, id)
    }
}


/**
 * The behavior of [Application Command][dev.jombi.kordsb.core.entity.application.GuildApplicationCommand].
 */
public interface GuildApplicationCommandBehavior : ApplicationCommandBehavior {
    public val guildId: Snowflake

    override suspend fun delete() {
        service.deleteGuildApplicationCommand(applicationId, guildId, id)
    }

}


public fun GuildApplicationCommandBehavior(
    guildId: Snowflake,
    applicationId: Snowflake,
    id: Snowflake,
    service: InteractionService
): GuildApplicationCommandBehavior = object : GuildApplicationCommandBehavior {
    override val guildId: Snowflake
        get() = guildId
    override val applicationId: Snowflake
        get() = applicationId
    override val service: InteractionService
        get() = service
    override val id: Snowflake
        get() = id
}


public fun GlobalApplicationCommandBehavior(
    applicationId: Snowflake,
    id: Snowflake,
    service: InteractionService
): GlobalApplicationCommandBehavior = object : GlobalApplicationCommandBehavior {
    override val applicationId: Snowflake
        get() = applicationId
    override val service: InteractionService
        get() = service
    override val id: Snowflake
        get() = id
}
