package dev.jombi.kordsb.core.gateway.handler

import dev.kord.cache.api.DataCache
import dev.kord.cache.api.put
import dev.kord.cache.api.query
import dev.kord.cache.api.remove
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
import kotlinx.coroutines.CoroutineScope
import dev.jombi.kordsb.core.event.Event as CoreEvent

internal class ChannelEventHandler(
    cache: DataCache
) : BaseGatewayEventHandler(cache) {

    override suspend fun handle(
        event: Event,
        kord: Kord,
        coroutineScope: CoroutineScope
    ): dev.jombi.kordsb.core.event.Event? = when (event) {
        is ChannelCreate -> handle(event, kord, coroutineScope)
        is ChannelUpdate -> handle(event, kord, coroutineScope)
        is ChannelDelete -> handle(event, kord, coroutineScope)
        is ChannelPinsUpdate -> handle(event, kord, coroutineScope)
        is TypingStart -> handle(event, kord, coroutineScope)
        else -> null
    }

    private suspend fun handle(event: ChannelCreate, kord: Kord, coroutineScope: CoroutineScope): CoreEvent? {
        val data = ChannelData.from(event.channel)
        cache.put(data)

        val coreEvent = when (val channel = Channel.from(data, kord)) {
            is NewsChannel -> NewsChannelCreateEvent(channel, coroutineScope)
            is @Suppress("DEPRECATION") StoreChannel -> @Suppress("DEPRECATION") StoreChannelCreateEvent(channel, coroutineScope)
            is DmChannel -> DMChannelCreateEvent(channel, coroutineScope)
            is TextChannel -> TextChannelCreateEvent(channel, coroutineScope)
            is StageChannel -> StageChannelCreateEvent(channel, coroutineScope)
            is VoiceChannel -> VoiceChannelCreateEvent(channel, coroutineScope)
            is Category -> CategoryCreateEvent(channel, coroutineScope)
            is ThreadChannel -> return null
            else -> UnknownChannelCreateEvent(channel, coroutineScope)

        }

        return coreEvent
    }

    private suspend fun handle(event: ChannelUpdate, kord: Kord, coroutineScope: CoroutineScope): CoreEvent? {
        val data = ChannelData.from(event.channel)
        val oldData = cache.query<ChannelData> { idEq(ChannelData::id, data.id) }.singleOrNull()
        cache.put(data)
        val old = oldData?.let { Channel.from(it, kord) }
        val coreEvent = when (val channel = Channel.from(data, kord)) {
            is NewsChannel -> NewsChannelUpdateEvent(channel, old as? NewsChannel, coroutineScope)
            is @Suppress("DEPRECATION") StoreChannel -> @Suppress("DEPRECATION") StoreChannelUpdateEvent(channel, old as? StoreChannel, coroutineScope)
            is DmChannel -> DMChannelUpdateEvent(channel, old as? DmChannel, coroutineScope)
            is TextChannel -> TextChannelUpdateEvent(channel, old as? TextChannel, coroutineScope)
            is StageChannel -> StageChannelUpdateEvent(channel, old as? StageChannel, coroutineScope)
            is VoiceChannel -> VoiceChannelUpdateEvent(channel, old as? VoiceChannel, coroutineScope)
            is Category -> CategoryUpdateEvent(channel, old as? Category, coroutineScope)
            is ThreadChannel -> return null
            else -> UnknownChannelUpdateEvent(channel, old, coroutineScope)

        }

        return coreEvent
    }

    private suspend fun handle(event: ChannelDelete, kord: Kord, coroutineScope: CoroutineScope): CoreEvent? {
        cache.remove<ChannelData> { idEq(ChannelData::id, event.channel.id) }
        val data = ChannelData.from(event.channel)

        val coreEvent = when (val channel = Channel.from(data, kord)) {
            is NewsChannel -> NewsChannelDeleteEvent(channel, coroutineScope)
            is @Suppress("DEPRECATION") StoreChannel -> @Suppress("DEPRECATION") StoreChannelDeleteEvent(channel, coroutineScope)
            is DmChannel -> DMChannelDeleteEvent(channel, coroutineScope)
            is TextChannel -> TextChannelDeleteEvent(channel, coroutineScope)
            is StageChannel -> StageChannelDeleteEvent(channel, coroutineScope)
            is VoiceChannel -> VoiceChannelDeleteEvent(channel, coroutineScope)
            is Category -> CategoryDeleteEvent(channel, coroutineScope)
            is ThreadChannel -> return null
            else -> UnknownChannelDeleteEvent(channel, coroutineScope)
        }

        return coreEvent
    }

    private suspend fun handle(
        event: ChannelPinsUpdate,
        kord: Kord,
        coroutineScope: CoroutineScope
    ): ChannelPinsUpdateEvent =
        with(event.pins) {
            val coreEvent = ChannelPinsUpdateEvent(
                ChannelPinsUpdateEventData.from(this),
                kord,
                coroutineScope = coroutineScope
            )

            cache.query<ChannelData> { idEq(ChannelData::id, channelId) }.update {
                it.copy(lastPinTimestamp = lastPinTimestamp)
            }

            return coreEvent
        }

    private suspend fun handle(
        event: TypingStart,
        kord: Kord,
        coroutineScope: CoroutineScope
    ): TypingStartEvent = with(event.data) {
        member.value?.let {
            cache.put(MemberData.from(userId = it.user.value!!.id, guildId = guildId.value!!, it))
        }

        return TypingStartEvent(
            TypingStartEventData.from(this),
            kord,
            coroutineScope = coroutineScope
        )
    }

}
