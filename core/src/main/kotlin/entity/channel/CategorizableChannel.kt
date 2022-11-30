package dev.jombi.kordsb.core.entity.channel

import dev.jombi.kordsb.common.entity.Snowflake
import dev.jombi.kordsb.common.entity.optional.value
import dev.jombi.kordsb.core.behavior.channel.CategorizableChannelBehavior
import dev.jombi.kordsb.core.behavior.channel.CategoryBehavior
import dev.jombi.kordsb.core.supplier.EntitySupplyStrategy

/**
 * An instance of a Discord channel associated to a [category].
 */
public interface CategorizableChannel : TopGuildChannel, CategorizableChannelBehavior {

    /**
     * The id of the [category] this channel belongs to, if any.
     */
    public val categoryId: Snowflake?
        get() = data.parentId.value

    /**
     * The category behavior this channel belongs to, if any.
     */
    public val category: CategoryBehavior?
        get() = when (val categoryId = categoryId) {
            null -> null
            else -> CategoryBehavior(id = categoryId, guildId = guildId, kord = kord)
        }

    override fun withStrategy(strategy: EntitySupplyStrategy<*>): CategorizableChannel
}