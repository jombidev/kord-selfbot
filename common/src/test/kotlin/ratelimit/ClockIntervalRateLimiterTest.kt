package ratelimit

import dev.jombi.kordsb.common.ratelimit.ClockIntervalRateLimiter
import dev.jombi.kordsb.common.ratelimit.IntervalRateLimiter
import fixed
import kotlinx.datetime.Clock
import kotlin.time.Duration

class ClockIntervalRateLimiterTest : AbstractIntervalRateLimiterTest() {

    private val clock = Clock.fixed(instant = Clock.System.now())

    override fun newRateLimiter(limit: Int, interval: Duration): IntervalRateLimiter =
        ClockIntervalRateLimiter(limit, interval, clock)
}
