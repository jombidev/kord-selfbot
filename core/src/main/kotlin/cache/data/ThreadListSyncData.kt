package dev.jombi.kordsb.core.cache.data

import dev.jombi.kordsb.common.entity.Snowflake
import dev.jombi.kordsb.common.entity.optional.Optional
import dev.jombi.kordsb.gateway.ThreadListSync

public class ThreadListSyncData(
    public val guildId: Snowflake,
    public val channelIds: Optional<List<Snowflake>> = Optional.Missing(),
    public val threads: List<ChannelData>,
    public val members: List<ThreadMemberData>
) {
    public companion object {
        public fun from(event: ThreadListSync): ThreadListSyncData = with(event.sync) {
            return ThreadListSyncData(
                guildId,
                channelIds,
                threads.map { it.toData() },
                members.map { ThreadMemberData.from(it) }
            )
        }
    }
}
