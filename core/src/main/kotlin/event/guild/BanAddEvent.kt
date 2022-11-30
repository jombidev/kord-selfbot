package dev.jombi.kordsb.core.event.guild

import dev.jombi.kordsb.common.entity.Snowflake
import dev.jombi.kordsb.common.exception.RequestException
import dev.jombi.kordsb.core.Kord
import dev.jombi.kordsb.core.behavior.GuildBehavior
import dev.jombi.kordsb.core.entity.Ban
import dev.jombi.kordsb.core.entity.Guild
import dev.jombi.kordsb.core.entity.Strategizable
import dev.jombi.kordsb.core.entity.User
import dev.jombi.kordsb.core.event.Event
import dev.jombi.kordsb.core.exception.EntityNotFoundException
import dev.jombi.kordsb.core.supplier.EntitySupplier
import dev.jombi.kordsb.core.supplier.EntitySupplyStrategy

public class BanAddEvent(
    public val user: User,
    public val guildId: Snowflake,
    override val customContext: Any?,
    override val supplier: EntitySupplier = user.kord.defaultSupplier,
) : Event, Strategizable {

    override val kord: Kord get() = user.kord

    public val guild: GuildBehavior get() = GuildBehavior(guildId, kord)

    /**
     * Requests to get the [Guild] this ban happened in.
     *
     * @throws [RequestException] if anything went wrong during the request.
     * @throws [EntityNotFoundException] if the guild wasn't present.
     */
    public suspend fun getGuild(): Guild = supplier.getGuild(guildId)

    /**
     * Requests to get the [Guild] this ban happened in,
     * returns null if the guild isn't present.
     *
     * @throws [RequestException] if anything went wrong during the request.
     */
    public suspend fun getGuildOrNull(): Guild? = supplier.getGuildOrNull(guildId)

    /**
     * Requests to get the [Ban] entity this event represents.
     *
     * @throws [RequestException] if anything went wrong during the request.
     * @throws [EntityNotFoundException] if the ban wasn't present.
     */
    public suspend fun getBan(): Ban = supplier.getGuildBan(guildId, user.id)

    /**
     * Requests to get the [Ban] entity this event represents.
     *
     * @throws [RequestException] if anything went wrong during the request.
     * @throws [EntityNotFoundException] if the ban wasn't present.
     */
    public suspend fun getBanOrNull(): Ban? = supplier.getGuildBanOrNull(guildId, user.id)

    override fun withStrategy(strategy: EntitySupplyStrategy<*>): BanAddEvent =
        BanAddEvent(user, guildId, customContext, strategy.supply(kord))

    override fun toString(): String {
        return "BanAddEvent(user=$user, guildId=$guildId, supplier=$supplier)"
    }
}
