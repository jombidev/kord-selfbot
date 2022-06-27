package dev.jombi.kordsb.core.cache.data

import dev.jombi.kordsb.common.entity.Snowflake
import dev.jombi.kordsb.common.entity.optional.Optional
import dev.jombi.kordsb.common.entity.optional.mapList
import dev.jombi.kordsb.gateway.GuildMembersChunkData
import kotlinx.serialization.Serializable

@Serializable
public data class MembersChunkData(
    val guildId: Snowflake,
    val members: Set<MemberData>,
    val users: Set<UserData>,
    val chunkIndex: Int,
    val chunkCount: Int,
    val notFound: Optional<Set<Snowflake>> = Optional.Missing(),
    val presences: Optional<List<PresenceData>> = Optional.Missing(),
    val nonce: Optional<String> = Optional.Missing(),
) {
    public companion object {

        public fun from(entity: GuildMembersChunkData): MembersChunkData = with(entity) {
            MembersChunkData(
                guildId,
                members.map { MemberData.from(userId = it.user.value!!.id, guildId = guildId, it) }.toSet(),
                members.map { UserData.from(it.user.value!!) }.toSet(),
                chunkIndex,
                chunkCount,
                notFound,
                presences.mapList { PresenceData.from(guildId, it) },
                nonce
            )
        }

    }
}
