package dev.jombi.kordsb.core.gateway.handler

import dev.jombi.kordsb.core.Kord
import dev.jombi.kordsb.core.event.guild.WebhookUpdateEvent
import dev.jombi.kordsb.gateway.Event
import dev.jombi.kordsb.gateway.WebhooksUpdate
import dev.jombi.kordsb.core.event.Event as CoreEvent

internal class WebhookEventHandler : BaseGatewayEventHandler() {

    override suspend fun handle(event: Event, kord: Kord, context: LazyContext?): CoreEvent? =
        when (event) {
            is WebhooksUpdate -> handle(event, kord, context)
            else -> null
        }

    private suspend fun handle(event: WebhooksUpdate, kord: Kord, context: LazyContext?): WebhookUpdateEvent =
        with(event.webhooksUpdateData) {
            return WebhookUpdateEvent(guildId, channelId, kord, context?.get())
        }

}
