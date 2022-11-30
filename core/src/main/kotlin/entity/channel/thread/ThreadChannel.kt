package dev.jombi.kordsb.core.entity.channel.thread

import dev.jombi.kordsb.common.entity.ArchiveDuration
import dev.jombi.kordsb.common.entity.Permission.ManageChannels
import dev.jombi.kordsb.common.entity.Permission.ManageMessages
import dev.jombi.kordsb.common.entity.Snowflake
import dev.jombi.kordsb.common.entity.optional.OptionalInt
import dev.jombi.kordsb.common.entity.optional.unwrap
import dev.jombi.kordsb.common.entity.optional.value
import dev.jombi.kordsb.core.Kord
import dev.jombi.kordsb.core.behavior.UserBehavior
import dev.jombi.kordsb.core.behavior.channel.threads.ThreadChannelBehavior
import dev.jombi.kordsb.core.cache.data.ChannelData
import dev.jombi.kordsb.core.entity.channel.GuildMessageChannel
import dev.jombi.kordsb.core.supplier.EntitySupplier
import dev.jombi.kordsb.core.supplier.EntitySupplyStrategy
import kotlinx.datetime.Instant
import kotlin.time.Duration

public interface ThreadChannel : GuildMessageChannel, ThreadChannelBehavior {

    private val threadData get() = data.threadMetadata.value!!

    /**
     * The id of the user who created the thread.
     */
    public val ownerId: Snowflake
        get() = data.ownerId.value!!

    public val owner: UserBehavior
        get() = UserBehavior(ownerId, kord)

    override val parentId: Snowflake get() = data.parentId.value!!


    /**
     * Whether the channel is archived.
     * Users cannot edit messages, add reactions, use slash commands, or join archived threads.
     * The only operation that should happen within an archived thread is messages being deleted.
     * Sending a message will automatically unarchive the thread, unless the thread has been locked by a moderator.
     */
    public val isArchived: Boolean get() = threadData.archived

    /**
     * Threads that have [isLocked] set to true can only be unarchived by a user with
     * the [Manage Threads][dev.jombi.kordsb.common.entity.Permission.ManageThreads] permission.
     */
    public val isLocked: Boolean get() = threadData.locked.orElse(false)

    /**
     * Whether the channel is nsfw.
     * This is inherited from the parent channel.
     */
    public val isNsfw: Boolean get() = data.nsfw.discordBoolean

    /**
     * timestamp when the thread's archive status was last changed.
     */
    @Deprecated(
        "archiveTimeStamp was renamed to archiveTimestamp.",
        ReplaceWith("archiveTimestamp"),
        DeprecationLevel.HIDDEN,
    )
    public val archiveTimeStamp: Instant
        get() = archiveTimestamp

    /**
     * The timestamp when the thread's archive status was last changed.
     */
    public val archiveTimestamp: Instant get() = threadData.archiveTimestamp

    /**
     * The time in which the thread will be auto archived after inactivity.
     */
    public val autoArchiveDuration: ArchiveDuration get() = threadData.autoArchiveDuration

    /**
     * The timestamp when the thread was created.
     *
     * This is only available for threads created after 2022-01-09.
     */
    public val createTimestamp: Instant? get() = threadData.createTimestamp.value

    /**
     * The amount of time a user has to wait before sending another message.
     *
     * Bots, as well as users with the permission [ManageMessages] or [ManageChannels], are unaffected.
     */
    public val rateLimitPerUser: Duration? get() = data.rateLimitPerUser.value

    /**
     * member count for this thread.
     * approximate maximum value is 50.
     */
    public val memberCount: OptionalInt get() = data.memberCount

    /**
     * message count for this thread.
     * approximate maximum value is 50.
     */
    public val messageCount: OptionalInt get() = data.messageCount

    /**
     * The default duration setup pre-selected for this thread.
     */
    public val defaultAutoArchiveDuration: ArchiveDuration? get() = data.defaultAutoArchiveDuration.value

    /**
     * The member of the current user in the thread.
     */
    public val member: ThreadMember? get() = data.member.unwrap { ThreadMember(it, kord) }


    override fun withStrategy(strategy: EntitySupplyStrategy<*>): ThreadChannel {
        return ThreadChannel(data, kord, strategy.supply(kord))
    }

}

internal fun ThreadChannel(data: ChannelData, kord: Kord, supplier: EntitySupplier = kord.defaultSupplier): ThreadChannel {
    return object : ThreadChannel {

        override val data: ChannelData
            get() = data
        override val kord: Kord
            get() = kord
        override val supplier: EntitySupplier
            get() = supplier
        override val guildId: Snowflake
            get() = data.guildId.value!!
    }
}
