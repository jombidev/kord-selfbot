package gateway

import dev.jombi.kordsb.common.entity.ActivityType
import dev.jombi.kordsb.common.entity.DiscordBotActivity
import dev.jombi.kordsb.common.entity.PresenceStatus
import dev.jombi.kordsb.common.ratelimit.IntervalRateLimiter
import dev.jombi.kordsb.gateway.*
import dev.jombi.kordsb.gateway.retry.LinearRetry
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.websocket.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import kotlin.time.Duration.Companion.seconds

class DefaultGatewayTest {
    @OptIn(DelicateCoroutinesApi::class)
    @Test
    @Disabled
    fun `default gateway functions correctly`() {
        val token = System.getenv("KORD_TEST_TOKEN")

        val gateway = DefaultGateway {
            client = HttpClient(CIO) {
                install(WebSockets)
                install(ContentNegotiation) {
                    json()
                }
            }

            reconnectRetry = LinearRetry(2.seconds, 20.seconds, 10)
            sendRateLimiter = IntervalRateLimiter(limit = 120, interval = 60.seconds)
        }

        gateway.events.filterIsInstance<MessageCreate>().flowOn(Dispatchers.Default).onEach {
            val words = it.message.content.split(' ')
            when (words.firstOrNull()) {
                "!close" -> gateway.stop()
                "!restart" -> gateway.restart(Close.Reconnecting)
                "!detach" -> gateway.detach()
                "!status" -> when (words.getOrNull(1)) {
                    "playing" -> gateway.send(UpdateStatus(status = PresenceStatus.Online, afk = false, activities = listOf(DiscordBotActivity("Kord", ActivityType.Game)), since = null))
                }
                "!ping" -> gateway.send(UpdateStatus(status = PresenceStatus.Online, afk = false, activities = listOf(DiscordBotActivity("Ping is ${gateway.ping.value?.inWholeMilliseconds}", ActivityType.Game)), since = null))
            }
        }.launchIn(GlobalScope)

        runBlocking {
            gateway.start(token) {
                @OptIn(PrivilegedIntent::class)
                intents = Intents.all
            }
        }
    }
}
