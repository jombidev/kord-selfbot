package dev.jombi.kordsb.core.entity.channel

import dev.jombi.kordsb.core.behavior.channel.threads.ThreadParentChannelBehavior
import dev.jombi.kordsb.core.supplier.EntitySupplyStrategy

public interface ThreadParentChannel : ThreadParentChannelBehavior, TopGuildMessageChannel {

    override fun withStrategy(strategy: EntitySupplyStrategy<*>): ThreadParentChannel
}
