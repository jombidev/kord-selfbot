package dev.jombi.kordsb.core.cache.data

import dev.jombi.kordsb.common.entity.DiscordMessageReference
import dev.jombi.kordsb.common.entity.optional.OptionalSnowflake
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
public data class MessageReferenceData(
    @SerialName("message_id")
    val id: OptionalSnowflake = OptionalSnowflake.Missing,
    @SerialName("channel_id")
    val channelId: OptionalSnowflake = OptionalSnowflake.Missing,
    @SerialName("guild_id")
    val guildId: OptionalSnowflake = OptionalSnowflake.Missing,
) {
    public companion object {
        public fun from(entity: DiscordMessageReference): MessageReferenceData = with(entity) {
            return MessageReferenceData(id, channelId, guildId)
        }
    }
}
