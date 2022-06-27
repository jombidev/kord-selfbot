package dev.jombi.kordsb.rest.ratelimit

import dev.jombi.kordsb.common.annotation.KordUnsafe
import dev.jombi.kordsb.rest.request.Request
import dev.jombi.kordsb.rest.request.RequestIdentifier
import dev.jombi.kordsb.rest.request.identifier
import mu.KLogger
import mu.KotlinLogging
import kotlinx.datetime.Clock

private val parallelLogger = KotlinLogging.logger {}

/**
 * A [RequestRateLimiter] that tries to handle [requests][Request] in a parallel order.
 * Requests are run sequentially per bucket,
 * allowing requests that do not share a common rate limit to run uninterrupted.
 * Requests that share a common bucket are handled in call order
 * and will suspend to adhere to global and bucket rate limits.
 *
 * Using this [RequestRateLimiter] increases the chance of a exceeding the global rate limit, which in exceedingly
 * unlikely cases might result in your bot's account getting temporarily banned.
 * As such, the [ExclusionRequestRateLimiter] is generally preferred.
 *
 * @param clock a [Clock] used for calculating suspension times, present for testing purposes.
 */
@KordUnsafe
public class ParallelRequestRateLimiter(clock: Clock = Clock.System) : AbstractRateLimiter(clock) {

    override val logger: KLogger
        get() = parallelLogger

    override fun newToken(request: Request<*, *>, buckets: List<Bucket>): RequestToken =
        ParallelRequestToken(this, request.identifier, buckets)

    private inner class ParallelRequestToken(rateLimiter: ParallelRequestRateLimiter, identity: RequestIdentifier, requestBuckets: List<Bucket>) :
        AbstractRequestToken(rateLimiter, identity, requestBuckets)

}
