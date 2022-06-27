package dev.jombi.kordsb.voice.streams

import dev.jombi.kordsb.common.annotation.KordVoice
import dev.jombi.kordsb.common.entity.Snowflake
import dev.jombi.kordsb.voice.AudioFrame
import dev.jombi.kordsb.voice.udp.RTPPacket
import io.ktor.util.network.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

@KordVoice
public object NOPStreams : Streams {
    override suspend fun listen(key: ByteArray, server: NetworkAddress) {}

    override val incomingAudioPackets: Flow<RTPPacket> = flow { }
    override val incomingAudioFrames: Flow<Pair<UInt, AudioFrame>> = flow { }
    override val incomingUserStreams: Flow<Pair<Snowflake, AudioFrame>> = flow { }
    override val ssrcToUser: Map<UInt, Snowflake> = emptyMap()
}