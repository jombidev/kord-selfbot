package dev.jombi.kordsb.rest.json.request

import dev.jombi.kordsb.common.entity.Snowflake
import dev.jombi.kordsb.common.entity.StageInstancePrivacyLevel
import dev.jombi.kordsb.common.entity.optional.Optional
import dev.jombi.kordsb.common.entity.optional.OptionalBoolean
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
public data class StageInstanceCreateRequest(
    @SerialName("channel_id")
    val channelId: Snowflake,
    val topic: String,
    @SerialName("privacy_level")
    val privacyLevel: Optional<StageInstancePrivacyLevel> = Optional.Missing(),
    @SerialName("send_start_notification")
    val sendStartNotification: OptionalBoolean = OptionalBoolean.Missing,
)

@Deprecated(
    "Replaced by 'StageInstanceModifyRequest'.",
    ReplaceWith("StageInstanceModifyRequest", "dev.jombi.kordsb.rest.json.request.StageInstanceModifyRequest"),
)
@Serializable
public data class StageInstanceUpdateRequest(val topic: String)

@Serializable
public data class StageInstanceModifyRequest(
    val topic: Optional<String> = Optional.Missing(),
    @SerialName("privacy_level")
    val privacyLevel: Optional<StageInstancePrivacyLevel> = Optional.Missing(),
)
