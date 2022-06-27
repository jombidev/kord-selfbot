package dev.jombi.kordsb.core

import dev.jombi.kordsb.common.annotation.KordExperimental
import dev.jombi.kordsb.core.event.gateway.ReadyEvent
import kotlinx.coroutines.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable
import kotlin.test.assertEquals

@EnabledIfEnvironmentVariable(named = "KORD_TEST_TOKEN", matches = ".+")
internal class KordTest {

    @Test
    @OptIn(KordExperimental::class, kotlinx.coroutines.DelicateCoroutinesApi::class)
    fun `Kord life cycle is correctly ended on shutdown`() = runBlocking {
        val kord = Kord.restOnly(System.getenv("KORD_TEST_TOKEN"))
        val job = kord.on<ReadyEvent> {}
        kord.shutdown()
        assertEquals(false, job.isActive)

    }
}
