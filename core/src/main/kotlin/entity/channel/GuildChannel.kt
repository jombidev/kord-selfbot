package dev.jombi.kordsb.core.entity.channel

import dev.jombi.kordsb.common.entity.OverwriteType
import dev.jombi.kordsb.common.entity.Permission
import dev.jombi.kordsb.common.entity.Permissions
import dev.jombi.kordsb.common.entity.Snowflake
import dev.jombi.kordsb.common.entity.optional.orEmpty
import dev.jombi.kordsb.common.entity.optional.value
import dev.jombi.kordsb.common.exception.RequestException
import dev.jombi.kordsb.core.behavior.channel.GuildChannelBehavior
import dev.jombi.kordsb.core.behavior.channel.TopGuildChannelBehavior
import dev.jombi.kordsb.core.cache.data.PermissionOverwriteData
import dev.jombi.kordsb.core.entity.PermissionOverwriteEntity
import dev.jombi.kordsb.core.supplier.EntitySupplyStrategy

/**
 * An instance of a Discord channel associated to a [guild].
 */
public interface GuildChannel : Channel, GuildChannelBehavior {

    override val guildId: Snowflake
        get() = data.guildId.value!!

    /**
     * The name of this channel.
     */
    public val name: String get() = data.name.value!!

    override fun withStrategy(strategy: EntitySupplyStrategy<*>): GuildChannel

}
