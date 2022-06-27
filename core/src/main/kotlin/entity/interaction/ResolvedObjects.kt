package dev.jombi.kordsb.core.entity.interaction

import dev.jombi.kordsb.common.entity.Snowflake
import dev.jombi.kordsb.core.Kord
import dev.jombi.kordsb.core.cache.data.ResolvedObjectsData
import dev.jombi.kordsb.core.entity.*
import dev.jombi.kordsb.core.entity.channel.ResolvedChannel
import dev.jombi.kordsb.core.supplier.EntitySupplyStrategy
import dev.jombi.kordsb.common.entity.optional.mapValues

public class ResolvedObjects(
    public val data: ResolvedObjectsData,
    public val kord: Kord,
    public val strategy: EntitySupplyStrategy<*> = kord.resources.defaultStrategy,
) {
    public val channels: Map<Snowflake, ResolvedChannel>?
        get() = data.channels.mapValues { ResolvedChannel(it.value, kord, strategy) }.value

    public val roles: Map<Snowflake, Role>? get() = data.roles.mapValues { Role(it.value, kord) }.value

    public val users: Map<Snowflake, User>? get() = data.users.mapValues { User(it.value, kord) }.value

    public val members: Map<Snowflake, Member>?
        get() = data.members.mapValues { Member(it.value, users!![it.key]!!.data, kord) }.value

    public val messages: Map<Snowflake, Message>?
        get() = data.messages.mapValues { Message(it.value, kord) }.value

    public val attachments: Map<Snowflake, Attachment>?
        get() = data.attachments.mapValues { Attachment(it.value, kord) }.value
}
