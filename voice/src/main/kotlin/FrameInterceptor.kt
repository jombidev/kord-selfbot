package dev.jombi.kordsb.voice

import dev.jombi.kordsb.common.annotation.KordVoice
import dev.jombi.kordsb.gateway.Gateway
import dev.jombi.kordsb.voice.gateway.VoiceGateway
import kotlinx.coroutines.flow.Flow

@KordVoice
public data class FrameInterceptorConfiguration(
    val gateway: Gateway,
    val voiceGateway: VoiceGateway,
    val ssrc: UInt
)

/**
 * An interceptor for audio frames before they are sent as packets.
 *
 * @see DefaultFrameInterceptor
 */
@KordVoice
public fun interface FrameInterceptor {
    public fun Flow<AudioFrame?>.intercept(configuration: FrameInterceptorConfiguration): Flow<AudioFrame?>
}