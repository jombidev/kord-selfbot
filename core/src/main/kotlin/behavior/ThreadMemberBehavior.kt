package dev.jombi.kordsb.core.behavior

import dev.jombi.kordsb.common.entity.Snowflake
import dev.jombi.kordsb.core.Kord
import dev.jombi.kordsb.core.entity.channel.thread.ThreadChannel
import dev.jombi.kordsb.core.supplier.EntitySupplier
import dev.jombi.kordsb.core.supplier.EntitySupplyStrategy
import dev.jombi.kordsb.core.supplier.getChannelOf
import dev.jombi.kordsb.core.supplier.getChannelOfOrNull

public interface ThreadMemberBehavior : UserBehavior {

    public val threadId: Snowflake

    public suspend fun getThread(): ThreadChannel = supplier.getChannelOf(threadId)

    public suspend fun getThreadOrNull(): ThreadChannel? = supplier.getChannelOfOrNull(threadId)

    override fun withStrategy(strategy: EntitySupplyStrategy<*>): UserBehavior {
        return ThreadMemberBehavior(id, threadId, kord, strategy.supply(kord))

    }
}

public fun ThreadMemberBehavior(
    id: Snowflake,
    threadId: Snowflake,
    kord: Kord,
    supplier: EntitySupplier = kord.defaultSupplier
): ThreadMemberBehavior {
    return object : ThreadMemberBehavior {
        override val id: Snowflake
            get() = id
        override val threadId: Snowflake
            get() = threadId
        override val kord: Kord
            get() = kord
        override val supplier: EntitySupplier
            get() = supplier
    }
}
