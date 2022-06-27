package dev.jombi.kordsb.core.behavior

import dev.jombi.kordsb.common.entity.Snowflake
import equality.EntityEqualityTest
import mockKord

internal class MessageBehaviorTest : EntityEqualityTest<MessageBehavior> by EntityEqualityTest({
    val kord = mockKord()
    MessageBehavior(it, Snowflake(0u), kord)
})
