package dev.jombi.kordsb.core.entity.channel

import dev.jombi.kordsb.core.Kord
import dev.jombi.kordsb.core.behavior.channel.ChannelBehavior
import dev.jombi.kordsb.core.behavior.channel.TopGuildChannelBehavior
import dev.jombi.kordsb.core.behavior.channel.NewsChannelBehavior
import dev.jombi.kordsb.core.cache.data.ChannelData
import dev.jombi.kordsb.core.supplier.EntitySupplier
import dev.jombi.kordsb.core.supplier.EntitySupplyStrategy
import java.util.*

/**
 * An instance of a Discord News Channel associated to a guild.
 */
public class NewsChannel(
    override val data: ChannelData,
    override val kord: Kord,
    override val supplier: EntitySupplier = kord.defaultSupplier
) : CategorizableChannel, TopGuildMessageChannel, ThreadParentChannel,  NewsChannelBehavior {

    override fun hashCode(): Int = Objects.hash(id, guildId)

    override fun equals(other: Any?): Boolean = when (other) {
        is TopGuildChannelBehavior -> other.id == id && other.guildId == guildId
        is ChannelBehavior -> other.id == id
        else -> false
    }

    override suspend fun asChannel(): NewsChannel = this

    override suspend fun asChannelOrNull(): NewsChannel = this

    /**
     * Returns a new [NewsChannel] with the given [strategy].
     */
    override fun withStrategy(strategy: EntitySupplyStrategy<*>): NewsChannel =
        NewsChannel(data, kord, strategy.supply(kord))

    override fun toString(): String {
        return "NewsChannel(data=$data, kord=$kord, supplier=$supplier)"
    }

}
