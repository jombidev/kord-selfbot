package dev.jombi.kordsb.core.cache.data

import dev.jombi.kordsb.common.entity.Overwrite
import dev.jombi.kordsb.common.entity.*
import kotlinx.serialization.Serializable

@Serializable
public data class PermissionOverwriteData(
    val id: Snowflake,
    val type: OverwriteType,
    val allowed: Permissions,
    val denied: Permissions
) {
    public companion object {
        public fun from(entity: Overwrite): PermissionOverwriteData = with(entity) {
            PermissionOverwriteData(id, type, allow, deny)
        }
    }
}
