package dev.jombi.kordsb.core.cache.data

import dev.jombi.kordsb.common.entity.DiscordWelcomeScreen
import dev.jombi.kordsb.common.entity.DiscordWelcomeScreenChannel
import dev.jombi.kordsb.common.entity.Snowflake
import kotlinx.serialization.Serializable

@Serializable
public data class WelcomeScreenData(
    val description: String?,
    val welcomeChannels: List<WelcomeScreenChannelData>,
) {
    public companion object {
        public fun from(data: DiscordWelcomeScreen): WelcomeScreenData {
            with(data) {
                return WelcomeScreenData(description, welcomeChannels.map { WelcomeScreenChannelData.from(it) })
            }
        }
    }
}

@Serializable
public data class WelcomeScreenChannelData(
    val channelId: Snowflake,
    val description: String,
    val emojiId: Snowflake?,
    val emojiName: String?,
) {
    public companion object {
        public fun from(data: DiscordWelcomeScreenChannel): WelcomeScreenChannelData {
            with(data) {
                return WelcomeScreenChannelData(channelId, description, emojiId, emojiName)
            }
        }
    }
}
