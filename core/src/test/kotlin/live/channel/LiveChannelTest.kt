package live.channel

import dev.jombi.kordsb.common.annotation.KordPreview
import dev.jombi.kordsb.common.entity.ChannelType
import dev.jombi.kordsb.common.entity.DiscordChannel
import dev.jombi.kordsb.common.entity.DiscordUnavailableGuild
import dev.jombi.kordsb.common.entity.Snowflake
import dev.jombi.kordsb.core.cache.data.ChannelData
import dev.jombi.kordsb.core.entity.channel.*
import dev.jombi.kordsb.core.event.channel.ChannelDeleteEvent
import dev.jombi.kordsb.core.event.guild.GuildDeleteEvent
import dev.jombi.kordsb.core.live.channel.*
import dev.jombi.kordsb.core.live.exception.LiveCancellationException
import dev.jombi.kordsb.gateway.ChannelDelete
import dev.jombi.kordsb.gateway.GuildDelete
import equality.randomId
import kotlinx.coroutines.job
import kotlinx.coroutines.runBlocking
import live.AbstractLiveEntityTest
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.Timeout
import java.util.concurrent.TimeUnit
import kotlin.reflect.KClass
import kotlin.test.Test
import kotlin.test.assertEquals

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@OptIn(KordPreview::class)
@Timeout(value = 5, unit = TimeUnit.SECONDS)
@Disabled
abstract class LiveChannelTest<LIVE : LiveChannel> : AbstractLiveEntityTest<LIVE>() {

    protected abstract val channelId: Snowflake

    @Test
    fun `Check type of live entity corresponds to the channel type`() = runBlocking {
        val data = ChannelData(
            id = randomId(),
            type = ChannelType.DM
        )

        fun checkLiveEntityType(expectedType: KClass<*>, channel: Channel) {
            assertEquals(expectedType, channel.live()::class)
        }

        checkLiveEntityType(LiveDmChannel::class, DmChannel(kord = kord, data = data))
        checkLiveEntityType(LiveGuildMessageChannel::class, NewsChannel(kord = kord, data = data))
        checkLiveEntityType(LiveGuildMessageChannel::class, TextChannel(kord = kord, data = data))
        checkLiveEntityType(LiveVoiceChannel::class, VoiceChannel(kord = kord, data = data))
    }

    @Test
    fun `Check if live entity is completed when event the category delete event is received`() {
        countdownContext(1) {
            live.coroutineContext.job.invokeOnCompletion {
                it as LiveCancellationException
                val event = it.event as ChannelDeleteEvent
                assertEquals(channelId, event.channel.id)
                count()
            }

            sendEventValidAndRandomId(channelId) {
                ChannelDelete(
                    DiscordChannel(
                        id = it,
                        type = live.channel.type,
                    ),
                    0
                )
            }
        }
    }

    @Test
    fun `Check if live entity is completed when event the guild delete event is received`() {
        countdownContext(1) {
            live.coroutineContext.job.invokeOnCompletion {
                it as LiveCancellationException
                val event = it.event as GuildDeleteEvent
                assertEquals(guildId, event.guildId)
                count()
            }

            sendEventValidAndRandomIdCheckLiveActive(guildId) {
                GuildDelete(
                    DiscordUnavailableGuild(
                        id = it
                    ),
                    0
                )
            }
        }
    }
}
