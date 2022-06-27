package dev.jombi.kordsb.core.behavior

import dev.jombi.kordsb.common.entity.Snowflake
import equality.GuildEntityEqualityTest
import mockKord
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

internal class RoleBehaviorTest : GuildEntityEqualityTest<RoleBehavior> by GuildEntityEqualityTest({ id, guildId ->
    val kord = mockKord()
    RoleBehavior(guildId = guildId, id = id, kord = kord)
}) {

    @Test
    fun `everyone role mention is properly formatted`(){
        val kord = mockKord()

        val id = Snowflake(1337u)
        val behavior = RoleBehavior(id, id, kord)

        assertEquals("@everyone", behavior.mention)
    }

}
