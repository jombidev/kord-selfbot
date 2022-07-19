package live.channel

import dev.jombi.kordsb.common.entity.ChannelType
import dev.jombi.kordsb.common.entity.DiscordChannel
import dev.jombi.kordsb.common.entity.Snowflake
import dev.jombi.kordsb.common.entity.optional.optionalSnowflake
import dev.jombi.kordsb.core.Kord
import dev.jombi.kordsb.core.cache.data.ChannelData
import dev.jombi.kordsb.core.entity.channel.TopGuildMessageChannel
import dev.jombi.kordsb.core.live.channel.LiveGuildChannel
import dev.jombi.kordsb.core.live.channel.onUpdate
import dev.jombi.kordsb.core.supplier.EntitySupplier
import dev.jombi.kordsb.core.supplier.EntitySupplyStrategy
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
@Timeout(value = 5, unit = TimeUnit.SECONDS)
@Disabled
class LiveGuildChannelTest : LiveChannelTest<LiveGuildChannel>() {

    inner class GuildChannelMock(
        override val kord: Kord,
        override val data: ChannelData,
        override val supplier: EntitySupplier = kord.defaultSupplier
    ) : TopGuildMessageChannel {
        override fun withStrategy(strategy: EntitySupplyStrategy<*>): TopGuildMessageChannel {
            error("Not invoked in test")
        }
    }

    override lateinit var channelId: Snowflake

    @BeforeAll
    override fun onBeforeAll() {
        super.onBeforeAll()
        channelId = randomId()
    }

    @BeforeTest
    fun onBefore() = runBlocking {
        live = LiveGuildChannel(
            GuildChannelMock(
                kord = kord,
                data = ChannelData(
                    id = channelId,
                    type = ChannelType.GuildText,
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
                        type = ChannelType.GuildText,
                    ),
                    0
                )
            }
        }
    }
}