package dev.jombi.kordsb.core.gateway.handler

import dev.jombi.kordsb.common.entity.ChannelType
import dev.jombi.kordsb.common.entity.optional.orEmpty
import dev.jombi.kordsb.core.Kord
import dev.jombi.kordsb.core.cache.data.*
import dev.jombi.kordsb.core.cache.idEq
import dev.jombi.kordsb.core.entity.channel.Channel
import dev.jombi.kordsb.core.entity.channel.thread.*
import dev.jombi.kordsb.core.event.channel.thread.*
import dev.jombi.kordsb.gateway.*
import dev.kord.cache.api.put
import dev.kord.cache.api.query
import dev.kord.cache.api.remove

internal class ThreadEventHandler : BaseGatewayEventHandler() {

    override suspend fun handle(
        event: Event,
        kord: Kord,
        context: LazyContext?,
    ): dev.jombi.kordsb.core.event.Event? = when (event) {
        is ThreadCreate -> handle(event, kord, context)
        is ThreadUpdate -> handle(event, kord, context)
        is ThreadDelete -> handle(event, kord, context)
        is ThreadListSync -> handle(event, kord, context)
        is ThreadMemberUpdate -> handle(event, kord, context)
        is ThreadMembersUpdate -> handle(event, kord, context)
        else -> null
    }

    private suspend fun handle(event: ThreadCreate, kord: Kord, context: LazyContext?): ThreadChannelCreateEvent? {
        val channelData = event.channel.toData()
        kord.cache.put(channelData)

        val coreEvent = when (val channel = Channel.from(channelData, kord)) {
            is NewsChannelThread -> NewsChannelThreadCreateEvent(channel, context?.get())
            is TextChannelThread -> TextChannelThreadCreateEvent(channel, context?.get())
            is ThreadChannel -> UnknownChannelThreadCreateEvent(channel, context?.get())
            else -> return null
        }
        return coreEvent
    }

    private suspend fun handle(event: ThreadUpdate, kord: Kord, context: LazyContext?): ThreadUpdateEvent? {
        val channelData = event.channel.toData()
        val oldData = kord.cache.query<ChannelData> {
            idEq(ChannelData::id, event.channel.id)
            idEq(ChannelData::guildId, event.channel.guildId.value)
        }.singleOrNull()
        kord.cache.put(channelData)

        val old = oldData?.let { ThreadChannel(it, kord) }

        val coreEvent = when (val channel = Channel.from(channelData, kord)) {
            is NewsChannelThread -> NewsChannelThreadUpdateEvent(channel, old as? NewsChannelThread, context?.get())
            is TextChannelThread -> TextChannelThreadUpdateEvent(channel, old as? TextChannelThread, context?.get())
            is ThreadChannel -> UnknownChannelThreadUpdateEvent(channel,old, context?.get())
            else -> return null
        }

        return coreEvent

    }

    private suspend fun handle(event: ThreadDelete, kord: Kord, context: LazyContext?): ThreadChannelDeleteEvent {

        val channelData = event.channel.toData()
        val cachedData = kord.cache.query<ChannelData> { idEq(ChannelData::id, channelData.id) }.singleOrNull()

        val channel = DeletedThreadChannel(channelData, kord)
        val old = cachedData?.let { Channel.from(cachedData, kord) }
        val coreEvent = when (channel.type) {
            is ChannelType.PublicNewsThread -> NewsChannelThreadDeleteEvent(
                channel,
                old as? NewsChannelThread,
                context?.get(),
            )
            is ChannelType.PrivateThread,
            is ChannelType.GuildText -> TextChannelThreadDeleteEvent(channel, old as? TextChannelThread, context?.get())
            else -> UnknownChannelThreadDeleteEvent(channel, old as? ThreadChannel, context?.get())
        }

        kord.cache.remove<ChannelData> { idEq(ChannelData::id, channel.id) }
        return coreEvent
    }

    private suspend fun handle(event: ThreadListSync, kord: Kord, context: LazyContext?): ThreadListSyncEvent {
        val data = ThreadListSyncData.from(event)

        data.threads.forEach { thread ->
            kord.cache.put(thread)
        }
        data.members.forEach { member ->
            kord.cache.put(member)
        }

        return ThreadListSyncEvent(data, kord, context?.get())
    }

    private suspend fun handle(event: ThreadMemberUpdate, kord: Kord, context: LazyContext?): ThreadMemberUpdateEvent {
        val data = ThreadMemberData.from(event.member)
        val member = ThreadMember(data, kord)
        return ThreadMemberUpdateEvent(member, kord, context?.get())
    }

    private suspend fun handle(event: ThreadMembersUpdate, kord: Kord, context: LazyContext?): ThreadMembersUpdateEvent {
        val data = ThreadMembersUpdateEventData.from(event)
        for (removedMemberId in data.removedMemberIds.orEmpty()) {
            kord.cache.remove<ThreadMemberData> {
                idEq(ThreadMemberData::userId, removedMemberId)
                idEq(ThreadMemberData::id, data.id)
            }
        }
        for (addedMember in data.addedMembers.orEmpty()) {
            kord.cache.put(addedMember)
        }
        return ThreadMembersUpdateEvent(data, kord, context?.get())
    }
}
