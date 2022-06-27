package dev.jombi.kordsb.core.entity

import dev.jombi.kordsb.common.entity.Snowflake
import dev.jombi.kordsb.core.Kord
import dev.jombi.kordsb.core.KordObject
import dev.jombi.kordsb.core.behavior.TemplateBehavior
import dev.jombi.kordsb.core.cache.data.TemplateData
import kotlinx.datetime.Instant

public class Template(public val data: TemplateData, override val kord: Kord) : KordObject, TemplateBehavior {
    override val code: String get() = data.code

    public val name: String get() = data.name

    public val description: String? get() = data.description

    public val usageCount: Int get() = data.usageCount

    public val creatorId: Snowflake get() = data.creatorId

    public val creator: User get() = User(data.creator, kord)

    public val createdAt: Instant get() = data.createdAt

    public val updatedAt: Instant get() = data.updatedAt

    override val guildId: Snowflake get() = data.sourceGuildId

    public val partialGuild: PartialGuild get() = PartialGuild(data.serializedSourceGuild, kord)

    public val dirty: Boolean? = data.isDirty
}
