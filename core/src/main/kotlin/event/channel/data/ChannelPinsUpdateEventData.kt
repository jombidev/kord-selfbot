package dev.jombi.kordsb.core.event.channel.data

import dev.jombi.kordsb.common.entity.DiscordPinsUpdateData
import dev.jombi.kordsb.common.entity.Snowflake
import dev.jombi.kordsb.common.entity.optional.Optional
import dev.jombi.kordsb.common.entity.optional.OptionalSnowflake
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
public data class ChannelPinsUpdateEventData(
    val guildId: OptionalSnowflake = OptionalSnowflake.Missing,
    val channelId: Snowflake,
    val lastPinTimestamp: Optional<Instant?> = Optional.Missing()
) {
    public companion object {
        public fun from(entity: DiscordPinsUpdateData): ChannelPinsUpdateEventData = with(entity) {
            ChannelPinsUpdateEventData(guildId, channelId, lastPinTimestamp)
        }
    }
}
