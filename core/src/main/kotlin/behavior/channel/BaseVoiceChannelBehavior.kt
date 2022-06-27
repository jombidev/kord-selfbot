package dev.jombi.kordsb.core.behavior.channel

import dev.kord.cache.api.query
import dev.jombi.kordsb.common.exception.RequestException
import dev.jombi.kordsb.core.Kord
import dev.jombi.kordsb.core.cache.data.VoiceStateData
import dev.jombi.kordsb.core.cache.idEq
import dev.jombi.kordsb.core.entity.VoiceState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

public interface BaseVoiceChannelBehavior : CategorizableChannelBehavior {

    /**
     * Requests to retrieve the present voice states of this channel.
     *
     * This property is not resolvable through REST and will always use [Kord.cache] instead.
     *
     * The returned flow is lazily executed, any [RequestException] will be thrown on
     * [terminal operators](https://kotlinlang.org/docs/reference/coroutines/flow.html#terminal-flow-operators) instead.
     */
    public val voiceStates: Flow<VoiceState>
        get() = kord.cache.query<VoiceStateData> { idEq(VoiceStateData::channelId, id) }
            .asFlow()
            .map { VoiceState(it, kord) }
}
