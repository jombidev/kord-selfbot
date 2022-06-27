package dev.jombi.kordsb.rest.json.request

import dev.jombi.kordsb.common.entity.Snowflake
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
public data class ChannelFollowRequest(
    @SerialName("webhook_channel_id")
    val webhookChannelId: Snowflake,
)
