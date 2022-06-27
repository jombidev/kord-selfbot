package dev.jombi.kordsb.core.gateway.handler

import dev.kord.cache.api.DataCache
import dev.jombi.kordsb.core.Kord
import dev.jombi.kordsb.core.event.guild.WebhookUpdateEvent
import dev.jombi.kordsb.gateway.Event
import dev.jombi.kordsb.gateway.WebhooksUpdate
import kotlinx.coroutines.CoroutineScope
import dev.jombi.kordsb.core.event.Event as CoreEvent

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
