package dev.kord.core.gateway.handler

import dev.kord.cache.api.DataCache
import dev.kord.core.Kord
import dev.kord.core.event.guild.WebhookUpdateEvent
import dev.kord.gateway.Event
import dev.kord.gateway.WebhooksUpdate
import kotlinx.coroutines.CoroutineScope
import dev.kord.core.event.Event as CoreEvent

internal class WebhookEventHandler(
    cache: DataCache
) : BaseGatewayEventHandler(cache) {

    override suspend fun handle(event: Event, kord: Kord, coroutineScope: CoroutineScope): CoreEvent? =
        when (event) {
            is WebhooksUpdate -> handle(event, kord, coroutineScope)
            else -> null
        }

    private fun handle(event: WebhooksUpdate, kord: Kord, coroutineScope: CoroutineScope): WebhookUpdateEvent =
        with(event.webhooksUpdateData) {
            return WebhookUpdateEvent(guildId, channelId, kord, coroutineScope = coroutineScope)
        }

}
