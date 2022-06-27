package dev.jombi.kordsb.core.entity

import dev.jombi.kordsb.common.entity.Snowflake
import dev.jombi.kordsb.common.exception.RequestException
import dev.jombi.kordsb.core.Kord
import dev.jombi.kordsb.core.KordObject
import dev.jombi.kordsb.core.behavior.GuildBehavior
import dev.jombi.kordsb.core.behavior.channel.TopGuildChannelBehavior
import dev.jombi.kordsb.core.cache.data.PermissionOverwriteData
import dev.jombi.kordsb.core.entity.channel.TopGuildChannel
import dev.jombi.kordsb.core.exception.EntityNotFoundException
import dev.jombi.kordsb.core.supplier.EntitySupplier
import dev.jombi.kordsb.core.supplier.EntitySupplyStrategy
import dev.jombi.kordsb.core.supplier.getChannelOf
import dev.jombi.kordsb.core.supplier.getChannelOfOrNull
import dev.jombi.kordsb.rest.request.RestRequestException

public class PermissionOverwriteEntity(
    public val guildId: Snowflake,
    public val channelId: Snowflake,
    data: PermissionOverwriteData,
    override val kord: Kord,
    override val supplier: EntitySupplier = kord.defaultSupplier
) : PermissionOverwrite(data), KordObject, Strategizable {

    public val guild: GuildBehavior get() = GuildBehavior(guildId, kord)

    public val channel: TopGuildChannelBehavior get() = TopGuildChannelBehavior(guildId, channelId, kord)

    /**
     * Requests to get the channel this overwrite applies to.
     *
     * @throws [RequestException] if anything went wrong during the request.
     * @throws [EntityNotFoundException] if the [TopGuildChannel] wasn't present.
     */
    public suspend fun getChannel(): TopGuildChannel = supplier.getChannelOf(channelId)

    /**
     * Requests to get the channel this overwrite applies to,
     * returns null if the [TopGuildChannel] isn't present.
     *
     * @throws [RequestException] if anything went wrong during the request.
     */
    public suspend fun getChannelOrNull(): TopGuildChannel? = supplier.getChannelOfOrNull(channelId)

    /**
     * Requests to get the the guild of this overwrite.
     *
     * @throws [RequestException] if anything went wrong during the request.
     * @throws [EntityNotFoundException] if the [Guild] wasn't present.
     */
    public suspend fun getGuild(): Guild = supplier.getGuild(guildId)

    /**
     * Requests to get the guild of this overwrite,
     * returns null if the [Guild] isn't present.
     *
     * @throws [RequestException] if anything went wrong during the request.
     */
    public suspend fun getGuildOrNull(): Guild? = supplier.getGuildOrNull(guildId)

    /**
     * Requests to delete this overwrite.
     *
     * @param reason the reason showing up in the audit log
     * @throws [RestRequestException] if something went wrong during the request.
     */
    public suspend fun delete(reason: String? = null) {
        kord.rest.channel.deleteChannelPermission(channelId, data.id, reason)
    }

    /**
     * Returns a new [PermissionOverwriteEntity] with the given [strategy].
     */
    override fun withStrategy(strategy: EntitySupplyStrategy<*>): PermissionOverwriteEntity =
        PermissionOverwriteEntity(guildId, channelId, data, kord, strategy.supply(kord))

    override fun toString(): String {
        return "PermissionOverwriteEntity(target=$target, type=$type, allowed=$allowed, denied=$denied, kord=$kord, supplier=$supplier)"
    }

}
