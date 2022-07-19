package dev.jombi.kordsb.voice.handlers

import dev.jombi.kordsb.voice.gateway.Close
import dev.jombi.kordsb.voice.gateway.Ready
import dev.jombi.kordsb.voice.gateway.SessionDescription
import dev.jombi.kordsb.voice.gateway.VoiceEvent
import dev.jombi.kordsb.voice.gateway.handler.GatewayEventHandler
import dev.jombi.kordsb.voice.streams.Streams
import io.ktor.network.sockets.*
import kotlinx.atomicfu.AtomicRef
import kotlinx.atomicfu.atomic
import kotlinx.coroutines.Job
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

internal class StreamsHandler(
    flow: Flow<VoiceEvent>,
    private val streams: Streams,
) : GatewayEventHandler(flow, "HandshakeHandler") {
    private val server: AtomicRef<SocketAddress?> = atomic(null)

    private var streamsJob: Job? by atomic(null)

    @OptIn(ExperimentalUnsignedTypes::class)
    override suspend fun start() = coroutineScope {
        on<Ready> {
            server.value = InetSocketAddress(it.ip, it.port)
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