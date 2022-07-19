package dev.jombi.kordsb.rest.request

import dev.jombi.kordsb.rest.json.response.GatewayResponse
import dev.jombi.kordsb.rest.route.Route
import io.ktor.client.*
import io.ktor.client.engine.mock.*
import io.ktor.http.*
import io.ktor.util.*
import io.ktor.utils.io.*
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class StackTraceRecoveryTest {

    @Test
    fun `test stack trace recovery`() = runTest {
        val mockEngine = MockEngine {
            respond(
                content = ByteReadChannel.Empty,
                status = HttpStatusCode.NotFound
            )
        }

        val client = HttpClient(mockEngine)
        val handler = KtorRequestHandler(client = client, token = "")
            .withStackTraceRecovery()

        val request = JsonRequest<GatewayResponse, GatewayResponse>(
            Route.GatewayGet, // The mock engine will 404 for any request, so we just use an endpoint without params
            emptyMap(),
            StringValues.Empty,
            StringValues.Empty,
            null
        )

        val stackTrace = Thread.currentThread().stackTrace[1] // 1st one would be Thread.run for some reason
        try {
            handler.handle(request)
        } catch (e: Exception) {
            e.printStackTrace()

            val initCause = e.cause ?: error("The thrown exception doesn't have a set cause! Is StackTrace Recovery enabled?")
            initCause.printStackTrace()

            //at dev.jombi.kordsb.rest.request.StackTraceRecoveryTest$test stack trace recovery$1.invokeSuspend(StackTraceRecoveryTest.kt:39)
            with(initCause.stackTrace.first()) {
                assertEquals(stackTrace.className, className)
                assertEquals(stackTrace.fileName, fileName)
                assertEquals(stackTrace.lineNumber + 2, lineNumber) // +2 because capture is two lines deeper
                assertEquals(stackTrace.methodName, methodName)
            }
        }
    }
}
