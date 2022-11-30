package dev.jombi.kordsb.core.gateway.handler

import dev.jombi.kordsb.core.Kord
import dev.jombi.kordsb.core.cache.data.VoiceStateData
import dev.jombi.kordsb.core.cache.data.id
import dev.jombi.kordsb.core.cache.idEq
import dev.jombi.kordsb.core.entity.VoiceState
import dev.jombi.kordsb.core.event.guild.VoiceServerUpdateEvent
import dev.jombi.kordsb.core.event.user.VoiceStateUpdateEvent
import dev.jombi.kordsb.gateway.Event
import dev.jombi.kordsb.gateway.VoiceServerUpdate
import dev.jombi.kordsb.gateway.VoiceStateUpdate
import dev.kord.cache.api.put
import dev.kord.cache.api.query
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.singleOrNull
import dev.jombi.kordsb.core.event.Event as CoreEvent

internal class VoiceEventHandler : BaseGatewayEventHandler() {

    override suspend fun handle(event: Event, kord: Kord, context: LazyContext?): CoreEvent? =
        when (event) {
            is VoiceStateUpdate -> handle(event, kord, context)
            is VoiceServerUpdate -> handle(event, kord, context)
            else -> null
        }

    private suspend fun handle(
        event: VoiceStateUpdate,
        kord: Kord,
        context: LazyContext?,
    ): VoiceStateUpdateEvent {
        val data = VoiceStateData.from(event.voiceState.guildId.value!!, event.voiceState)

        val old = kord.cache.query<VoiceStateData> { idEq(VoiceStateData::id, data.id) }
            .asFlow().map { VoiceState(it, kord) }.singleOrNull()

        kord.cache.put(data)
        val new = VoiceState(data, kord)

        return VoiceStateUpdateEvent(old, new, context?.get())
    }

    private suspend fun handle(
        event: VoiceServerUpdate,
        kord: Kord,
        context: LazyContext?,
    ): VoiceServerUpdateEvent =
        with(event.voiceServerUpdateData) {
            return VoiceServerUpdateEvent(token, guildId, endpoint, kord, context?.get())
        }

}
