package dev.jombi.kordsb.core.cache.data

import dev.jombi.kordsb.common.entity.*
import dev.jombi.kordsb.common.entity.optional.Optional
import dev.jombi.kordsb.common.entity.optional.OptionalInt
import dev.jombi.kordsb.common.entity.optional.OptionalSnowflake
import dev.jombi.kordsb.common.entity.optional.map
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
public data class GuildScheduledEventData(
    val id: Snowflake,
    val guildId: Snowflake,
    val channelId: Snowflake?,
    val creatorId: OptionalSnowflake? = OptionalSnowflake.Missing,
    val name: String,
    val description: Optional<String?> = Optional.Missing(),
    val scheduledStartTime: Instant,
    val scheduledEndTime: Instant?,
    val privacyLevel: GuildScheduledEventPrivacyLevel,
    val status: GuildScheduledEventStatus,
    val entityId: Snowflake?,
    val entityType: ScheduledEntityType,
    val entityMetadata: GuildScheduledEventEntityMetadata?,
    val creator: Optional<UserData> = Optional.Missing(),
    val userCount: OptionalInt = OptionalInt.Missing,
    val image: Optional<String?> = Optional.Missing(),
) {
    public companion object {
        public fun from(event: DiscordGuildScheduledEvent): GuildScheduledEventData = with(event) {
            GuildScheduledEventData(
                id = id,
                guildId = guildId,
                channelId = channelId,
                creatorId = creatorId,
                name = name,
                description = description,
                scheduledStartTime = scheduledStartTime,
                scheduledEndTime = scheduledEndTime,
                privacyLevel = privacyLevel,
                status = status,
                entityId = entityId,
                entityType = entityType,
                entityMetadata = entityMetadata,
                creator = creator.map { UserData.from(it) },
                userCount = userCount,
                image = image,
            )
        }
    }
}
