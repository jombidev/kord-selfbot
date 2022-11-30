package dev.jombi.kordsb.core.gateway.handler

import dev.jombi.kordsb.core.Kord
import dev.jombi.kordsb.core.cache.data.ApplicationCommandData
import dev.jombi.kordsb.core.cache.data.GuildApplicationCommandPermissionsData
import dev.jombi.kordsb.core.cache.data.InteractionData
import dev.jombi.kordsb.core.cache.idEq
import dev.jombi.kordsb.core.entity.application.*
import dev.jombi.kordsb.core.entity.interaction.*
import dev.jombi.kordsb.core.event.interaction.*
import dev.jombi.kordsb.gateway.*
import dev.kord.cache.api.put
import dev.kord.cache.api.remove
import dev.jombi.kordsb.core.event.Event as CoreEvent


internal class InteractionEventHandler : BaseGatewayEventHandler() {

    override suspend fun handle(event: Event, kord: Kord, context: LazyContext?): CoreEvent? =
        when (event) {
            is InteractionCreate -> handle(event, kord, context)
            is ApplicationCommandCreate -> handle(event, kord, context)
            is ApplicationCommandUpdate -> handle(event, kord, context)
            is ApplicationCommandDelete -> handle(event, kord, context)
            is ApplicationCommandPermissionsUpdate -> {
                val data = GuildApplicationCommandPermissionsData.from(event.permissions)
                ApplicationCommandPermissionsUpdateEvent(ApplicationCommandPermissions(data), kord, context?.get())
            }
            else -> null
        }

    private suspend fun handle(event: InteractionCreate, kord: Kord, context: LazyContext?): CoreEvent {
        val data = InteractionData.from(event.interaction)
        val coreEvent = when(val interaction = Interaction.from(data, kord)) {
            is GlobalAutoCompleteInteraction -> GlobalAutoCompleteInteractionCreateEvent(kord, interaction, context?.get())
            is GlobalChatInputCommandInteraction -> GlobalChatInputCommandInteractionCreateEvent(interaction, kord, context?.get())
            is GlobalUserCommandInteraction -> GlobalUserCommandInteractionCreateEvent(interaction, kord, context?.get())
            is GlobalMessageCommandInteraction -> GlobalMessageCommandInteractionCreateEvent(interaction, kord, context?.get())
            is GlobalButtonInteraction -> GlobalButtonInteractionCreateEvent(interaction, kord, context?.get())
            is GlobalSelectMenuInteraction -> GlobalSelectMenuInteractionCreateEvent(interaction, kord, context?.get())
            is GlobalModalSubmitInteraction -> GlobalModalSubmitInteractionCreateEvent(interaction, kord, context?.get())
            is GuildAutoCompleteInteraction -> GuildAutoCompleteInteractionCreateEvent(kord, interaction, context?.get())
            is GuildChatInputCommandInteraction -> GuildChatInputCommandInteractionCreateEvent(interaction, kord, context?.get())
            is GuildMessageCommandInteraction -> GuildMessageCommandInteractionCreateEvent(interaction, kord, context?.get())
            is GuildUserCommandInteraction -> GuildUserCommandInteractionCreateEvent(interaction, kord, context?.get())
            is GuildButtonInteraction -> GuildButtonInteractionCreateEvent(interaction, kord, context?.get())
            is GuildSelectMenuInteraction -> GuildSelectMenuInteractionCreateEvent(interaction, kord, context?.get())
            is GuildModalSubmitInteraction -> GuildModalSubmitInteractionCreateEvent(interaction, kord, context?.get())
        }
        return coreEvent
    }

    private suspend fun handle(
        event: ApplicationCommandCreate,
        kord: Kord,
        context: LazyContext?,
    ): CoreEvent {
        val data = ApplicationCommandData.from(event.application)
        kord.cache.put(data)
        val coreEvent = when (val application = GuildApplicationCommand(data, kord.rest.interaction)) {
            is GuildChatInputCommand -> ChatInputCommandCreateEvent(application, kord, context?.get())
            is GuildMessageCommand -> MessageCommandCreateEvent(application, kord, context?.get())
            is GuildUserCommand -> UserCommandCreateEvent(application, kord, context?.get())
            is UnknownGuildApplicationCommand -> UnknownApplicationCommandCreateEvent(application, kord, context?.get())
        }
        return coreEvent
    }


    private suspend fun handle(
        event: ApplicationCommandUpdate,
        kord: Kord,
        context: LazyContext?,
    ): CoreEvent {
        val data = ApplicationCommandData.from(event.application)
        kord.cache.put(data)

        val coreEvent = when (val application = GuildApplicationCommand(data, kord.rest.interaction)) {
            is GuildChatInputCommand -> ChatInputCommandUpdateEvent(application, kord, context?.get())
            is GuildMessageCommand -> MessageCommandUpdateEvent(application, kord, context?.get())
            is GuildUserCommand -> UserCommandUpdateEvent(application, kord, context?.get())
            is UnknownGuildApplicationCommand -> UnknownApplicationCommandUpdateEvent(application, kord, context?.get())
        }
        return coreEvent
    }

    private suspend fun handle(
        event: ApplicationCommandDelete,
        kord: Kord,
        context: LazyContext?,
    ): CoreEvent {
        val data = ApplicationCommandData.from(event.application)
        kord.cache.remove<ApplicationCommandData> { idEq(ApplicationCommandData::id, data.id) }
        val coreEvent = when (val application = GuildApplicationCommand(data, kord.rest.interaction)) {
            is GuildChatInputCommand -> ChatInputCommandDeleteEvent(application, kord, context?.get())
            is GuildMessageCommand -> MessageCommandDeleteEvent(application, kord, context?.get())
            is GuildUserCommand -> UserCommandDeleteEvent(application, kord, context?.get())
            is UnknownGuildApplicationCommand -> UnknownApplicationCommandDeleteEvent(application, kord, context?.get())
        }
        return coreEvent
    }
}
