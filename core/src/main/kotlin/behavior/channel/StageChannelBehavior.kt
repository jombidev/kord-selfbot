package dev.jombi.kordsb.core.behavior.channel

import dev.jombi.kordsb.common.entity.Snowflake
import dev.jombi.kordsb.core.Kord
import dev.jombi.kordsb.core.cache.data.ChannelData
import dev.jombi.kordsb.core.cache.data.StageInstanceData
import dev.jombi.kordsb.core.entity.StageInstance
import dev.jombi.kordsb.core.entity.channel.Channel
import dev.jombi.kordsb.core.entity.channel.StageChannel
import dev.jombi.kordsb.core.supplier.EntitySupplier
import dev.jombi.kordsb.core.supplier.EntitySupplyStrategy
import dev.jombi.kordsb.rest.builder.channel.StageVoiceChannelModifyBuilder
import dev.jombi.kordsb.rest.builder.guild.CurrentVoiceStateModifyBuilder
import dev.jombi.kordsb.rest.builder.guild.VoiceStateModifyBuilder
import dev.jombi.kordsb.rest.builder.stage.StageInstanceCreateBuilder
import dev.jombi.kordsb.rest.request.RestRequestException
import dev.jombi.kordsb.rest.service.modifyCurrentVoiceState
import dev.jombi.kordsb.rest.service.modifyVoiceState
import dev.jombi.kordsb.rest.service.patchStageVoiceChannel
import kotlin.DeprecationLevel.HIDDEN
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

public interface StageChannelBehavior : BaseVoiceChannelBehavior {

    /**
     * Returns a new [StageChannelBehavior] with the given [strategy].
     */
    override fun withStrategy(
        strategy: EntitySupplyStrategy<*>
    ): StageChannelBehavior {
        return StageChannelBehavior(id, guildId, kord, strategy.supply(kord))
    }

    @Deprecated("Binary compatibility.", level = HIDDEN)
    public suspend fun createStageInstance(topic: String): StageInstance {
        val instance = kord.rest.stageInstance.createStageInstance(id, topic)
        val data = StageInstanceData.from(instance)

        return StageInstance(data, kord, supplier)
    }

    public suspend fun getStageInstanceOrNull(): StageInstance? = supplier.getStageInstanceOrNull(id)

    public suspend fun getStageInstance(): StageInstance = supplier.getStageInstance(id)

}

public suspend inline fun StageChannelBehavior.createStageInstance(
    topic: String,
    builder: StageInstanceCreateBuilder.() -> Unit = {},
): StageInstance {
    contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }

    val instance = kord.rest.stageInstance.createStageInstance(id, topic, builder)
    val data = StageInstanceData.from(instance)
    return StageInstance(data, kord, supplier)
}

/**
 * Requests to edit the current user's voice state in this [StageChannel].
 */
public suspend inline fun StageChannelBehavior.editCurrentVoiceState(builder: CurrentVoiceStateModifyBuilder.() -> Unit) {
    contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
    kord.rest.guild.modifyCurrentVoiceState(guildId, id, builder)
}

/**
 * Requests to edit the another user's voice state in this [StageChannel].
 */
public suspend inline fun StageChannelBehavior.editVoiceState(
    userId: Snowflake,
    builder: VoiceStateModifyBuilder.() -> Unit
) {
    contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
    kord.rest.guild.modifyVoiceState(guildId, id, userId, builder)
}

/**
 * Requests to edit this channel.
 *
 * @return The edited [StageChannel].
 *
 * @throws [RestRequestException] if something went wrong during the request.
 */
public suspend fun StageChannelBehavior.edit(builder: StageVoiceChannelModifyBuilder.() -> Unit): StageChannel {
    contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
    val response = kord.rest.channel.patchStageVoiceChannel(id, builder)

    val data = ChannelData.from(response)
    return Channel.from(data, kord) as StageChannel
}

public fun StageChannelBehavior(
    id: Snowflake,
    guildId: Snowflake,
    kord: Kord,
    supplier: EntitySupplier = kord.defaultSupplier
): StageChannelBehavior = object : StageChannelBehavior {
    override val guildId: Snowflake
        get() = guildId
    override val kord get() = kord
    override val id: Snowflake get() = id
    override val supplier get() = supplier

    override fun toString(): String {
        return "StageChannelBehavior(id=$id, guildId=$guildId, kord=$kord, supplier=$supplier)"
    }
}
