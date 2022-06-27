package dev.jombi.kordsb.core.behavior

import dev.jombi.kordsb.core.cache.data.ApplicationCommandData
import dev.jombi.kordsb.core.entity.application.GlobalMessageCommand
import dev.jombi.kordsb.core.entity.application.GuildMessageCommand
import dev.jombi.kordsb.core.entity.application.MessageCommand
import dev.jombi.kordsb.rest.builder.interaction.MessageCommandModifyBuilder


public interface MessageCommandBehavior : ApplicationCommandBehavior {

    public suspend fun edit(builder: suspend MessageCommandModifyBuilder.() -> Unit): MessageCommand
}


public interface GuildMessageCommandBehavior : MessageCommandBehavior, GuildApplicationCommandBehavior {
    override suspend fun edit(builder: suspend MessageCommandModifyBuilder.() -> Unit): GuildMessageCommand {
        val response = service.modifyGuildMessageApplicationCommand(applicationId, guildId, id) { builder() }
        val data = ApplicationCommandData.from(response)
        return GuildMessageCommand(data, service)
    }
}


public interface GlobalMessageCommandBehavior : MessageCommandBehavior, GlobalApplicationCommandBehavior {
    override suspend fun edit(builder: suspend MessageCommandModifyBuilder.() -> Unit): GlobalMessageCommand {
        val response = service.modifyGlobalMessageApplicationCommand(applicationId, id) {
            builder()
        }
        val data = ApplicationCommandData.from(response)
        return GlobalMessageCommand(data, service)
    }
}
