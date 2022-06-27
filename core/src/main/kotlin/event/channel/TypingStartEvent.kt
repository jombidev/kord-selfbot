package dev.jombi.kordsb.core.event.channel

import dev.jombi.kordsb.common.entity.Snowflake
import dev.jombi.kordsb.common.exception.RequestException
import dev.jombi.kordsb.core.Kord
import dev.jombi.kordsb.core.behavior.GuildBehavior
import dev.jombi.kordsb.core.behavior.UserBehavior
import dev.jombi.kordsb.core.behavior.channel.MessageChannelBehavior
import dev.jombi.kordsb.core.entity.Guild
import dev.jombi.kordsb.core.entity.Strategizable
import dev.jombi.kordsb.core.entity.User
import dev.jombi.kordsb.core.entity.channel.MessageChannel
import dev.jombi.kordsb.core.event.Event
import dev.jombi.kordsb.core.event.channel.data.TypingStartEventData
import dev.jombi.kordsb.core.event.kordCoroutineScope
import dev.jombi.kordsb.core.exception.EntityNotFoundException
import dev.jombi.kordsb.core.supplier.EntitySupplier
import dev.jombi.kordsb.core.supplier.EntitySupplyStrategy
import dev.jombi.kordsb.core.supplier.getChannelOf
import dev.jombi.kordsb.core.supplier.getChannelOfOrNull
import kotlinx.coroutines.CoroutineScope
import kotlinx.datetime.Instant

public class TypingStartEvent(
    public val data: TypingStartEventData,
    override val kord: Kord,
    override val supplier: EntitySupplier = kord.defaultSupplier,
    public val coroutineScope: CoroutineScope = kordCoroutineScope(kord)
) : Event, CoroutineScope by coroutineScope, Strategizable {

    public val channelId: Snowflake get() = data.channelId

    public val userId: Snowflake get() = data.userId

    public val guildId: Snowflake? get() = data.guildId.value

    public val started: Instant get() = data.timestamp

    public val channel: MessageChannelBehavior get() = MessageChannelBehavior(channelId, kord)

    public val guild: GuildBehavior? get() = guildId?.let { GuildBehavior(it, kord) }

    public val user: UserBehavior get() = UserBehavior(userId, kord)

    /**
     * Requests to get the channel triggering the event.
     *
     * @throws [RequestException] if anything went wrong during the request.
     * @throws [EntityNotFoundException] if the [MessageChannel] wasn't present.
     */
    public suspend fun getChannel(): MessageChannel = supplier.getChannelOf(channelId)

    /**
     * Requests to get the channel triggering the event,
     * returns null if the [MessageChannel] isn't present.
     *
     * @throws [RequestException] if anything went wrong during the request.
     */
    public suspend fun getChannelOrNull(): MessageChannel? = supplier.getChannelOfOrNull(channelId)

    /**
     * Requests to get the user triggering the event.
     *
     * @throws [RequestException] if anything went wrong during the request.
     * @throws [EntityNotFoundException] if the [User] wasn't present.
     */
    public suspend fun getUser(): User = supplier.getUser(userId)

    /**
     * Requests to get the user triggering the event,
     * returns null if the [User] isn't present.
     *
     * @throws [RequestException] if anything went wrong during the request.
     */
    public suspend fun getUserOrNull(): User? = supplier.getUserOrNull(userId)

    /**
     * Requests to get the guild this event was triggered in,
     * returns null if the [Guild] isn't present.
     *
     * @throws [RequestException] if anything went wrong during the request.
     */
    public suspend fun getGuild(): Guild? = guildId?.let { supplier.getGuildOrNull(it) }

    override fun withStrategy(strategy: EntitySupplyStrategy<*>): Strategizable =
        TypingStartEvent(
            data,
            kord,
            supplier
        )

    override fun toString(): String {
        return "TypingStartEvent(channelId=$channelId, userId=$userId, guildId=$guildId, started=$started, kord=$kord, supplier=$supplier)"
    }

}
