package equality

import dev.jombi.kordsb.common.entity.Snowflake
import dev.jombi.kordsb.core.behavior.channel.ChannelBehavior
import dev.jombi.kordsb.core.entity.KordEntity
import mockKord
import kotlin.test.assertEquals

interface ChannelEqualityTest<T: KordEntity> : EntityEqualityTest<T> {

    @kotlin.test.Test
    fun `Channel is equal to Channel with the same id`() {
        val id = randomId()
        val kord = mockKord()
        val fakeChannel: KordEntity = ChannelBehavior(id, kord)
        val channel: KordEntity = newEntity(id)

        assertEquals(fakeChannel, channel)
    }

    companion object {
        operator fun<T: KordEntity> invoke(supplier: (Snowflake) -> T) = object: ChannelEqualityTest<T> {
            override fun newEntity(id: Snowflake): T = supplier(id)
        }
    }

}