package dev.jombi.kordsb.core.entity

import dev.jombi.kordsb.core.behavior.WebhookBehavior
import dev.jombi.kordsb.core.cache.data.WebhookData
import equality.BehaviorEqualityTest
import equality.EntityEqualityTest
import io.mockk.every
import io.mockk.mockk
import mockKord

internal class WebhookTest : EntityEqualityTest<Webhook> by EntityEqualityTest({
    val kord = mockKord()
    val data = mockk<WebhookData>()
    every { data.id } returns it
    Webhook(data, kord)
}), BehaviorEqualityTest<Webhook> {
    override fun Webhook.behavior(): KordEntity = WebhookBehavior(id = id, kord = kord)
}