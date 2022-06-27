package dev.jombi.kordsb.core.cache.data

import dev.jombi.kordsb.common.entity.Snowflake
import dev.jombi.kordsb.common.entity.optional.OptionalSnowflake
import dev.jombi.kordsb.gateway.DiscordDeletedInvite
import kotlinx.serialization.Serializable

@Serializable
public data class InviteDeleteData(
    val channelId: Snowflake,
    val guildId: OptionalSnowflake = OptionalSnowflake.Missing,
    val code: String
) {

    public companion object {
        public fun from(entity: DiscordDeletedInvite): InviteDeleteData = with(entity) {
            InviteDeleteData(channelId, guildId, code)
        }
    }
}
