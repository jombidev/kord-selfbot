package dev.jombi.kordsb.rest.ratelimit

import dev.jombi.kordsb.common.annotation.KordUnsafe
import kotlinx.datetime.Clock

class ParallelRequestRateLimiterTest : AbstractRequestRateLimiterTest() {

    @OptIn(KordUnsafe::class)
    override fun newRequestRateLimiter(clock: Clock): RequestRateLimiter {
        return ParallelRequestRateLimiter(clock)
    }

}
