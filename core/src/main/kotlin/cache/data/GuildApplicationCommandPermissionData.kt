package dev.jombi.kordsb.core.cache.data

import dev.jombi.kordsb.common.entity.DiscordGuildApplicationCommandPermission
import dev.jombi.kordsb.common.entity.Snowflake


public data class GuildApplicationCommandPermissionData(
    val id: Snowflake,
    val type: DiscordGuildApplicationCommandPermission.Type,
    val permission: Boolean
) {
    public companion object {
        public fun from(permission: DiscordGuildApplicationCommandPermission): GuildApplicationCommandPermissionData =
            with(permission) {
                GuildApplicationCommandPermissionData(id, type, this.permission)
            }
    }
}
