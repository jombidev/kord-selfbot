package dev.jombi.kordsb.core.entity.channel.thread

import dev.jombi.kordsb.common.entity.Snowflake
import dev.jombi.kordsb.core.Kord
import dev.jombi.kordsb.core.cache.data.ChannelData
import dev.jombi.kordsb.core.entity.channel.NewsChannel
import dev.jombi.kordsb.core.supplier.EntitySupplier
import dev.jombi.kordsb.core.supplier.EntitySupplyStrategy
import dev.jombi.kordsb.core.supplier.getChannelOf
import dev.jombi.kordsb.core.supplier.getChannelOfOrNull

/**
 * A thread channel instance whose parent is a [NewsChannel].
 */
public class NewsChannelThread(
    override val data: ChannelData,
    override val kord: Kord,
    override val supplier: EntitySupplier = kord.defaultSupplier
) : ThreadChannel {


    override suspend fun asChannel(): NewsChannelThread = this

    override suspend fun asChannelOrNull(): NewsChannelThread? = this


    override suspend fun getParent(): NewsChannel {
        return supplier.getChannelOf(parentId)
    }

    override suspend fun getParentOrNull(): NewsChannel? {
        return supplier.getChannelOfOrNull(parentId)
    }

    override val guildId: Snowflake
        get() = data.guildId.value!!


    override fun withStrategy(strategy: EntitySupplyStrategy<*>): NewsChannelThread {
        return NewsChannelThread(data, kord, strategy.supply(kord))
    }
}
