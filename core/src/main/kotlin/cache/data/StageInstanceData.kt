package dev.jombi.kordsb.core.cache.data

import dev.jombi.kordsb.common.entity.DiscordStageInstance
import dev.jombi.kordsb.common.entity.Snowflake
import dev.jombi.kordsb.common.entity.StageInstancePrivacyLevel
import kotlinx.serialization.Serializable

@Serializable
public data class StageInstanceData(
    val id: Snowflake,
    val guildId: Snowflake,
    val channelId: Snowflake,
    val topic: String,
    val privacyLevel: StageInstancePrivacyLevel,
    val guildScheduledEventId: Snowflake?,
) {
    public companion object {
        public fun from(stageInstance: DiscordStageInstance): StageInstanceData = with(stageInstance) {
            StageInstanceData(
                id = id,
                guildId = guildId,
                channelId = channelId,
                topic = topic,
                privacyLevel = privacyLevel,
                guildScheduledEventId = guildScheduledEventId,
            )
        }
    }
}

public fun DiscordStageInstance.toData(): StageInstanceData {
    return StageInstanceData.from(this)
}
