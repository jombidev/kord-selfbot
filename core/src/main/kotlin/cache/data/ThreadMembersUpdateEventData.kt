package dev.jombi.kordsb.core.cache.data

import dev.jombi.kordsb.common.entity.Snowflake
import dev.jombi.kordsb.common.entity.optional.Optional
import dev.jombi.kordsb.common.entity.optional.mapList
import dev.jombi.kordsb.gateway.ThreadMembersUpdate

public class ThreadMembersUpdateEventData(
    public val id: Snowflake,
    public val guildId: Snowflake,
    public val memberCount: Int,
    public val addedMembers: Optional<List<ThreadMemberData>> = Optional.Missing(),
    public val removedMemberIds: Optional<List<Snowflake>> = Optional.Missing()
) {
    public companion object {
        public fun from(event: ThreadMembersUpdate): ThreadMembersUpdateEventData = with(event.members) {
            ThreadMembersUpdateEventData(
                id,
                guildId,
                memberCount,
                addedMembers.mapList { ThreadMemberData.from(it) },
                removedMemberIds
            )
        }
    }
}
