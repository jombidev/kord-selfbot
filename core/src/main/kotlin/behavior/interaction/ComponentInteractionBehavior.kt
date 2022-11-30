package dev.jombi.kordsb.core.behavior.interaction

import dev.jombi.kordsb.common.entity.InteractionResponseType
import dev.jombi.kordsb.common.entity.MessageFlag
import dev.jombi.kordsb.common.entity.MessageFlags
import dev.jombi.kordsb.common.entity.Snowflake
import dev.jombi.kordsb.common.entity.optional.Optional
import dev.jombi.kordsb.core.Kord
import dev.jombi.kordsb.core.behavior.interaction.response.EphemeralMessageInteractionResponseBehavior
import dev.jombi.kordsb.core.behavior.interaction.response.PublicMessageInteractionResponseBehavior
import dev.jombi.kordsb.core.behavior.interaction.response.edit
import dev.jombi.kordsb.core.entity.interaction.ComponentInteraction
import dev.jombi.kordsb.core.entity.interaction.ModalSubmitInteraction
import dev.jombi.kordsb.core.supplier.EntitySupplier
import dev.jombi.kordsb.core.supplier.EntitySupplyStrategy
import dev.jombi.kordsb.rest.builder.message.create.UpdateMessageInteractionResponseCreateBuilder
import dev.jombi.kordsb.rest.json.request.InteractionApplicationCommandCallbackData
import dev.jombi.kordsb.rest.json.request.InteractionResponseCreateRequest
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

/**
 * The behavior of a [ComponentInteraction] or a [ModalSubmitInteraction] that contains a
 * [message][ModalSubmitInteraction.message].
 */
public interface ComponentInteractionBehavior : ActionInteractionBehavior {
// this can not be a ModalParentInteractionBehavior since ModalSubmitInteractions implement ComponentInteractionBehavior
// but can not be responded to with another modal (yet?)

    /**
     * Acknowledges a component interaction publicly with the intent of updating it later.
     *
     * There is no requirement to actually update the message later, calling this is
     * sufficient to handle the interaction and stops any 'loading' animations in the client.
     *
     * There is no noticeable difference between this and [acknowledgeEphemeralDeferredMessageUpdate]
     * when it comes to acknowledging the interaction, both functions can be called
     * on public and ephemeral messages.
     *
     * @suppress
     */
    @Deprecated(
        "Renamed to 'deferPublicMessageUpdate'. Also take a look at the new documentation.",
        ReplaceWith("this.deferPublicMessageUpdate()"),
        level = DeprecationLevel.ERROR
    )
    public suspend fun acknowledgePublicDeferredMessageUpdate(): PublicMessageInteractionResponseBehavior {
        val request = InteractionResponseCreateRequest(
            type = InteractionResponseType.DeferredUpdateMessage
        )

        kord.rest.interaction.createInteractionResponse(id, token, request)

        return PublicMessageInteractionResponseBehavior(applicationId, token, kord)
    }

    /**
     * Acknowledges the interaction with the intent of updating the original public message later by calling
     * [edit][PublicMessageInteractionResponseBehavior.edit] on the returned object.
     *
     * There is no requirement to actually update the message later, calling this is sufficient to handle the
     * interaction and stops any 'loading' animations in the client.
     *
     * There is nothing that will prevent you from calling this for an [ephemeral][MessageFlag.Ephemeral] message but
     * subsequent operations on the returned [PublicMessageInteractionResponseBehavior] might fail.
     *
     * This is not available for [ModalSubmitInteraction]s that do not contain a
     * [message][ModalSubmitInteraction.message].
     */
    public suspend fun deferPublicMessageUpdate(): PublicMessageInteractionResponseBehavior {
        kord.rest.interaction.deferMessageUpdate(id, token)
        return PublicMessageInteractionResponseBehavior(applicationId, token, kord)
    }

    /**
     * Acknowledges a component interaction ephemerally with the intent of updating it later.
     *
     * There is no requirement to actually update the message later, calling this is
     * sufficient to handle the interaction and stops any 'loading' state in the client.
     *
     * There is no noticeable difference between this and [acknowledgePublicDeferredMessageUpdate]
     * when it comes to acknowledging the interaction, both functions can be called
     * on public and ephemeral messages.
     *
     * @suppress
     */
    @Deprecated(
        "Renamed to 'deferEphemeralMessageUpdate'. Also take a look at the new documentation.",
        ReplaceWith("this.deferEphemeralMessageUpdate()"),
        level = DeprecationLevel.ERROR
    )
    public suspend fun acknowledgeEphemeralDeferredMessageUpdate(): EphemeralMessageInteractionResponseBehavior {
        val request = InteractionResponseCreateRequest(
            data = Optional.Value(
                InteractionApplicationCommandCallbackData(
                    flags = Optional(MessageFlags(MessageFlag.Ephemeral))
                )
            ),
            type = InteractionResponseType.DeferredUpdateMessage
        )

        kord.rest.interaction.createInteractionResponse(id, token, request)

        return EphemeralMessageInteractionResponseBehavior(applicationId, token, kord)
    }

