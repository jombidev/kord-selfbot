package dev.jombi.kordsb.voice.streams

import dev.jombi.kordsb.common.annotation.KordVoice
import dev.jombi.kordsb.common.entity.Snowflake
import dev.jombi.kordsb.voice.AudioFrame
import dev.jombi.kordsb.voice.udp.RTPPacket
import io.ktor.util.network.*
import kotlinx.coroutines.flow.Flow

/**
 * A representation of receiving voice through Discord and different stages of processing.
 */
@KordVoice
public interface Streams {
    /**
     * Starts propagating packets from [server] with the following [key] to decrypt the incoming frames.
     */
    public suspend fun listen(key: ByteArray, server: NetworkAddress)

    /**
     * A flow of all incoming [dev.jombi.kordsb.voice.udp.RTPPacket]s through the UDP connection.
     */
    public val incomingAudioPackets: Flow<RTPPacket>

    /**
     * A flow of all incoming [AudioFrame]s mapped to their [ssrc][UInt].
     */
    public val incomingAudioFrames: Flow<Pair<UInt, AudioFrame>>

    /**
     * A flow of all incoming [AudioFrame]s mapped to their [userId][Snowflake].
     * Streams for every user should be built over time and will not be immediately available.
     */
    public val incomingUserStreams: Flow<Pair<Snowflake, AudioFrame>>

    /**
     * A map of [ssrc][UInt]s to their corresponding [userId][Snowflake].
     */
    public val ssrcToUser: Map<UInt, Snowflake>
}