package dev.jombi.kordsb.core.event.message

import dev.jombi.kordsb.common.entity.Snowflake
import dev.jombi.kordsb.core.Kord
import dev.jombi.kordsb.core.behavior.GuildBehavior
import dev.jombi.kordsb.core.behavior.MemberBehavior
import dev.jombi.kordsb.core.behavior.MessageBehavior
import dev.jombi.kordsb.core.behavior.UserBehavior
import dev.jombi.kordsb.core.behavior.channel.MessageChannelBehavior
import dev.jombi.kordsb.core.entity.*
import dev.jombi.kordsb.core.entity.channel.MessageChannel
import dev.jombi.kordsb.core.event.Event
import dev.jombi.kordsb.core.supplier.EntitySupplier
import dev.jombi.kordsb.core.supplier.EntitySupplyStrategy
import dev.jombi.kordsb.core.supplier.getChannelOf
import dev.jombi.kordsb.core.supplier.getChannelOfOrNull

public class ReactionAddEvent(
    public val userId: Snowflake,
    public val channelId: Snowflake,
    public val messageId: Snowflake,
    public val guildId: Snowflake?,
    public val emoji: ReactionEmoji,
    override val kord: Kord,
    override val customContext: Any?,
    override val supplier: EntitySupplier = kord.defaultSupplier,
) : Event, Strategizable {

    public val channel: MessageChannelBehavior get() = MessageChannelBehavior(channelId, kord)

    public val guild: GuildBehavior? get() = guildId?.let { GuildBehavior(it, kord) }

    public val message: MessageBehavior get() = MessageBehavior(channelId, messageId, kord)

    public val user: UserBehavior get() = UserBehavior(userId, kord)

    public val userAsMember: MemberBehavior? get() = guildId?.let { MemberBehavior(it, userId, kord) }

    public suspend fun getChannel(): MessageChannel = supplier.getChannelOf(channelId)

    public suspend fun getChannelOrNull(): MessageChannel? = supplier.getChannelOfOrNull(channelId)

    public suspend fun getGuild(): Guild? = guildId?.let { supplier.getGuildOrNull(it) }

    public suspend fun getMessage(): Message = supplier.getMessage(channelId = channelId, messageId = messageId)
    public suspend fun getMessageOrNull(): Message? = supplier.getMessageOrNull(channelId = channelId, messageId = messageId)

    public suspend fun getUser(): User = supplier.getUser(userId)
    public suspend fun getUserOrNull(): User? = supplier.getUserOrNull(userId)

    public suspend fun getUserAsMember(): Member? = guildId?.let { supplier.getMemberOrNull(it, userId) }

    override fun withStrategy(strategy: EntitySupplyStrategy<*>): ReactionAddEvent =
        ReactionAddEvent(userId, channelId, messageId, guildId, emoji, kord, customContext, strategy.supply(kord))

    override fun toString(): String {
        return "ReactionAddEvent(userId=$userId, channelId=$channelId, messageId=$messageId, guildId=$guildId, emoji=$emoji, kord=$kord, supplier=$supplier)"
    }
}
