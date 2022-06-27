package dev.jombi.kordsb.core.entity.channel.thread

import dev.jombi.kordsb.common.entity.ChannelType
import dev.jombi.kordsb.common.entity.Snowflake
import dev.jombi.kordsb.core.Kord
import dev.jombi.kordsb.core.cache.data.ChannelData
import dev.jombi.kordsb.core.entity.channel.TextChannel
import dev.jombi.kordsb.core.supplier.EntitySupplier
import dev.jombi.kordsb.core.supplier.EntitySupplyStrategy
import dev.jombi.kordsb.core.supplier.getChannelOf
import dev.jombi.kordsb.core.supplier.getChannelOfOrNull

/**
 * A thread channel instance whose parent is a [TextChannel].
 */
public class TextChannelThread(
    override val data: ChannelData,
    override val kord: Kord,
    override val supplier: EntitySupplier = kord.defaultSupplier
) : ThreadChannel {

    /**
     * Whether this thread is private.
     */
    public val isPrivate: Boolean get() = data.type == ChannelType.PrivateThread

    /**
     * Whether non-moderators can add other non-moderators to a thread.
     *
     * This is only applicable to [private][isPrivate] threads and will always be `false` for public threads.
     */
    public val isInvitable: Boolean get() = data.threadMetadata.value!!.invitable.discordBoolean

    override suspend fun getParent(): TextChannel {
        return supplier.getChannelOf(parentId)
    }

    override suspend fun getParentOrNull(): TextChannel? {
        return supplier.getChannelOfOrNull(parentId)
    }

    override val guildId: Snowflake
        get() = data.guildId.value!!

    override suspend fun asChannel(): TextChannelThread = this

    override suspend fun asChannelOrNull(): TextChannelThread = this

    override fun withStrategy(strategy: EntitySupplyStrategy<*>): TextChannelThread {
        return TextChannelThread(data, kord, strategy.supply(kord))
    }

}
