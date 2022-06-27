package dev.jombi.kordsb.core.cache.data

import dev.jombi.kordsb.common.entity.Snowflake
import dev.jombi.kordsb.gateway.DiscordRemovedEmoji
import dev.jombi.kordsb.gateway.DiscordRemovedReactionEmoji
import kotlinx.serialization.Serializable

@Serializable
public data class RemovedReactionData(val id: Snowflake? = null, val name: String?) {
    public companion object {
        public fun from(entity: DiscordRemovedReactionEmoji): RemovedReactionData = with(entity) {
            RemovedReactionData(id, name)
        }
    }
}

@Serializable
public data class ReactionRemoveEmojiData(
    val channelId: Snowflake,
    val guildId: Snowflake,
    val messageId: Snowflake,
    val emoji: RemovedReactionData
) {
    public companion object {
        public fun from(entity: DiscordRemovedEmoji): ReactionRemoveEmojiData = with(entity) {
            ReactionRemoveEmojiData(channelId, guildId, messageId, RemovedReactionData.from(emoji))
        }
    }
}
