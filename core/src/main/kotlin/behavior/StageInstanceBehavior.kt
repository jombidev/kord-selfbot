package dev.jombi.kordsb.core.behavior

import dev.jombi.kordsb.common.entity.Snowflake
import dev.jombi.kordsb.common.exception.RequestException
import dev.jombi.kordsb.core.Kord
import dev.jombi.kordsb.core.cache.data.StageInstanceData
import dev.jombi.kordsb.core.entity.KordEntity
import dev.jombi.kordsb.core.entity.StageInstance
import dev.jombi.kordsb.core.entity.Strategizable
import dev.jombi.kordsb.core.entity.channel.StageChannel
import dev.jombi.kordsb.core.exception.EntityNotFoundException
import dev.jombi.kordsb.core.supplier.EntitySupplier
import dev.jombi.kordsb.core.supplier.EntitySupplyStrategy
import dev.jombi.kordsb.rest.builder.stage.StageInstanceModifyBuilder
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

public interface StageInstanceBehavior : KordEntity, Strategizable {

    /** The id of the associated [StageChannel]. */
    public val channelId: Snowflake

    public suspend fun delete(reason: String? = null): Unit = kord.rest.stageInstance.deleteStageInstance(channelId, reason)

    /** @suppress */
    @Suppress("DEPRECATION_ERROR")
    @Deprecated("Replaced by 'edit'.", ReplaceWith("this.edit {\nthis@edit.topic = topic\n}"), DeprecationLevel.ERROR)
    public suspend fun update(topic: String): StageInstance {
        val instance = kord.rest.stageInstance.updateStageInstance(channelId, dev.jombi.kordsb.rest.json.request.StageInstanceUpdateRequest(topic))
        val data = StageInstanceData.from(instance)

        return StageInstance(data, kord, supplier)
    }

    /**
     * Requests to get this behavior as a [StageInstance] if it's not an instance of a [StageInstance].
     *
     * @throws [RequestException] if anything went wrong during the request.
     * @throws [EntityNotFoundException] if the user wasn't present.
     */
    public suspend fun asStageInstance(): StageInstance = supplier.getStageInstance(channelId)

    /**
     * Requests to get this behavior as a [StageInstance] if it's not an instance of a [StageInstance],
     * returns null if the stage instance isn't present.
     *
     * @throws [RequestException] if anything went wrong during the request.
     */
    public suspend fun asStageInstanceOrNull(): StageInstance? = supplier.getStageInstanceOrNull(channelId)

    /**
     * Retrieve the [StageInstance] associated with this behaviour from the provided [EntitySupplier]
     *
     * @throws [RequestException] if anything went wrong during the request.
     * @throws [EntityNotFoundException] if the user wasn't present.
     */
    public suspend fun fetchStageInstance(): StageInstance = supplier.getStageInstance(id)


    /**
     * Retrieve the [StageInstance] associated with this behaviour from the provided [EntitySupplier]
     * returns null if the [StageInstance] isn't present.
     *
     * @throws [RequestException] if anything went wrong during the request.
     */
    public suspend fun fetchStageInstanceOrNull(): StageInstance? = supplier.getStageInstanceOrNull(id)

    override fun withStrategy(strategy: EntitySupplyStrategy<*>): StageInstanceBehavior =
        StageInstanceBehavior(id, channelId, kord, strategy.supply(kord))
}

public suspend inline fun StageInstanceBehavior.edit(builder: StageInstanceModifyBuilder.() -> Unit): StageInstance {
    contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }

    val instance = kord.rest.stageInstance.modifyStageInstance(channelId, builder)
    val data = StageInstanceData.from(instance)
    return StageInstance(data, kord, supplier)
}


internal fun StageInstanceBehavior(id: Snowflake, channelId: Snowflake, kord: Kord, supplier: EntitySupplier) =
    object : StageInstanceBehavior {
        override val channelId: Snowflake
            get() = channelId
        override val kord: Kord
            get() = kord
        override val id: Snowflake
            get() = id
        override val supplier: EntitySupplier
            get() = supplier

        override fun toString(): String {
            return "StageInstanceBehavior(id=$id, channelId=$id, kord=$kord, supplier=$supplier)"
        }
    }
