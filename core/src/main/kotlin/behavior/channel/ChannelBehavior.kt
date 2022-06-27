package dev.jombi.kordsb.core.behavior.channel

import dev.jombi.kordsb.common.entity.Snowflake
import dev.jombi.kordsb.common.exception.RequestException
import dev.jombi.kordsb.core.Kord
import dev.jombi.kordsb.core.entity.KordEntity
import dev.jombi.kordsb.core.entity.Strategizable
import dev.jombi.kordsb.core.entity.channel.Channel
import dev.jombi.kordsb.core.exception.EntityNotFoundException
import dev.jombi.kordsb.core.supplier.EntitySupplier
import dev.jombi.kordsb.core.supplier.EntitySupplyStrategy
import dev.jombi.kordsb.core.supplier.getChannelOf
import dev.jombi.kordsb.core.supplier.getChannelOfOrNull
import dev.jombi.kordsb.rest.request.RestRequestException
import java.util.*

/**
 * The behavior of a [Discord Channel](https://discord.com/developers/docs/resources/channel)
 */
public interface ChannelBehavior : KordEntity, Strategizable {

    /**
     * This channel [formatted as a mention](https://discord.com/developers/docs/reference#message-formatting)
     * as used by the Discord API.
     */
    public val mention: String get() = "<#$id>"

    /**
     * Requests to get this behavior as a [Channel] .
     *
     * @throws [RequestException] if something went wrong during the request.
     * @throws [EntityNotFoundException] if the channel wasn't present.
     */
    public suspend fun asChannel(): Channel = supplier.getChannel(id)

    /**
     * Requests to get this behavior as a [Channel],
     * returns null if the channel isn't present.
     *
     * @throws [RequestException] if something went wrong during the request.
     */
    public suspend fun asChannelOrNull(): Channel? = supplier.getChannelOrNull(id)

    /**
     * Retrieve the [Channel] associated with this behaviour from the provided [EntitySupplier]
     *
     * @throws [RequestException] if anything went wrong during the request.
     * @throws [EntityNotFoundException] if the user wasn't present.
     */
    public suspend fun fetchChannel(): Channel = supplier.getChannel(id)


    /**
     * Retrieve the [Channel] associated with this behaviour from the provided [EntitySupplier]
     * returns null if the [Channel] isn't present.
     *
     * @throws [RequestException] if anything went wrong during the request.
     */
    public suspend fun fetchChannelOrNull(): Channel? = supplier.getChannelOrNull(id)

    /**
     * Requests to delete a channel (or close it if this is a dm channel).
     *
     * @param reason the reason showing up in the audit log
     * @throws [RestRequestException] if something went wrong during the request.
     */
    public suspend fun delete(reason: String? = null) {
        kord.rest.channel.deleteChannel(id, reason)
    }

    /**
     * Returns a new [ChannelBehavior] with the given [strategy].
     */
    override fun withStrategy(strategy: EntitySupplyStrategy<*>): ChannelBehavior = ChannelBehavior(id, kord, strategy)

}

/**
 * Requests to get the [Channel] represented by the [id],
 * returns null if the [Channel] isn't present.
 *
 * @throws [RequestException] if anything went wrong during the request.
 * @throws [ClassCastException] if the channel is not of type [T]
 */
@Deprecated("Deprecated in favor of asChannelOfOrNull",ReplaceWith("asChannelOfOrNull(id)"))
public suspend inline fun <reified T: Channel> ChannelBehavior.ofOrNull(): T? = supplier.getChannelOfOrNull(id)


/**
 * Requests to get the [Channel] represented by the [id].
 *
 * @throws [RequestException] if anything went wrong during the request.
 * @throws [EntityNotFoundException] if the [Channel] wasn't present.
 * @throws [ClassCastException] if the channel is not of type  [T].
 */
@Deprecated("Deprecated in favor of asChannelOfOrNull",ReplaceWith("asChannelOfOrNull(id)"))
public suspend inline fun <reified T: Channel> ChannelBehavior.of(): T = supplier.getChannelOf(id)


/**
 * Requests to get the [Channel] represented by the [id],
 * returns null if the [Channel] isn't present.
 *
 * @throws [RequestException] if anything went wrong during the request.
 * @throws [ClassCastException] if the channel is not of type [T]
 */
public suspend inline fun <reified T: Channel> ChannelBehavior.asChannelOfOrNull(): T? = supplier.getChannelOfOrNull(id)


/**
 * Requests to get the [Channel] represented by the [id].
 *
 * @throws [RequestException] if anything went wrong during the request.
 * @throws [EntityNotFoundException] if the [Channel] wasn't present.
 * @throws [ClassCastException] if the channel is not of type  [T].
 */
public suspend inline fun <reified T: Channel> ChannelBehavior.asChannelOf(): T = supplier.getChannelOf(id)

public fun ChannelBehavior(id: Snowflake, kord: Kord, strategy: EntitySupplyStrategy<*> = kord.resources.defaultStrategy): ChannelBehavior =
    object : ChannelBehavior {
        override val id: Snowflake = id
        override val kord: Kord = kord
        override val supplier: EntitySupplier = strategy.supply(kord)


        override fun hashCode(): Int = Objects.hash(id)

        override fun equals(other: Any?): Boolean = when (other) {
            is ChannelBehavior -> other.id == id
            else -> false
        }

        override fun toString(): String {
            return "ChannelBehavior(id=$id, kord=$kord, supplier=$supplier)"
        }
    }
