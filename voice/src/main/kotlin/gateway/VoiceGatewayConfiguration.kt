package dev.jombi.kordsb.voice.gateway

import dev.jombi.kordsb.common.annotation.KordVoice

@KordVoice
public data class VoiceGatewayConfiguration(
    val token: String,
    val endpoint: String
)