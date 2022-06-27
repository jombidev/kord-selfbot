package dev.jombi.kordsb.gateway.handler

import dev.jombi.kordsb.gateway.Close
import dev.jombi.kordsb.gateway.DispatchEvent
import dev.jombi.kordsb.gateway.Event
import dev.jombi.kordsb.gateway.Sequence
import kotlinx.coroutines.flow.Flow

internal class SequenceHandler(
    flow: Flow<Event>,
    private val sequence: Sequence
) : Handler(flow, "SequenceHandler") {

    init {
        on<DispatchEvent> { event ->
            sequence.value = event.sequence ?: sequence.value
        }

        on<Close.SessionReset> {
            sequence.value = null
        }
    }

}