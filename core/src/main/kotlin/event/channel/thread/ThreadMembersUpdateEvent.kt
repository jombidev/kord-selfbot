package dev.jombi.kordsb.core.event.channel.thread

import dev.jombi.kordsb.common.entity.Snowflake
import dev.jombi.kordsb.common.entity.optional.orEmpty
import dev.jombi.kordsb.core.Kord
import dev.jombi.kordsb.core.behavior.MemberBehavior
import dev.jombi.kordsb.core.cache.data.ThreadMembersUpdateEventData
import dev.jombi.kordsb.core.entity.channel.thread.ThreadMember
import dev.jombi.kordsb.core.event.Event

public class ThreadMembersUpdateEvent(
    public val data: ThreadMembersUpdateEventData,
    override val kord: Kord,
    override val customContext: Any?,
) : Event {

    public val id: Snowflake get() = data.id

    public val guildId: Snowflake get() = data.guildId

    public val memberCount: Int get() = data.memberCount

    public val addedMembers: List<ThreadMember>
        get() = data.addedMembers.orEmpty().map {
            ThreadMember(it, kord)
        }

    public val removedMemberIds: List<Snowflake> get() = data.removedMemberIds.orEmpty()

    public val removedMemberBehaviors: List<MemberBehavior>
        get() = removedMemberIds.map {
            MemberBehavior(guildId, it, kord)
        }
}
