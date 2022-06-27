package dev.jombi.kordsb.core.entity

import dev.jombi.kordsb.core.behavior.GuildBehavior
import dev.jombi.kordsb.core.cache.data.GuildData
import equality.BehaviorEqualityTest
import equality.EntityEqualityTest
import io.mockk.every
import io.mockk.mockk
import mockKord

internal class GuildTest: EntityEqualityTest<Guild> by EntityEqualityTest({
    val kord = mockKord()
    val data = mockk<GuildData>()
    every { data.id } returns it
    Guild(data, kord)
}), BehaviorEqualityTest<Guild> {
    override fun Guild.behavior(): KordEntity = GuildBehavior(id, kord)
}