import dev.jombi.kordsb.core.Kord
import dev.jombi.kordsb.core.event.gateway.ReadyEvent
import dev.jombi.kordsb.core.on
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable
import kotlin.test.assertEquals

@EnabledIfEnvironmentVariable(named = "KORD_TEST_TOKEN", matches = ".+")
internal class KordTest {

    @Test
    fun `Kord life cycle is correctly ended on shutdown`() = runBlocking {
        val kord = Kord.restOnly(System.getenv("KORD_TEST_TOKEN"))
        val job = kord.on<ReadyEvent> {}
        kord.shutdown()
        assertEquals(false, job.isActive)

    }
}
