package dev.jombi.kordsb.core.entity.channel.thread

import dev.jombi.kordsb.common.entity.Snowflake
import dev.jombi.kordsb.core.Kord
import dev.jombi.kordsb.core.behavior.ThreadMemberBehavior
import dev.jombi.kordsb.core.cache.data.ThreadMemberData
import dev.jombi.kordsb.core.supplier.EntitySupplier
import dev.jombi.kordsb.core.supplier.EntitySupplyStrategy
import kotlinx.datetime.Instant

public class ThreadMember(
    public val data: ThreadMemberData,
    override val kord: Kord,
    override val supplier: EntitySupplier = kord.defaultSupplier
) : ThreadMemberBehavior {
    override val id: Snowflake
        get() = data.userId.orElse(kord.selfId)

    override val threadId: Snowflake get() = data.id


    public val joinTimestamp: Instant get() = data.joinTimestamp

    public val flags: Int = data.flags


    override fun withStrategy(strategy: EntitySupplyStrategy<*>): ThreadMember {
        return ThreadMember(data, kord, strategy.supply(kord))
    }
}
