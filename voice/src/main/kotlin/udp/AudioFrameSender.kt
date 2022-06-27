@file:Suppress("ArrayInDataClass")

package dev.jombi.kordsb.voice.udp

import dev.jombi.kordsb.common.annotation.KordVoice
import dev.jombi.kordsb.voice.FrameInterceptorConfiguration
import io.ktor.network.sockets.*
import io.ktor.util.network.*

@KordVoice
public data class AudioFrameSenderConfiguration(
    val server: SocketAddress,
    val ssrc: UInt,
    val key: ByteArray,
    val interceptorConfiguration: FrameInterceptorConfiguration
)

@KordVoice
public interface AudioFrameSender {
    /**
     * This should start polling frames from [the audio provider][AudioFrameSenderConfiguration.provider] and
     * send them to Discord.
     */
    public suspend fun start(configuration: AudioFrameSenderConfiguration)
}
