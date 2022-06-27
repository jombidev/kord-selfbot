package dev.jombi.kordsb.core.behavior

import dev.jombi.kordsb.core.cache.data.ApplicationCommandData
import dev.jombi.kordsb.core.entity.application.ChatInputCommandCommand
import dev.jombi.kordsb.core.entity.application.GlobalChatInputCommand
import dev.jombi.kordsb.core.entity.application.GuildChatInputCommand
import dev.jombi.kordsb.rest.builder.interaction.ChatInputModifyBuilder


public interface ChatInputCommandBehavior : ApplicationCommandBehavior {

    public suspend fun edit(builder: suspend ChatInputModifyBuilder.() -> Unit): ChatInputCommandCommand

}


public interface GuildChatInputCommandBehavior : ChatInputCommandBehavior, GuildApplicationCommandBehavior {

    override suspend fun edit(builder: suspend ChatInputModifyBuilder.() -> Unit): GuildChatInputCommand {
        val response = service.modifyGuildChatInputApplicationCommand(applicationId, guildId, id) {
            builder()
        }
        val data = ApplicationCommandData.from(response)
        return GuildChatInputCommand(data, service)
    }
}


public interface GlobalChatInputCommandBehavior : ChatInputCommandBehavior, GlobalApplicationCommandBehavior {
    override suspend fun edit(builder: suspend ChatInputModifyBuilder.() -> Unit): GlobalChatInputCommand {
        val response = service.modifyGlobalChatInputApplicationCommand(applicationId, id) {
            builder()
        }
        val data = ApplicationCommandData.from(response)
        return GlobalChatInputCommand(data, service)
    }
}
