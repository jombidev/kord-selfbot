package dev.jombi.kordsb.core.gateway.handler

import dev.jombi.kordsb.core.Kord
import dev.jombi.kordsb.core.cache.data.UserData
import dev.jombi.kordsb.core.entity.User
import dev.jombi.kordsb.core.event.gateway.ConnectEvent
import dev.jombi.kordsb.core.event.gateway.DisconnectEvent
import dev.jombi.kordsb.core.event.gateway.ReadyEvent
import dev.jombi.kordsb.core.event.gateway.ResumedEvent
import dev.jombi.kordsb.gateway.*
import dev.kord.cache.api.put
import dev.jombi.kordsb.core.event.Event as CoreEvent

internal class LifeCycleEventHandler : BaseGatewayEventHandler() {

    override suspend fun handle(event: Event, kord: Kord, context: LazyContext?): CoreEvent? =
        when (event) {
            is Ready -> handle(event, kord, context)
            is Resumed -> ResumedEvent(kord, context?.get())
            Reconnect -> ConnectEvent(kord, context?.get())
            is Close -> when (event) {
                Close.Detach -> DisconnectEvent.DetachEvent(kord, context?.get())
                Close.UserClose -> DisconnectEvent.UserCloseEvent(kord, context?.get())
                Close.Timeout -> DisconnectEvent.TimeoutEvent(kord, context?.get())
                is Close.DiscordClose -> DisconnectEvent.DiscordCloseEvent(
                    kord,
                    event.closeCode,
                    event.recoverable,
                    context?.get()
                )
                Close.Reconnecting -> DisconnectEvent.ReconnectingEvent(kord, context?.get())
                Close.ZombieConnection -> DisconnectEvent.ZombieConnectionEvent(kord, context?.get())
                Close.RetryLimitReached -> DisconnectEvent.RetryLimitReachedEvent(kord, context?.get())
                Close.SessionReset -> DisconnectEvent.SessionReset(kord, context?.get())
            }
            else -> null
        }

    private suspend fun handle(event: Ready, kord: Kord, context: LazyContext?): ReadyEvent =
        with(event.data) {
            val guilds = guilds.map { it.id }.toSet()
            val self = UserData.from(event.data.user)

            kord.cache.put(self)

            return ReadyEvent(
                event.data.version,
                guilds,
                User(self, kord),
                sessionId,
                kord,
                context?.get()
            )
        }
}
