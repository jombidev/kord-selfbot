package dev.kord.core.gateway.handler

import dev.kord.cache.api.DataCache
import dev.kord.cache.api.put
import dev.kord.core.Kord
import dev.kord.core.cache.data.UserData
import dev.kord.core.entity.User
import dev.kord.core.event.gateway.ConnectEvent
import dev.kord.core.event.gateway.DisconnectEvent
import dev.kord.core.event.gateway.ReadyEvent
import dev.kord.core.event.gateway.ResumedEvent
import dev.kord.gateway.*
import kotlinx.coroutines.CoroutineScope
import dev.kord.core.event.Event as CoreEvent

internal class LifeCycleEventHandler(
    cache: DataCache
) : BaseGatewayEventHandler(cache) {

    override suspend fun handle(event: Event, kord: Kord, coroutineScope: CoroutineScope): CoreEvent? =
        when (event) {
            is Ready -> handle(event, kord, coroutineScope)
            is Resumed -> ResumedEvent(kord, coroutineScope)
            Reconnect -> ConnectEvent(kord, coroutineScope)
            is Close -> when (event) {
                Close.Detach -> DisconnectEvent.DetachEvent(kord, coroutineScope)
                Close.UserClose -> DisconnectEvent.UserCloseEvent(kord, coroutineScope)
                Close.Timeout -> DisconnectEvent.TimeoutEvent(kord, coroutineScope)
                is Close.DiscordClose -> DisconnectEvent.DiscordCloseEvent(
                    kord,
                    event.closeCode,
                    event.recoverable,
                    coroutineScope
                )
                Close.Reconnecting -> DisconnectEvent.ReconnectingEvent(kord, coroutineScope)
                Close.ZombieConnection -> DisconnectEvent.ZombieConnectionEvent(kord, coroutineScope)
                Close.RetryLimitReached -> DisconnectEvent.RetryLimitReachedEvent(kord, coroutineScope)
                Close.SessionReset -> DisconnectEvent.SessionReset(kord, coroutineScope)
            }
            else -> null
        }

    private suspend fun handle(event: Ready, kord: Kord, coroutineScope: CoroutineScope): ReadyEvent =
        with(event.data) {
            val guilds = guilds.map { it.id }.toSet()
            val self = UserData.from(event.data.user)

            cache.put(self)

            return ReadyEvent(
                event.data.version,
                guilds,
                User(self, kord),
                sessionId,
                kord,
                coroutineScope = coroutineScope
            )
        }
}
