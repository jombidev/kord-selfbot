package dev.jombi.kordsb.core.entity.channel

import dev.jombi.kordsb.core.behavior.channel.GuildMessageChannelBehavior
import dev.jombi.kordsb.core.supplier.EntitySupplyStrategy

public interface GuildMessageChannel : GuildChannel, MessageChannel, GuildMessageChannelBehavior {

    /**
     * Returns a new [GuildMessageChannel] with the given [strategy].
     */
    override fun withStrategy(strategy: EntitySupplyStrategy<*>): GuildMessageChannel

}
