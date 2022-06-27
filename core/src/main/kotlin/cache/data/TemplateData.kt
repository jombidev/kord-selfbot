package dev.jombi.kordsb.core.cache.data

import dev.jombi.kordsb.common.entity.DiscordTemplate
import dev.jombi.kordsb.common.entity.Snowflake
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
public data class TemplateData(
    val code: String,
    val name: String,
    val description: String?,
    val usageCount: Int,
    val creatorId: Snowflake,
    val creator: UserData,
    val createdAt: Instant,
    val updatedAt: Instant,
    val sourceGuildId: Snowflake,
    val serializedSourceGuild: PartialGuildData,
    val isDirty: Boolean?

) {
    public companion object {
        public fun from(template: DiscordTemplate): TemplateData {
            return with(template) {
                TemplateData(
                    code,
                    name,
                    description,
                    usageCount,
                    creatorId,
                    creator.toData(),
                    createdAt,
                    updatedAt,
                    sourceGuildId,
                    PartialGuildData.from(serializedSourceGuild),
                    isDirty
                )
            }
        }
    }
}

public fun DiscordTemplate.toData(): TemplateData = TemplateData.from(this)
