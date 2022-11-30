package dev.jombi.kordsb.core.event.guild

import dev.jombi.kordsb.common.entity.Snowflake
import dev.jombi.kordsb.common.exception.RequestException
import dev.jombi.kordsb.core.Kord
import dev.jombi.kordsb.core.behavior.GuildBehavior
import dev.jombi.kordsb.core.behavior.channel.ChannelBehavior
import dev.jombi.kordsb.core.cache.data.InviteDeleteData
import dev.jombi.kordsb.core.entity.Guild
import dev.jombi.kordsb.core.entity.Strategizable
import dev.jombi.kordsb.core.entity.channel.Channel
import dev.jombi.kordsb.core.event.Event
import dev.jombi.kordsb.core.exception.EntityNotFoundException
import dev.jombi.kordsb.core.supplier.EntitySupplier
import dev.jombi.kordsb.core.supplier.EntitySupplyStrategy

/**
 * Sent when an invite is deleted.
 */
public class InviteDeleteEvent(
    public val data: InviteDeleteData,
    override val kord: Kord,
    override val customContext: Any?,
    override val supplier: EntitySupplier = kord.defaultSupplier,
) : Event, Strategizable {

    /**
     * The id of the [Channel] the invite is for.
     */
    public val channelId: Snowflake get() = data.channelId

    /**
     * The behavior of the [Channel] the invite is for.
     */
    public val channel: ChannelBehavior get() = ChannelBehavior(id = channelId, kord = kord)

    /**
     * The id of the [Guild] of the invite.
     */
    public val guildId: Snowflake? get() = data.guildId.value

    /**
     * The behavior of the [Guild] of the invite.
     */
    public val guild: GuildBehavior? get() = guildId?.let { GuildBehavior(id = it, kord = kord) }

    /**
     * The unique invite code.
     */
    public val code: String get() = data.code

    /**
     * Requests to get the [Channel] this invite is for.
     *
     * @throws [RequestException] if anything went wrong during the request.
     * @throws [EntityNotFoundException] if the  wasn't present.
     */
    public suspend fun getChannel(): Channel = supplier.getChannel(channelId)

    /**
     * Requests to get the [Channel] this invite is for,
     * returns null if the channel isn't present.
     *
     * @throws [RequestException] if anything went wrong during the request.
     */
    public suspend fun getChannelOrNull(): Channel? = supplier.getChannelOrNull(channelId)

    /**
     * Requests to get the [Guild] of the invite.
     */
    @Deprecated(
        "'guildId' might not be present, use 'getGuildOrNull' instead.",
        ReplaceWith("this.getGuildOrNull()"),
        DeprecationLevel.HIDDEN,
    )
    public suspend fun getGuild(): Guild = supplier.getGuild(guildId!!)

    /**
     * Requests to get the [Guild] of the invite.
     * returns null if the guild isn't present, or if invite does not target a guild.
     *
     * @throws [RequestException] if anything went wrong during the request.
     */
    public suspend fun getGuildOrNull(): Guild? = guildId?.let { supplier.getGuildOrNull(it) }

    override fun withStrategy(strategy: EntitySupplyStrategy<*>): InviteDeleteEvent =
        InviteDeleteEvent(data, kord, customContext, strategy.supply(kord))

    override fun toString(): String {
        return "InviteDeleteEvent(data=$data, kord=$kord, supplier=$supplier)"
    }

}
