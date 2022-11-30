package dev.jombi.kordsb.core.gateway.handler

import dev.jombi.kordsb.core.Kord
import dev.jombi.kordsb.core.cache.data.ChannelData
import dev.jombi.kordsb.core.cache.data.MemberData
import dev.jombi.kordsb.core.cache.idEq
import dev.jombi.kordsb.core.entity.channel.*
import dev.jombi.kordsb.core.entity.channel.thread.ThreadChannel
import dev.jombi.kordsb.core.event.channel.*
import dev.jombi.kordsb.core.event.channel.data.ChannelPinsUpdateEventData
import dev.jombi.kordsb.core.event.channel.data.TypingStartEventData
import dev.jombi.kordsb.gateway.*
import dev.kord.cache.api.put
import dev.kord.cache.api.query
import dev.kord.cache.api.remove

internal class ChannelEventHandler : BaseGatewayEventHandler() {

    override suspend fun handle(
        event: Event,
        kord: Kord,
        context: LazyContext?,
    ): dev.jombi.kordsb.core.event.Event? = when (event) {
        is ChannelCreate -> handle(event, kord, context)
        is ChannelUpdate -> handle(event, kord, context)
        is ChannelDelete -> handle(event, kord, context)
        is ChannelPinsUpdate -> handle(event, kord, context)
        is TypingStart -> handle(event, kord, context)
        else -> null
    }

    private suspend fun handle(event: ChannelCreate, kord: Kord, context: LazyContext?): ChannelCreateEvent? {
        val data = ChannelData.from(event.channel)
            kord.cache.put(data)

        val coreEvent = when (val channel = Channel.from(data, kord)) {
            is NewsChannel -> NewsChannelCreateEvent(channel, context?.get())
            is @Suppress("DEPRECATION_ERROR") StoreChannel -> @Suppress("DEPRECATION_ERROR") StoreChannelCreateEvent(channel, context?.get())
            is DmChannel -> DMChannelCreateEvent(channel, context?.get())
            is TextChannel -> TextChannelCreateEvent(channel, context?.get())
            is StageChannel -> StageChannelCreateEvent(channel, context?.get())
            is VoiceChannel -> VoiceChannelCreateEvent(channel, context?.get())
            is Category -> CategoryCreateEvent(channel, context?.get())
            is ThreadChannel -> return null
            else -> UnknownChannelCreateEvent(channel, context?.get())

        }

        return coreEvent
    }

    private suspend fun handle(event: ChannelUpdate, kord: Kord, context: LazyContext?): ChannelUpdateEvent? {
        val data = ChannelData.from(event.channel)
        val oldData = kord.cache.query<ChannelData> { idEq(ChannelData::id, data.id) }.singleOrNull()
        kord.cache.put(data)
        val old = oldData?.let { Channel.from(it, kord) }
        val coreEvent = when (val channel = Channel.from(data, kord)) {
            is NewsChannel -> NewsChannelUpdateEvent(channel, old as? NewsChannel, context?.get())
            is @Suppress("DEPRECATION_ERROR") StoreChannel -> @Suppress("DEPRECATION_ERROR") StoreChannelUpdateEvent(channel, old as? StoreChannel, context?.get())
            is DmChannel -> DMChannelUpdateEvent(channel, old as? DmChannel, context?.get())
            is TextChannel -> TextChannelUpdateEvent(channel, old as? TextChannel, context?.get())
            is StageChannel -> StageChannelUpdateEvent(channel, old as? StageChannel, context?.get())
            is VoiceChannel -> VoiceChannelUpdateEvent(channel, old as? VoiceChannel, context?.get())
            is Category -> CategoryUpdateEvent(channel, old as? Category, context?.get())
            is ThreadChannel -> return null
            else -> UnknownChannelUpdateEvent(channel, old, context?.get())

        }

        return coreEvent
    }

    private suspend fun handle(event: ChannelDelete, kord: Kord, context: LazyContext?): ChannelDeleteEvent? {
        kord.cache.remove<ChannelData> { idEq(ChannelData::id, event.channel.id) }
        val data = ChannelData.from(event.channel)

        val coreEvent = when (val channel = Channel.from(data, kord)) {
            is NewsChannel -> NewsChannelDeleteEvent(channel, context?.get())
            is @Suppress("DEPRECATION_ERROR") StoreChannel -> @Suppress("DEPRECATION_ERROR") StoreChannelDeleteEvent(channel, context?.get())
            is DmChannel -> DMChannelDeleteEvent(channel, context?.get())
            is TextChannel -> TextChannelDeleteEvent(channel, context?.get())
            is StageChannel -> StageChannelDeleteEvent(channel, context?.get())
            is VoiceChannel -> VoiceChannelDeleteEvent(channel, context?.get())
            is Category -> CategoryDeleteEvent(channel, context?.get())
            is ThreadChannel -> return null
            else -> UnknownChannelDeleteEvent(channel, context?.get())
        }

        return coreEvent
    }

    private suspend fun handle(
        event: ChannelPinsUpdate,
        kord: Kord,
        context: LazyContext?,
    ): ChannelPinsUpdateEvent =
        with(event.pins) {
            val coreEvent = ChannelPinsUpdateEvent(ChannelPinsUpdateEventData.from(this), kord, context?.get())

            kord.cache.query<ChannelData> { idEq(ChannelData::id, channelId) }.update {
                it.copy(lastPinTimestamp = lastPinTimestamp)
            }

            coreEvent
        }

    private suspend fun handle(
        event: TypingStart,
        kord: Kord,
        context: LazyContext?,
    ): TypingStartEvent = with(event.data) {
        member.value?.let {
            kord.cache.put(MemberData.from(userId = it.user.value!!.id, guildId = guildId.value!!, it))
        }

        TypingStartEvent(TypingStartEventData.from(this), kord, context?.get())
    }

}
