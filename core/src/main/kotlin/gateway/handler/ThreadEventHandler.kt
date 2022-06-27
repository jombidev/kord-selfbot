package dev.jombi.kordsb.core.gateway.handler

import dev.kord.cache.api.DataCache
import dev.kord.cache.api.put
import dev.kord.cache.api.query
import dev.kord.cache.api.remove
import dev.jombi.kordsb.common.entity.ChannelType
import dev.jombi.kordsb.common.entity.optional.orEmpty
import dev.jombi.kordsb.core.Kord
import dev.jombi.kordsb.core.cache.data.*
import dev.jombi.kordsb.core.cache.idEq
import dev.jombi.kordsb.core.entity.channel.Channel
import dev.jombi.kordsb.core.entity.channel.thread.*
import dev.jombi.kordsb.core.event.channel.thread.*
import dev.jombi.kordsb.gateway.*
import kotlinx.coroutines.CoroutineScope
import dev.jombi.kordsb.core.event.Event as CoreEvent

public class ThreadEventHandler(
    cache: DataCache
) : BaseGatewayEventHandler(cache) {

    override suspend fun handle(
        event: Event,
        kord: Kord,
        coroutineScope: CoroutineScope
    ): dev.jombi.kordsb.core.event.Event? = when (event) {
        is ThreadCreate -> handle(event, kord, coroutineScope)
        is ThreadUpdate -> handle(event, kord, coroutineScope)
        is ThreadDelete -> handle(event, kord, coroutineScope)
        is ThreadListSync -> handle(event, kord, coroutineScope)
        is ThreadMemberUpdate -> handle(event, kord, coroutineScope)
        is ThreadMembersUpdate -> handle(event, kord, coroutineScope)
        else -> null
    }

    public suspend fun handle(event: ThreadCreate, kord: Kord, coroutineScope: CoroutineScope): CoreEvent? {
        val channelData = event.channel.toData()
        cache.put(channelData)

        val coreEvent = when (val channel = Channel.from(channelData, kord)) {
            is NewsChannelThread -> NewsChannelThreadCreateEvent(channel, coroutineScope)
            is TextChannelThread -> TextChannelThreadCreateEvent(channel, coroutineScope)
            is ThreadChannel -> UnknownChannelThreadCreateEvent(channel, coroutineScope)
            else -> return null
        }
        return coreEvent
    }

    public suspend fun handle(event: ThreadUpdate, kord: Kord, coroutineScope: CoroutineScope): CoreEvent? {
        val channelData = event.channel.toData()
        val oldData = cache.query<ChannelData> {
            idEq(ChannelData::id, event.channel.id)
            idEq(ChannelData::guildId, event.channel.guildId.value)
        }.singleOrNull()
        cache.put(channelData)

        val old = oldData?.let { ThreadChannel(it, kord) }

        val coreEvent = when (val channel = Channel.from(channelData, kord)) {
            is NewsChannelThread -> NewsChannelThreadUpdateEvent(channel, old as? NewsChannelThread, coroutineScope)
            is TextChannelThread -> TextChannelThreadUpdateEvent(channel, old as? TextChannelThread, coroutineScope)
            is ThreadChannel -> UnknownChannelThreadUpdateEvent(channel,old, coroutineScope)
            else -> return null
        }

        return coreEvent

    }

    public suspend fun handle(event: ThreadDelete, kord: Kord, coroutineScope: CoroutineScope): CoreEvent? {

        val channelData = event.channel.toData()
        val cachedData = cache.query<ChannelData> { idEq(ChannelData::id, channelData.id) }.singleOrNull()

        val channel = DeletedThreadChannel(channelData, kord)
        val old = cachedData?.let { Channel.from(cachedData, kord) }
        val coreEvent = when (channel.type) {
            is ChannelType.PublicNewsThread -> NewsChannelThreadDeleteEvent(
                channel,
                old as? NewsChannelThread,
                coroutineScope = coroutineScope
            )
            is ChannelType.PrivateThread,
            is ChannelType.GuildText -> TextChannelThreadDeleteEvent(channel, old as? TextChannelThread, coroutineScope)
            else -> UnknownChannelThreadDeleteEvent(channel, old as? ThreadChannel, coroutineScope)
        }

        cache.remove<ChannelData> { idEq(ChannelData::id, channel.id) }
        return coreEvent
    }

    public suspend fun handle(event: ThreadListSync, kord: Kord, coroutineScope: CoroutineScope): CoreEvent? {
        val data = ThreadListSyncData.from(event)

        data.threads.forEach { thread ->
            cache.put(thread)
        }
        data.members.forEach { member ->
            cache.put(member)
        }

        return ThreadListSyncEvent(data, kord, coroutineScope = coroutineScope)
    }

    public fun handle(event: ThreadMemberUpdate, kord: Kord, coroutineScope: CoroutineScope): CoreEvent? {
        val data = ThreadMemberData.from(event.member)
        val member = ThreadMember(data, kord)
        return ThreadMemberUpdateEvent(member, kord, coroutineScope)
    }

    public suspend fun handle(event: ThreadMembersUpdate, kord: Kord, coroutineScope: CoroutineScope): CoreEvent? {
        val data = ThreadMembersUpdateEventData.from(event)
        for (removedMemberId in data.removedMemberIds.orEmpty()) {
            cache.remove<ThreadMemberData> {
                idEq(ThreadMemberData::userId, removedMemberId)
                idEq(ThreadMemberData::id, data.id)
            }
        }
        for (addedMember in data.addedMembers.orEmpty()) {
            cache.put(addedMember)
        }
        return ThreadMembersUpdateEvent(data, kord, coroutineScope)
    }
}
