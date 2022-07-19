package dev.jombi.kordsb.gateway.handler

import dev.jombi.kordsb.gateway.Event
import dev.jombi.kordsb.gateway.Reconnect
import kotlinx.coroutines.flow.Flow

internal class ReconnectHandler(
    flow: Flow<Event>,
    private val reconnect: suspend () -> Unit
) : Handler(flow, "ReconnectHandler") {

    override fun start() {
        on<Reconnect> {
            reconnect()
        }
    }
}