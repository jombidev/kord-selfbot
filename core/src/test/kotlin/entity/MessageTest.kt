package dev.jombi.kordsb.core.entity

import dev.jombi.kordsb.core.behavior.MessageBehavior
import dev.jombi.kordsb.core.cache.data.MessageData
import equality.BehaviorEqualityTest
import equality.EntityEqualityTest
import io.mockk.every
import io.mockk.mockk
import mockKord

internal class MessageTest : EntityEqualityTest<Message> by EntityEqualityTest({
    val kord = mockKord()
    val data = mockk<MessageData>()
    every { data.id } returns it
    every { data.channelId } returns it
    Message(data, kord)
}), BehaviorEqualityTest<Message> {
    override fun Message.behavior(): KordEntity = MessageBehavior(messageId = id, channelId = id, kord = kord)
}