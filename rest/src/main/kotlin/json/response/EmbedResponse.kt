package dev.jombi.kordsb.rest.json.response

import dev.jombi.kordsb.common.annotation.DeprecatedSinceKord
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@DeprecatedSinceKord("0.7.0")
@Deprecated(
    "Guild embeds were renamed to widgets",
    ReplaceWith(
        "DiscordGuildWidget(enabled, channelId)",
        "dev.jombi.kordsb.common.entity.DiscordGuildWidget"
    ),
    DeprecationLevel.HIDDEN
)
public data class EmbedResponse(
    val enabled: Boolean,
    @SerialName("channel_id")
    val channelId: String
)
