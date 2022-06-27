package live.channel

import dev.jombi.kordsb.common.annotation.KordPreview
import dev.jombi.kordsb.common.entity.ChannelType
import dev.jombi.kordsb.common.entity.DiscordChannel
import dev.jombi.kordsb.common.entity.Snowflake
import dev.jombi.kordsb.common.entity.optional.optionalSnowflake
import dev.jombi.kordsb.core.cache.data.ChannelData
import dev.jombi.kordsb.core.entity.channel.VoiceChannel
import dev.jombi.kordsb.core.live.channel.LiveVoiceChannel
import dev.jombi.kordsb.core.live.channel.onUpdate
import dev.jombi.kordsb.gateway.ChannelUpdate
import equality.randomId
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.Timeout
import java.util.concurrent.TimeUnit
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@OptIn(KordPreview::class)
@Timeout(value = 5, unit = TimeUnit.SECONDS)
@Disabled
class LiveVoiceChannelTest : LiveChannelTest<LiveVoiceChannel>() {

    override lateinit var channelId: Snowflake

    @BeforeAll
    override fun onBeforeAll() {
        super.onBeforeAll()
        channelId = randomId()
    }

    @BeforeTest
    fun onBefore() = runBlocking {
        live = LiveVoiceChannel(
            VoiceChannel(
                kord = kord,
                data = ChannelData(
                    id = channelId,
                    type = ChannelType.GuildVoice,
                    guildId = guildId.optionalSnowflake()
                )
            )
        )
    }

    @Test
    fun `Check onUpdate is called when event is received`() {
        countdownContext(1) {
            live.onUpdate {
                assertEquals(it.channel.id, channelId)
                count()
            }

            sendEventValidAndRandomId(channelId) {
                ChannelUpdate(
                    DiscordChannel(
                        id = it,
                        type = ChannelType.GuildVoice,
                    ),
                    0
                )
            }
        }
    }
}