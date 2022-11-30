package dev.jombi.kordsb.rest.request


/**
 * Extension of [KtorRequestHandler] which tries to recover stack trace information lost through Ktor's
 * [io.ktor.util.pipeline.SuspendFunctionGun].
 *
 * This is done by creating a [RecoveredStackTrace] to capture the stack trace up until the point just before
 * [KtorRequestHandler.handle] gets called, then if that call throws any type of [Throwable] the [RecoveredStackTrace]
 * gets added to the original [Throwable] as a [suppressed exception][addSuppressed] and the original [Throwable] is
 * rethrown.
 *
 * @see ContextException
 * @see withStackTraceRecovery
 */
public class StackTraceRecoveringKtorRequestHandler(private val delegate: KtorRequestHandler) :
    RequestHandler by delegate {

    /**
     * @throws ContextException if any exception occurs (this is also the only exception which can be thrown)
     * @see KtorRequestHandler.handle
     */
    override suspend fun <B : Any, R> handle(request: Request<B, R>): R {
        val recoveredStackTrace = RecoveredStackTrace()

        return try {
            delegate.handle(request)
        } catch (e: Throwable) {
            recoveredStackTrace.sanitizeStackTrace()
            e.addSuppressed(recoveredStackTrace)
            throw e
        }
    }
}

@Deprecated("'ContextException' is no longer thrown. 'stackTraceRecovery' uses a suppressed exception instead.")
public class ContextException internal constructor() : RuntimeException()

internal class RecoveredStackTrace : Throwable("This is recovered stack trace:") {

    fun sanitizeStackTrace() {
        // Remove artifacts of stack trace capturing.
        // The first stack trace element is the creation of the RecoveredStackTrace:
        // at dev.jombi.kordsb.rest.request.StackTraceRecoveringKtorRequestHandler.handle(StackTraceRecoveringKtorRequekstHandler.kt:21)
        stackTrace = stackTrace.copyOfRange(1, stackTrace.size)
    }
}

/**
 * Returns a new [RequestHandler] with stack trace recovery enabled.
 *
 * @see StackTraceRecoveringKtorRequestHandler
 */
public fun KtorRequestHandler.withStackTraceRecovery(): StackTraceRecoveringKtorRequestHandler =
    StackTraceRecoveringKtorRequestHandler(this)
