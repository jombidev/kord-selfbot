package dev.kord.core.gateway.handler

import dev.kord.cache.api.DataCache
import dev.kord.cache.api.put
import dev.kord.cache.api.remove
import dev.kord.core.Kord
import dev.kord.core.cache.data.ApplicationCommandData
import dev.kord.core.cache.data.GuildApplicationCommandPermissionData
import dev.kord.core.cache.data.GuildApplicationCommandPermissionsData
import dev.kord.core.cache.data.InteractionData
import dev.kord.core.cache.idEq
import dev.kord.core.entity.application.*
import dev.kord.core.entity.interaction.*
import dev.kord.core.event.interaction.*
import dev.kord.gateway.*
import kotlinx.coroutines.CoroutineScope
import dev.kord.core.event.Event as CoreEvent


public class InteractionEventHandler(
    cache: DataCache
) : BaseGatewayEventHandler(cache) {

    override suspend fun handle(event: Event, kord: Kord, coroutineScope: CoroutineScope): CoreEvent? =
        when (event) {
            is InteractionCreate -> handle(event, kord, coroutineScope)
            is ApplicationCommandCreate -> handle(event, kord, coroutineScope)
            is ApplicationCommandUpdate -> handle(event, kord, coroutineScope)
            is ApplicationCommandDelete -> handle(event, kord, coroutineScope)
            is ApplicationCommandPermissionsUpdate -> {
                val data = GuildApplicationCommandPermissionsData.from(event.permissions)
                ApplicationCommandPermissionsUpdateEvent(
                    ApplicationCommandPermissions(data),
                    kord, coroutineScope
                )
            }
            else -> null
        }

    private fun handle(event: InteractionCreate, kord: Kord, coroutineScope: CoroutineScope): CoreEvent {
        val data = InteractionData.from(event.interaction)
        val coreEvent = when(val interaction = Interaction.from(data, kord)) {
            is GlobalAutoCompleteInteraction -> GlobalAutoCompleteInteractionCreateEvent(kord, interaction, coroutineScope)
            is GlobalChatInputCommandInteraction -> GlobalChatInputCommandInteractionCreateEvent(interaction, kord, coroutineScope)
            is GlobalUserCommandInteraction -> GlobalUserCommandInteractionCreateEvent(interaction, kord, coroutineScope)
            is GlobalMessageCommandInteraction -> GlobalMessageCommandInteractionCreateEvent(interaction, kord, coroutineScope)
            is GlobalButtonInteraction -> GlobalButtonInteractionCreateEvent(interaction, kord, coroutineScope)
            is GlobalSelectMenuInteraction -> GlobalSelectMenuInteractionCreateEvent(interaction, kord, coroutineScope)
            is GlobalModalSubmitInteraction -> GlobalModalSubmitInteractionCreateEvent(interaction, kord, coroutineScope)
            is GuildAutoCompleteInteraction -> GuildAutoCompleteInteractionCreateEvent(kord, interaction, coroutineScope)
            is GuildChatInputCommandInteraction -> GuildChatInputCommandInteractionCreateEvent(interaction, kord, coroutineScope)
            is GuildMessageCommandInteraction -> GuildMessageCommandInteractionCreateEvent(interaction, kord, coroutineScope)
            is GuildUserCommandInteraction -> GuildUserCommandInteractionCreateEvent(interaction, kord, coroutineScope)
            is GuildButtonInteraction -> GuildButtonInteractionCreateEvent(interaction, kord, coroutineScope)
            is GuildSelectMenuInteraction -> GuildSelectMenuInteractionCreateEvent(interaction, kord, coroutineScope)
            is GuildModalSubmitInteraction -> GuildModalSubmitInteractionCreateEvent(interaction, kord, coroutineScope)
        }
        return coreEvent
    }

    private suspend fun handle(
        event: ApplicationCommandCreate,
        kord: Kord,
        coroutineScope: CoroutineScope
    ): CoreEvent {
        val data = ApplicationCommandData.from(event.application)
        cache.put(data)
        val coreEvent = when (val application = GuildApplicationCommand(data, kord.rest.interaction)) {
            is GuildChatInputCommand -> ChatInputCommandCreateEvent(application, kord, coroutineScope)
            is GuildMessageCommand -> MessageCommandCreateEvent(application, kord, coroutineScope)
            is GuildUserCommand -> UserCommandCreateEvent(application, kord, coroutineScope)
            is UnknownGuildApplicationCommand -> UnknownApplicationCommandCreateEvent(application, kord, coroutineScope)
        }
        return coreEvent
    }


    private suspend fun handle(
        event: ApplicationCommandUpdate,
        kord: Kord,
        coroutineScope: CoroutineScope
    ): CoreEvent {
        val data = ApplicationCommandData.from(event.application)
        cache.put(data)

        val coreEvent = when (val application = GuildApplicationCommand(data, kord.rest.interaction)) {
            is GuildChatInputCommand -> ChatInputCommandUpdateEvent(application, kord, coroutineScope)
            is GuildMessageCommand -> MessageCommandUpdateEvent(application, kord, coroutineScope)
            is GuildUserCommand -> UserCommandUpdateEvent(application, kord, coroutineScope)
            is UnknownGuildApplicationCommand -> UnknownApplicationCommandUpdateEvent(application, kord, coroutineScope)
        }
        return coreEvent
    }

    private suspend fun handle(
        event: ApplicationCommandDelete,
        kord: Kord,
        coroutineScope: CoroutineScope
    ): CoreEvent {
        val data = ApplicationCommandData.from(event.application)
        cache.remove<ApplicationCommandData> { idEq(ApplicationCommandData::id, data.id) }
        val coreEvent = when (val application = GuildApplicationCommand(data, kord.rest.interaction)) {
            is GuildChatInputCommand -> ChatInputCommandDeleteEvent(application, kord, coroutineScope)
            is GuildMessageCommand -> MessageCommandDeleteEvent(application, kord, coroutineScope)
            is GuildUserCommand -> UserCommandDeleteEvent(application, kord, coroutineScope)
            is UnknownGuildApplicationCommand -> UnknownApplicationCommandDeleteEvent(application, kord, coroutineScope)
        }
        return coreEvent
    }
}
