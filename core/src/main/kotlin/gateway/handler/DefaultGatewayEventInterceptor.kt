package dev.jombi.kordsb.core.gateway.handler

import dev.kord.cache.api.DataCache
import dev.jombi.kordsb.core.Kord
import dev.jombi.kordsb.core.event.kordCoroutineScope
import dev.jombi.kordsb.core.gateway.ShardEvent
import io.ktor.util.logging.*
import kotlinx.coroutines.CoroutineScope
import mu.KotlinLogging
import dev.jombi.kordsb.core.event.Event as CoreEvent

private val logger = KotlinLogging.logger { }

public class DefaultGatewayEventInterceptor(
    cache: DataCache,
    private val eventScope: ((ShardEvent, Kord) -> CoroutineScope) = { _, kord -> kordCoroutineScope(kord) }
) : GatewayEventInterceptor {

    private val listeners = listOf(
        MessageEventHandler(cache),
        ChannelEventHandler(cache),
        ThreadEventHandler(cache),
        GuildEventHandler(cache),
        LifeCycleEventHandler(cache),
        UserEventHandler(cache),
        VoiceEventHandler(cache),
        WebhookEventHandler(cache),
        InteractionEventHandler(cache)
    )

    override suspend fun handle(event: ShardEvent, kord: Kord): CoreEvent? {
        return runCatching {
            for (listener in listeners) {
                val coreEvent = listener.handle(
                    event.event,
                    kord,
                    eventScope.invoke(event, kord)
                )
                if (coreEvent != null) {
                    return coreEvent
                }
            }
            return null
        }.onFailure {
            logger.error(it)
        }.getOrNull()
    }
}