    /**
     * Acknowledges the interaction with the intent of updating the original [ephemeral][MessageFlag.Ephemeral] message
     * later by calling [edit][EphemeralMessageInteractionResponseBehavior.edit] on the returned object.
     *
     * There is no requirement to actually update the message later, calling this is sufficient to handle the
     * interaction and stops any 'loading' animations in the client.
     *
     * There is nothing that will prevent you from calling this for a public message but subsequent operations on the
     * returned [EphemeralMessageInteractionResponseBehavior] might fail.
     *
     * This is not available for [ModalSubmitInteraction]s that do not contain a
     * [message][ModalSubmitInteraction.message].
     */
    public suspend fun deferEphemeralMessageUpdate(): EphemeralMessageInteractionResponseBehavior {
        kord.rest.interaction.deferMessageUpdate(id, token)
        return EphemeralMessageInteractionResponseBehavior(applicationId, token, kord)
    }

    override fun withStrategy(strategy: EntitySupplyStrategy<*>): ComponentInteractionBehavior =
        ComponentInteractionBehavior(id, channelId, token, applicationId, kord, strategy)
}

/**
 * Creates a [ComponentInteractionBehavior] with the given [id], [channelId], [token], [applicationId], [kord] and
 * [strategy].
 */
public fun ComponentInteractionBehavior(
    id: Snowflake,
    channelId: Snowflake,
    token: String,
    applicationId: Snowflake,
    kord: Kord,
    strategy: EntitySupplyStrategy<*> = kord.resources.defaultStrategy
): ComponentInteractionBehavior = object : ComponentInteractionBehavior {
    override val id: Snowflake = id
    override val channelId: Snowflake = channelId
    override val token: String = token
    override val applicationId: Snowflake = applicationId
    override val kord: Kord = kord
    override val supplier: EntitySupplier = strategy.supply(kord)
}

/**
 * Acknowledges a component interaction publicly and updates the message with the [builder].
 *
 * There is no noticeable difference between this and [acknowledgeEphemeralUpdateMessage]
 * when it comes to acknowledging the interaction, both functions can be called
 * on public and ephemeral messages.
 *
 * @suppress
 */
@Deprecated(
    "Renamed to 'updatePublicMessage'. Also take a look at the new documentation.",
    ReplaceWith("this.updatePublicMessage()", "dev.jombi.kordsb.core.behavior.interaction.updatePublicMessage"),
    level = DeprecationLevel.ERROR
)
public suspend fun ComponentInteractionBehavior.acknowledgePublicUpdateMessage(
    builder: UpdateMessageInteractionResponseCreateBuilder.() -> Unit
): PublicMessageInteractionResponseBehavior {
    contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }

    val request = UpdateMessageInteractionResponseCreateBuilder().apply(builder).toRequest()

    kord.rest.interaction.createInteractionResponse(
        id,
        token,
        request.copy(request = request.request.copy(InteractionResponseType.UpdateMessage))
    )

    return PublicMessageInteractionResponseBehavior(applicationId, token, kord)
}

/**
 * Responds to the interaction by updating the original public message.
 *
 * There is nothing that will prevent you from calling this for an [ephemeral][MessageFlag.Ephemeral] message but
 * subsequent operations on the returned [PublicMessageInteractionResponseBehavior] might fail.
 *
 * This is not available for [ModalSubmitInteraction]s that do not contain a [message][ModalSubmitInteraction.message].
 */
public suspend inline fun ComponentInteractionBehavior.updatePublicMessage(
    builder: UpdateMessageInteractionResponseCreateBuilder.() -> Unit,
): PublicMessageInteractionResponseBehavior {
    contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }

    val request = UpdateMessageInteractionResponseCreateBuilder().apply(builder).toRequest()
    kord.rest.interaction.createInteractionResponse(id, token, request)

    return PublicMessageInteractionResponseBehavior(applicationId, token, kord)
}

/**
 * Acknowledges a component interaction ephemerally and updates the message with the [builder].
 *
 * There is no noticeable difference between this and [acknowledgeEphemeralUpdateMessage]
 * when it comes to acknowledging the interaction, both functions can be called
 * on public and ephemeral messages.
 *
 * @suppress
 */
@Deprecated(
    "Renamed to 'updateEphemeralMessage'. Also take a look at the new documentation.",
    ReplaceWith("this.updateEphemeralMessage()", "dev.jombi.kordsb.core.behavior.interaction.updateEphemeralMessage"),
    level = DeprecationLevel.ERROR
)
public suspend fun ComponentInteractionBehavior.acknowledgeEphemeralUpdateMessage(
    builder: UpdateMessageInteractionResponseCreateBuilder.() -> Unit
): EphemeralMessageInteractionResponseBehavior {
    contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }

    val request = UpdateMessageInteractionResponseCreateBuilder().apply(builder).toRequest()

    kord.rest.interaction.createInteractionResponse(
        id,
        token,
        request
    )

    return EphemeralMessageInteractionResponseBehavior(applicationId, token, kord)
}

/**
 * Responds to the interaction by updating the original [ephemeral][MessageFlag.Ephemeral] message.
 *
 * There is nothing that will prevent you from calling this for a public message but subsequent operations on the
 * returned [EphemeralMessageInteractionResponseBehavior] might fail.
 *
 * This is not available for [ModalSubmitInteraction]s that do not contain a [message][ModalSubmitInteraction.message].
 */
public suspend inline fun ComponentInteractionBehavior.updateEphemeralMessage(
    builder: UpdateMessageInteractionResponseCreateBuilder.() -> Unit,
): EphemeralMessageInteractionResponseBehavior {
    contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }

    val request = UpdateMessageInteractionResponseCreateBuilder().apply(builder).toRequest()
    kord.rest.interaction.createInteractionResponse(id, token, request)

    return EphemeralMessageInteractionResponseBehavior(applicationId, token, kord)
}
