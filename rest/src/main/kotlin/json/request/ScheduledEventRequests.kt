package dev.jombi.kordsb.rest.json.request

import dev.jombi.kordsb.common.entity.GuildScheduledEventEntityMetadata
import dev.jombi.kordsb.common.entity.GuildScheduledEventPrivacyLevel
import dev.jombi.kordsb.common.entity.GuildScheduledEventStatus
import dev.jombi.kordsb.common.entity.ScheduledEntityType
import dev.jombi.kordsb.common.entity.optional.Optional
import dev.jombi.kordsb.common.entity.optional.OptionalSnowflake
import kotlinx.datetime.Instant
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
public data class GuildScheduledEventCreateRequest(
    @SerialName("channel_id")
    val channelId: OptionalSnowflake = OptionalSnowflake.Missing,
    @SerialName("entity_metadata")
    val entityMetadata: Optional<GuildScheduledEventEntityMetadata> = Optional.Missing(),
    val name: String,
    @SerialName("privacy_level")
    val privacyLevel: GuildScheduledEventPrivacyLevel,
    @SerialName("scheduled_start_time")
    val scheduledStartTime: Instant,
    @SerialName("scheduled_end_time")
    val scheduledEndTime: Optional<Instant> = Optional.Missing(),
    val description: Optional<String> = Optional.Missing(),
    @SerialName("entity_type")
    val entityType: ScheduledEntityType,
    val image: Optional<String> = Optional.Missing(),
)

@Serializable
public data class ScheduledEventModifyRequest(
    @SerialName("channel_id")
    val channelId: OptionalSnowflake? = OptionalSnowflake.Missing,
    @SerialName("entity_metadata")
    val entityMetadata: Optional<GuildScheduledEventEntityMetadata?> = Optional.Missing(),
    val name: Optional<String> = Optional.Missing(),
    @SerialName("privacy_level")
    val privacyLevel: Optional<GuildScheduledEventPrivacyLevel> = Optional.Missing(),
    @SerialName("scheduled_start_time")
    val scheduledStartTime: Optional<Instant> = Optional.Missing(),
    @SerialName("scheduled_end_time")
    val scheduledEndTime: Optional<Instant> = Optional.Missing(),
    val description: Optional<String?> = Optional.Missing(),
    @SerialName("entity_type")
    val entityType: Optional<ScheduledEntityType> = Optional.Missing(),
    val status: Optional<GuildScheduledEventStatus> = Optional.Missing(),
    val image: Optional<String> = Optional.Missing(),
)
