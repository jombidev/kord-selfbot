package dev.jombi.kordsb.core.live.exception

import dev.jombi.kordsb.core.event.Event
import java.util.concurrent.CancellationException

public class LiveCancellationException(public val event: Event, message: String? = null) :
    CancellationException(message)
