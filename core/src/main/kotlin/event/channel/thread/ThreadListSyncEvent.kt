package dev.jombi.kordsb.core.event.channel.thread

import dev.jombi.kordsb.common.entity.Snowflake
import dev.jombi.kordsb.common.entity.optional.orEmpty
import dev.jombi.kordsb.core.Kord
import dev.jombi.kordsb.core.behavior.GuildBehavior
import dev.jombi.kordsb.core.behavior.channel.threads.ThreadParentChannelBehavior
import dev.jombi.kordsb.core.cache.data.ThreadListSyncData
import dev.jombi.kordsb.core.entity.Guild
import dev.jombi.kordsb.core.entity.Strategizable
import dev.jombi.kordsb.core.entity.channel.Channel
import dev.jombi.kordsb.core.entity.channel.TopGuildChannel
import dev.jombi.kordsb.core.entity.channel.thread.ThreadChannel
import dev.jombi.kordsb.core.entity.channel.thread.ThreadMember
import dev.jombi.kordsb.core.event.Event
import dev.jombi.kordsb.core.event.kordCoroutineScope
import dev.jombi.kordsb.core.supplier.EntitySupplier
import dev.jombi.kordsb.core.supplier.EntitySupplyStrategy
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlin.coroutines.CoroutineContext


public class ThreadListSyncEvent(
    public val data: ThreadListSyncData,
    override val kord: Kord,
    override val supplier: EntitySupplier = kord.defaultSupplier,
    public val coroutineScope: CoroutineScope = kordCoroutineScope(kord)
) : Event, CoroutineScope by coroutineScope, Strategizable {

    public val guildId: Snowflake get() = data.guildId

    public val guild: GuildBehavior get() = GuildBehavior(guildId, kord)

    /**
     * the parent channel ids whose threads are being synced.
     * If empty, then threads were synced for the entire guild.
     */
    public val channelIds: List<Snowflake> get() = data.channelIds.orEmpty()

    public val channelBehaviors: List<ThreadParentChannelBehavior>
        get() = channelIds.map {
            ThreadParentChannelBehavior(guildId, it, kord)
        }

    /**
     * Threads that are being synced for [channelIds].
     *
     * @see [channelIds]
     */
    public val threads: List<ThreadChannel>
        get() = data.threads.mapNotNull {
            Channel.from(it, kord) as? ThreadChannel
        }

    /**
     * [ThreadMember] objects for the current user for each of the synced threads.
     */
    public val members: List<ThreadMember> get() = data.members.map { ThreadMember(it, kord) }

    public suspend fun getGuild(): Guild {
        return supplier.getGuild(guildId)
    }

    public suspend fun getGuildOrNull(): Guild? {
        return supplier.getGuildOrNull(guildId)
    }

    public suspend fun getChannels(): Flow<TopGuildChannel> {
        return supplier.getGuildChannels(guildId).filter { it.id in channelIds }
    }

    override fun withStrategy(strategy: EntitySupplyStrategy<*>): Strategizable {
        return ThreadListSyncEvent(data, kord, strategy.supply(kord))
    }

}
