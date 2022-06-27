package dev.jombi.kordsb.core.cache.data

import dev.kord.cache.api.data.DataDescription
import dev.kord.cache.api.data.description
import dev.jombi.kordsb.common.entity.DiscordWebhook
import dev.jombi.kordsb.common.entity.Snowflake
import dev.jombi.kordsb.common.entity.WebhookType
import dev.jombi.kordsb.common.entity.optional.Optional
import dev.jombi.kordsb.common.entity.optional.OptionalSnowflake
import dev.jombi.kordsb.common.entity.optional.mapSnowflake
import kotlinx.serialization.Serializable

@Serializable
public data class WebhookData(
    val id: Snowflake,
    val type: WebhookType,
    val guildId: OptionalSnowflake = OptionalSnowflake.Missing,
    val channelId: Snowflake,
    val userId: OptionalSnowflake = OptionalSnowflake.Missing,
    val name: String? = null,
    val avatar: String? = null,
    val token: Optional<String> = Optional.Missing(),
    val applicationId: Snowflake?
) {
    public companion object {
        public val description: DataDescription<WebhookData, Snowflake> = description(WebhookData::id)

        public fun from(entity: DiscordWebhook): WebhookData = with(entity) {
            WebhookData(id, type, guildId, channelId, user.mapSnowflake { it.id }, name, avatar, token, applicationId)
        }
    }
}
