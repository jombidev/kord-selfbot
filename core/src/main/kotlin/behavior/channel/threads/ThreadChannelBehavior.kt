package dev.jombi.kordsb.core.behavior.channel.threads

import dev.jombi.kordsb.common.entity.Snowflake
import dev.jombi.kordsb.common.exception.RequestException
import dev.jombi.kordsb.core.Kord
import dev.jombi.kordsb.core.behavior.channel.GuildMessageChannelBehavior
import dev.jombi.kordsb.core.cache.data.toData
import dev.jombi.kordsb.core.entity.channel.Channel
import dev.jombi.kordsb.core.entity.channel.ThreadParentChannel
import dev.jombi.kordsb.core.entity.channel.thread.ThreadChannel
import dev.jombi.kordsb.core.entity.channel.thread.ThreadMember
import dev.jombi.kordsb.core.exception.EntityNotFoundException
import dev.jombi.kordsb.core.supplier.EntitySupplier
import dev.jombi.kordsb.core.supplier.EntitySupplyStrategy
import dev.jombi.kordsb.core.supplier.getChannelOf
import dev.jombi.kordsb.core.supplier.getChannelOfOrNull
import dev.jombi.kordsb.rest.builder.channel.thread.ThreadModifyBuilder
import kotlinx.coroutines.flow.Flow
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

public interface ThreadChannelBehavior : GuildMessageChannelBehavior {

    public val parentId: Snowflake

    public val parent: ThreadParentChannelBehavior get() = ThreadParentChannelBehavior(guildId, parentId, kord)

    /**
     * Requests to get all members of the current thread.
     *
     * The returned flow is lazily executed, any [RequestException] will be thrown on
     * [terminal operators](https://kotlinlang.org/docs/reference/coroutines/flow.html#terminal-flow-operators) instead.
     */
    public val members: Flow<ThreadMember>
        get() = supplier.getThreadMembers(id)

    /**
     * Removes the user identified by [id] from the current thread.
     * Requires the thread is not locked.
     * or current bot has [Manage Threads][dev.jombi.kordsb.common.entity.Permission.ManageThreads] permission
     *
     * @see [ThreadChannel.isArchived]
     * @see [ThreadChannel.isLocked]
     * @throws [RequestException] if something went wrong during the request.
     */
    public suspend fun removeUser(userId: Snowflake) {
        kord.rest.channel.removeUserFromThread(id, userId)
    }

    /**
     * Adds the user identified by [id] from the current thread.
     * Requires the thread is not locked.
     * or current bot has [Manage Threads][dev.jombi.kordsb.common.entity.Permission.ManageThreads] permission
     *
     * @see [ThreadChannel.isArchived]
     * @see [ThreadChannel.isLocked]
     * @throws [RequestException] if something went wrong during the request.
     */
    public suspend fun addUser(userId: Snowflake) {
        kord.rest.channel.addUserToThread(id, userId)
    }

    /**
     * Join the the current thread.
     * Requires the thread is not locked.
     * or current bot has [Manage Threads][dev.jombi.kordsb.common.entity.Permission.ManageThreads] permission
     *
     * @see [ThreadChannel.isArchived]
     * @see [ThreadChannel.isLocked]
     * @throws [RequestException] if something went wrong during the request.
     */
    public suspend fun join() {
        kord.rest.channel.joinThread(id)
    }

    /**
     * Leaves the current thread if the bot has already joined.
     * Requires the thread is not locked.
     * or current bot has [Manage Threads][dev.jombi.kordsb.common.entity.Permission.ManageThreads] permission
     *
     * @throws [RequestException] if something went wrong during the request.
     */
    public suspend fun leave() {
        kord.rest.channel.leaveThread(id)
    }

    /**
     * Deleting a thread requires the [Manage Threads][dev.jombi.kordsb.common.entity.Permission.ManageThreads] permission.
     */
    override suspend fun delete(reason: String?) {
        super.delete(reason)
    }

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


    override suspend fun asChannel(): ThreadChannel {
        return super.asChannel() as ThreadChannel
    }

    override suspend fun asChannelOrNull(): ThreadChannel? {
        return super.asChannelOrNull() as? ThreadChannel
    }

    override fun withStrategy(strategy: EntitySupplyStrategy<*>): ThreadChannelBehavior {
        return ThreadChannelBehavior(guildId, parentId, id, kord, strategy.supply(kord))
    }

}

/**
 * * Editing a thread to set [archived][ThreadChannel.isArchived] to false only requires the current user to be in the thread.
 * * If [locked][ThreadChannel.isLocked] is true, then the user must have [Manage Threads][dev.jombi.kordsb.common.entity.Permission.ManageThreads]
 * * Editing a thread to change the
 * [name][ThreadModifyBuilder.name],
 * [archived][ThreadModifyBuilder.archived],
 * [autoArchiveDuration][ThreadModifyBuilder.autoArchiveDuration] fields
 * requires [Manage Threads][dev.jombi.kordsb.common.entity.Permission.ManageThreads] or that the current user is the thread creator.
 */
public suspend inline fun ThreadChannelBehavior.edit(builder: ThreadModifyBuilder.() -> Unit): ThreadChannel {
    contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
    val appliedBuilder = ThreadModifyBuilder().apply(builder)
    val patchedChannel = kord.rest.channel.patchThread(id, appliedBuilder.toRequest(), appliedBuilder.reason)
    return Channel.from(patchedChannel.toData(), kord) as ThreadChannel
}

internal fun ThreadChannelBehavior(
    guildId: Snowflake,
    parentId: Snowflake,
    id: Snowflake,
    kord: Kord,
    supplier: EntitySupplier = kord.defaultSupplier
): ThreadChannelBehavior {
    return object : ThreadChannelBehavior {

        override val kord: Kord
            get() = kord

        override val id: Snowflake
            get() = id

        override val parentId: Snowflake
            get() = parentId

        override val guildId: Snowflake
            get() = guildId

        override val supplier: EntitySupplier
            get() = supplier

    }
}
