package dev.jombi.kordsb.rest.builder.channel

import dev.jombi.kordsb.common.annotation.KordDsl
import dev.jombi.kordsb.common.entity.Overwrite
import dev.jombi.kordsb.common.entity.OverwriteType
import dev.jombi.kordsb.common.entity.Permissions
import dev.jombi.kordsb.common.entity.Snowflake

@KordDsl
public class PermissionOverwriteBuilder(private val type: OverwriteType, private val id: Snowflake) {
    public var allowed: Permissions = Permissions()
    public var denied: Permissions = Permissions()

    public fun toOverwrite(): Overwrite = Overwrite(id = id, allow = allowed, deny = denied, type = type)
}
