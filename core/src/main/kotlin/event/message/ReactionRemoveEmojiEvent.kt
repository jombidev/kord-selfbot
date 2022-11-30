package dev.jombi.kordsb.core.event.message

import dev.jombi.kordsb.common.entity.Snowflake
import dev.jombi.kordsb.core.Kord
import dev.jombi.kordsb.core.behavior.GuildBehavior
import dev.jombi.kordsb.core.behavior.MessageBehavior
import dev.jombi.kordsb.core.behavior.channel.GuildMessageChannelBehavior
import dev.jombi.kordsb.core.cache.data.ReactionRemoveEmojiData
import dev.jombi.kordsb.core.entity.Guild
import dev.jombi.kordsb.core.entity.Message
import dev.jombi.kordsb.core.entity.ReactionEmoji
import dev.jombi.kordsb.core.entity.Strategizable
import dev.jombi.kordsb.core.entity.channel.TopGuildMessageChannel
import dev.jombi.kordsb.core.event.Event
import dev.jombi.kordsb.core.supplier.EntitySupplier
import dev.jombi.kordsb.core.supplier.EntitySupplyStrategy
import dev.jombi.kordsb.core.supplier.getChannelOf
import dev.jombi.kordsb.core.supplier.getChannelOfOrNull

public class ReactionRemoveEmojiEvent(
    public val data: ReactionRemoveEmojiData,
    override val kord: Kord,
    override val customContext: Any?,
    override val supplier: EntitySupplier = kord.defaultSupplier,
) : Event, Strategizable {

    /**
     * The id of the [TopGuildMessageChannel].
     */
    public val channelId: Snowflake get() = data.channelId

    public val channel: GuildMessageChannelBehavior
        get() = GuildMessageChannelBehavior(
            guildId = guildId,
            id = channelId,
            kord = kord
        )

    /**
     * The id of the [Guild].
     */
    public val guildId: Snowflake get() = data.guildId

    public val guild: GuildBehavior get() = GuildBehavior(id = guildId, kord = kord)

    /**
     * The id of the message.
     */
    public val messageId: Snowflake get() = data.messageId

    public val message: MessageBehavior get() = MessageBehavior(channelId = channelId, messageId = messageId, kord = kord)

    /**
     * The emoji that was removed.
     */
    public val emoji: ReactionEmoji get() = ReactionEmoji.from(data.emoji)

    public suspend fun getChannel(): TopGuildMessageChannel = supplier.getChannelOf(channelId)

    public suspend fun getChannelOrNull(): TopGuildMessageChannel? = supplier.getChannelOfOrNull(channelId)

    public suspend fun getGuild(): Guild = supplier.getGuild(guildId)

    public suspend fun getGuildOrNull(): Guild? = supplier.getGuildOrNull(guildId)

    public suspend fun getMessage(): Message = supplier.getMessage(channelId = channelId, messageId = messageId)

    public suspend fun getMessageOrNull(): Message? = supplier.getMessageOrNull(channelId = channelId, messageId = messageId)

    override fun withStrategy(strategy: EntitySupplyStrategy<*>): ReactionRemoveAllEvent =
        ReactionRemoveAllEvent(channelId, messageId, guildId, kord, customContext, strategy.supply(kord))

    override fun toString(): String {
        return "ReactionRemoveEmojiEvent(data=$data, kord=$kord, supplier=$supplier)"
    }
}
