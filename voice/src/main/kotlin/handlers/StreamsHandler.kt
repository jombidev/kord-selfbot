package dev.jombi.kordsb.voice.handlers

import dev.jombi.kordsb.common.annotation.KordVoice
import dev.jombi.kordsb.voice.gateway.Close
import dev.jombi.kordsb.voice.gateway.Ready
import dev.jombi.kordsb.voice.gateway.SessionDescription
import dev.jombi.kordsb.voice.gateway.VoiceEvent
import dev.jombi.kordsb.voice.gateway.handler.GatewayEventHandler
import dev.jombi.kordsb.voice.streams.Streams
import io.ktor.util.network.*
import kotlinx.atomicfu.AtomicRef
import kotlinx.atomicfu.atomic
import kotlinx.coroutines.Job
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

@OptIn(KordVoice::class)
internal class StreamsHandler(
    flow: Flow<VoiceEvent>,
    private val streams: Streams,
) : GatewayEventHandler(flow, "HandshakeHandler") {
    private val server: AtomicRef<NetworkAddress?> = atomic(null)

    private var streamsJob: Job? by atomic(null)

    @OptIn(ExperimentalUnsignedTypes::class)
    override suspend fun start() = coroutineScope {
        on<Ready> {
            server.value = NetworkAddress(it.ip, it.port)
        }

        on<SessionDescription> {
            streamsJob?.cancel()
            streamsJob = launch { streams.listen(it.secretKey.toUByteArray().toByteArray(), server.value!!) }
        }

        on<Close> {
            streamsJob?.cancel()
        }
    }
}