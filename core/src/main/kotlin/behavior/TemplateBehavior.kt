package dev.jombi.kordsb.core.behavior

import dev.jombi.kordsb.common.entity.Snowflake
import dev.jombi.kordsb.core.Kord
import dev.jombi.kordsb.core.KordObject
import dev.jombi.kordsb.core.cache.data.toData
import dev.jombi.kordsb.core.entity.Guild
import dev.jombi.kordsb.core.entity.Template
import dev.jombi.kordsb.rest.builder.template.GuildFromTemplateCreateBuilder
import dev.jombi.kordsb.rest.builder.template.GuildTemplateModifyBuilder
import java.util.*
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

public interface TemplateBehavior : KordObject {
    public val guildId: Snowflake
    public val code: String

    public suspend fun sync(): Template {
        val response = kord.rest.template.syncGuildTemplate(guildId, code)
        val data = response.toData()
        return Template(data, kord)
    }

    public suspend fun delete(): Template {
        val response = kord.rest.template.deleteGuildTemplate(guildId, code)
        val data = response.toData()
        return Template(data, kord)
    }

}


public suspend fun TemplateBehavior.edit(builder: GuildTemplateModifyBuilder.() -> Unit): Template {
    contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
    val response = kord.rest.template.modifyGuildTemplate(guildId, code, builder)
    val data = response.toData()
    return Template(data, kord)
}

public suspend fun TemplateBehavior.createGuild(name: String, builder: GuildFromTemplateCreateBuilder.() -> Unit): Guild {
    contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
    val response = kord.rest.template.createGuildFromTemplate(code, name, builder)
    val data = response.toData()
    return Guild(data, kord)
}


public fun TemplateBehavior(guildId: Snowflake, code: String, kord: Kord): TemplateBehavior =
    object : TemplateBehavior {
        override val code: String = code
        override val guildId: Snowflake = guildId
        override val kord: Kord = kord

        override fun hashCode(): Int = Objects.hash(code)

        override fun equals(other: Any?): Boolean =
            other is TemplateBehavior && other.code == code


        override fun toString(): String {
            return "TemplateBehavior(code=$code, guildId=$guildId)"
        }
    }
