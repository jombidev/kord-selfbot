package dev.jombi.kordsb.core.cache.data

import dev.jombi.kordsb.common.entity.DiscordTeam
import dev.jombi.kordsb.common.entity.DiscordTeamMember
import dev.jombi.kordsb.common.entity.Snowflake
import dev.jombi.kordsb.common.entity.TeamMembershipState
import kotlinx.serialization.Serializable

/**
 * A serializable data representation of a Discord developer team.
 */
@Serializable
public data class TeamData(
    val id: Snowflake,
    val icon: String? = null,
    val members: List<TeamMemberData>,
    val ownerUserId: Snowflake,
) {
    public companion object {
        public fun from(entity: DiscordTeam): TeamData = with(entity) {
            TeamData(id, icon, members.map { TeamMemberData.from(it) }, ownerUserId)
        }
    }
}

/**
 * A serializable data representation of a Discord developer team member.
 */
@Serializable
public class TeamMemberData(
    public val membershipState: TeamMembershipState,
    public val permissions: List<String>,
    public val teamId: Snowflake,
    public val userId: Snowflake,
) {
    public companion object {
        public fun from(entity: DiscordTeamMember): TeamMemberData = with(entity) {
            TeamMemberData(membershipState, permissions, teamId = teamId, userId = user.id)
        }
    }
}
