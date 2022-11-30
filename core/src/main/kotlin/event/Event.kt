package dev.jombi.kordsb.core.event

import dev.jombi.kordsb.common.annotation.KordPreview
import dev.jombi.kordsb.core.Kord
import dev.jombi.kordsb.gateway.Gateway

public interface Event {
    /**
     * The Gateway that spawned this event.
     */
    public val gateway: Gateway get() = kord.gateway.gateway

    public val kord: Kord

    /**
     * A custom object that can be inserted when creating events. By default, this is just `null`.
     *
     * This can be used to associate a custom context with an event, e.g. like this:
     * ```kotlin
     * class YourCustomContext(...)
     *
     * val kord = Kord(token) {
     *     gatewayEventInterceptor = DefaultGatewayEventInterceptor(
     *         customContextCreator = { event, kord -> YourCustomContext(...) }
     *     )
     * }
     *
     * kord.on<MessageCreateEvent> {
     *     // receive the value previously set when creating the event
     *     val context = customContext as YourCustomContext
     *     // ...
     * }
     *
     * kord.login()
     * ```
     *
     * Note that [withStrategy][Strategizable.withStrategy] for [strategizable][Strategizable] [Event]s will copy
     * [customContext] only by reference. This should be considered when inserting mutable objects into [customContext].
     */
    @KordPreview
    public val customContext: Any?
}
