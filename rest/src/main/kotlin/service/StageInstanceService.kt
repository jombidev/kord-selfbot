package dev.jombi.kordsb.rest.service

import dev.jombi.kordsb.common.entity.DiscordStageInstance
import dev.jombi.kordsb.common.entity.Snowflake
import dev.jombi.kordsb.rest.builder.stage.StageInstanceCreateBuilder
import dev.jombi.kordsb.rest.builder.stage.StageInstanceModifyBuilder
import dev.jombi.kordsb.rest.json.request.StageInstanceCreateRequest
import dev.jombi.kordsb.rest.json.request.StageInstanceModifyRequest
import dev.jombi.kordsb.rest.request.RequestHandler
import dev.jombi.kordsb.rest.request.auditLogReason
import dev.jombi.kordsb.rest.route.Route
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

public class StageInstanceService(requestHandler: RequestHandler) : RestService(requestHandler) {

    public suspend fun getStageInstance(channelId: Snowflake): DiscordStageInstance = call(Route.StageInstanceGet) {
        keys[Route.ChannelId] = channelId
    }

    public suspend fun createStageInstance(
        request: StageInstanceCreateRequest,
        reason: String? = null,
    ): DiscordStageInstance = call(Route.StageInstancePost) {
        body(StageInstanceCreateRequest.serializer(), request)
        auditLogReason(reason)
    }

    public suspend inline fun createStageInstance(
        channelId: Snowflake,
        topic: String,
        builder: StageInstanceCreateBuilder.() -> Unit = {},
    ): DiscordStageInstance {
        contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }

        val appliedBuilder = StageInstanceCreateBuilder(channelId, topic).apply(builder)
        return createStageInstance(appliedBuilder.toRequest(), appliedBuilder.reason)
    }

    public suspend fun modifyStageInstance(
        channelId: Snowflake,
        request: StageInstanceModifyRequest,
        reason: String? = null,
    ): DiscordStageInstance = call(Route.StageInstancePatch) {
        keys[Route.ChannelId] = channelId

        body(StageInstanceModifyRequest.serializer(), request)
        auditLogReason(reason)
    }

    public suspend inline fun modifyStageInstance(
        channelId: Snowflake,
        builder: StageInstanceModifyBuilder.() -> Unit,
    ): DiscordStageInstance {
        contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }

        val appliedBuilder = StageInstanceModifyBuilder().apply(builder)
        return modifyStageInstance(channelId, appliedBuilder.toRequest(), appliedBuilder.reason)
    }

    /** @suppress */
    @Suppress("DEPRECATION_ERROR")
    @Deprecated(
        "Replaced by 'modifyStageInstance'.",
        ReplaceWith("this.modifyStageInstance(channelId, request, reason)"),
        DeprecationLevel.ERROR
    )
    public suspend fun updateStageInstance(
        channelId: Snowflake,
        request: dev.jombi.kordsb.rest.json.request.StageInstanceUpdateRequest,
        reason: String? = null,
    ): DiscordStageInstance = call(Route.StageInstancePatch) {
        keys[Route.ChannelId] = channelId

        body(dev.jombi.kordsb.rest.json.request.StageInstanceUpdateRequest.serializer(), request)
        auditLogReason(reason)
    }

    public suspend fun deleteStageInstance(channelId: Snowflake, reason: String? = null): Unit =
        call(Route.StageInstanceDelete) {
            keys[Route.ChannelId] = channelId
            auditLogReason(reason)
        }
}

/** @suppress */
@Deprecated(
    "Replaced by builder overload.",
    ReplaceWith("this.createStageInstance(channelId, topic) {\nthis@createStageInstance.reason = reason\n}"),
    DeprecationLevel.ERROR
)
public suspend fun StageInstanceService.createStageInstance(
    channelId: Snowflake,
    topic: String,
    reason: String? = null,
): DiscordStageInstance = createStageInstance(
    StageInstanceCreateRequest(channelId, topic), reason
)

/** @suppress */
@Suppress("DEPRECATION_ERROR")
@Deprecated(
    "Replaced by 'modifyStageInstance'.",
    ReplaceWith(
        "this.modifyStageInstance(channelId) {\nthis@modifyStageInstance.topic = topic\nthis@modifyStageInstance.reason = reason\n}"
    ),
    DeprecationLevel.ERROR
)
public suspend fun StageInstanceService.updateStageInstance(
    channelId: Snowflake,
    topic: String,
    reason: String? = null,
): DiscordStageInstance = updateStageInstance(
    channelId,
    dev.jombi.kordsb.rest.json.request.StageInstanceUpdateRequest(topic),
    reason
)
