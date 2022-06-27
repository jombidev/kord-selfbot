package dev.jombi.kordsb.core.cache.data

import dev.kord.cache.api.data.DataDescription
import dev.kord.cache.api.data.description
import dev.jombi.kordsb.common.entity.DiscordGuildApplicationCommandPermissions
import dev.jombi.kordsb.common.entity.Snowflake

public data class GuildApplicationCommandPermissionsData(
    val id: Snowflake,
    val applicationId: Snowflake,
    val guildId: Snowflake,
    val permissions: List<GuildApplicationCommandPermissionData>
) {

    public companion object {
        public val description: DataDescription<GuildApplicationCommandPermissionsData, Snowflake> =
            description(GuildApplicationCommandPermissionsData::id) {
                link(GuildApplicationCommandPermissionsData::guildId to GuildData::id)
                link(GuildApplicationCommandPermissionsData::id to ApplicationCommandData::id)
            }

        public fun from(permissions: DiscordGuildApplicationCommandPermissions): GuildApplicationCommandPermissionsData =
            with(permissions) {
                GuildApplicationCommandPermissionsData(
                    id,
                    applicationId,
                    guildId,
                    this.permissions.map(GuildApplicationCommandPermissionData::from)
                )
            }
    }
}
