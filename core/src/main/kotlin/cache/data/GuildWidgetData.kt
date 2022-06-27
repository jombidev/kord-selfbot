package dev.jombi.kordsb.core.cache.data

import dev.jombi.kordsb.common.entity.DiscordGuildWidget
import dev.jombi.kordsb.common.entity.Snowflake
import kotlinx.serialization.Serializable

@Serializable
public data class GuildWidgetData(
    val enabled: Boolean,
    val channelId: Snowflake?
) {
    public companion object {
        public fun from(entity: DiscordGuildWidget): GuildWidgetData = with(entity) {
            GuildWidgetData(enabled, channelId)
        }
    }
}
