package dev.jombi.kordsb.voice.gateway.handler

import dev.jombi.kordsb.voice.gateway.*
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow

internal class HandshakeHandler(
    flow: Flow<VoiceEvent>,
    private val data: DefaultVoiceGatewayData,
    private val send: suspend (Command) -> Unit
) : GatewayEventHandler(flow, "HandshakeHandler") {
    lateinit var configuration: VoiceGatewayConfiguration

    private val identify
        get() = Identify(
            data.guildId,
            data.selfId,
            data.sessionId,
            configuration.token
        )

    override suspend fun start() = coroutineScope {
        on<Hello> {
            data.reconnectRetry.reset()
            send(identify)
        }
    }
}