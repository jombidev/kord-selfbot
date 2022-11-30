package dev.jombi.kordsb.core.entity.channel

import dev.jombi.kordsb.core.Kord
import dev.jombi.kordsb.core.behavior.channel.ChannelBehavior
import dev.jombi.kordsb.core.behavior.channel.GuildChannelBehavior
import dev.jombi.kordsb.core.behavior.channel.StoreChannelBehavior
import dev.jombi.kordsb.core.cache.data.ChannelData
import dev.jombi.kordsb.core.supplier.EntitySupplier
import dev.jombi.kordsb.core.supplier.EntitySupplyStrategy
import java.util.Objects
import kotlin.DeprecationLevel.ERROR

/**
 * An instance of a Discord Store Channel associated to a guild.
 *
 * @suppress
 */
@Suppress("DEPRECATION_ERROR")
@Deprecated(
    """
    Discord no longer offers the ability to purchase a license to sell PC games on Discord and store channels were
    removed on March 10, 2022.
    
    See https://support-dev.discord.com/hc/en-us/articles/4414590563479 for more information.
    """,
    level = ERROR,
)
public data class StoreChannel(
    override val data: ChannelData,
    override val kord: Kord,
    override val supplier: EntitySupplier = kord.defaultSupplier
) : CategorizableChannel, StoreChannelBehavior {


    override suspend fun asChannel(): StoreChannel = this

    override suspend fun asChannelOrNull(): StoreChannel = this

    /**
     * Returns a new [StoreChannel] with the given [strategy].
     */
    override fun withStrategy(strategy: EntitySupplyStrategy<*>): StoreChannel =
        StoreChannel(data, kord, strategy.supply(kord))

    override fun hashCode(): Int = Objects.hash(id, guildId)

    override fun equals(other: Any?): Boolean = when (other) {
        is GuildChannelBehavior -> other.id == id && other.guildId == guildId
        is ChannelBehavior -> other.id == id
        else -> false
    }

    override fun toString(): String {
        return "StoreChannel(data=$data, kord=$kord, supplier=$supplier)"
    }

}
