package dev.jombi.kordsb.core.entity.channel.thread

import dev.jombi.kordsb.common.entity.ChannelType
import dev.jombi.kordsb.common.entity.Snowflake
import dev.jombi.kordsb.common.exception.RequestException
import dev.jombi.kordsb.core.Kord
import dev.jombi.kordsb.core.behavior.GuildBehavior
import dev.jombi.kordsb.core.behavior.channel.threads.ThreadParentChannelBehavior
import dev.jombi.kordsb.core.cache.data.ChannelData
import dev.jombi.kordsb.core.entity.Guild
import dev.jombi.kordsb.core.entity.Strategizable
import dev.jombi.kordsb.core.entity.channel.ThreadParentChannel
import dev.jombi.kordsb.core.exception.EntityNotFoundException
import dev.jombi.kordsb.core.supplier.EntitySupplier
import dev.jombi.kordsb.core.supplier.EntitySupplyStrategy
import dev.jombi.kordsb.core.supplier.getChannelOf
import dev.jombi.kordsb.core.supplier.getChannelOfOrNull

public class DeletedThreadChannel(
    public val data: ChannelData,
    public val kord: Kord,
    override val supplier: EntitySupplier = kord.defaultSupplier
) : Strategizable {

    public val id: Snowflake
        get() = data.id

    public val type: ChannelType get() = data.type

    public val guildId: Snowflake get() = data.guildId.value!!

    public val guild: GuildBehavior get() = GuildBehavior(guildId, kord)

    public val parentId: Snowflake get() = data.parentId!!.value!!

    public val parent: ThreadParentChannelBehavior get() = ThreadParentChannelBehavior(guildId, id, kord)

    /**
     * Requests to get this channel's [Guild].
     *
     * @throws [RequestException] if something went wrong during the request.
     * @throws [EntityNotFoundException] if the guild wasn't present.
     */
    public suspend fun getGuild(): Guild = supplier.getGuild(guildId)

    /**
     * Requests to get this channel's [Guild],
     * returns null if the guild isn't present.
     *
     * @throws [RequestException] if something went wrong during the request.
     */
    public suspend fun getGuildOrNull(): Guild? = supplier.getGuildOrNull(guildId)


    /**
     * Requests to get this channel's [ThreadParentChannel].
     *
     * @throws [RequestException] if something went wrong during the request.
     * @throws [EntityNotFoundException] if the thread parent wasn't present.
     */
    public suspend fun getParent(): ThreadParentChannel {
        return supplier.getChannelOf(parentId)
    }

    /**
     * Requests to get this channel's [ThreadParentChannel],
     * returns null if the thread parent isn't present.
     *
     * @throws [RequestException] if something went wrong during the request.
     */
    public suspend fun getParentOrNull(): ThreadParentChannel? {
        return supplier.getChannelOfOrNull(parentId)
    }

    override fun withStrategy(strategy: EntitySupplyStrategy<*>): DeletedThreadChannel {
        return DeletedThreadChannel(data, kord, strategy.supply(kord))
    }
}
