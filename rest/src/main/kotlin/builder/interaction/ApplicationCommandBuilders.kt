package dev.jombi.kordsb.rest.builder.interaction

import dev.jombi.kordsb.common.annotation.KordDsl
import dev.jombi.kordsb.common.entity.ApplicationCommandType
import dev.jombi.kordsb.common.entity.Permissions
import dev.jombi.kordsb.rest.builder.RequestBuilder
import dev.jombi.kordsb.rest.json.request.ApplicationCommandCreateRequest
import dev.jombi.kordsb.rest.json.request.ApplicationCommandModifyRequest

@KordDsl
public interface ApplicationCommandCreateBuilder : LocalizedNameCreateBuilder,
    RequestBuilder<ApplicationCommandCreateRequest> {

    public var defaultMemberPermissions: Permissions?

    @Deprecated("'defaultPermission' is deprecated in favor of 'defaultMemberPermissions' and 'dmPermission'. Setting 'defaultPermission' to false can be replaced by setting 'defaultMemberPermissions' to empty Permissions and 'dmPermission' to false ('dmPermission' is only available for global commands).")
    public var defaultPermission: Boolean?
    public val type: ApplicationCommandType

    /**
     * Disables the command for everyone except admins by default.
     *
     * **This does not ensure normal users cannot execute the command, any admin can change this setting.**
     */
    public fun disableCommandInGuilds() {
        defaultMemberPermissions = Permissions()
    }
}

@KordDsl
public interface GlobalApplicationCommandCreateBuilder : ApplicationCommandCreateBuilder {
    public var dmPermission: Boolean?
}

@KordDsl
public interface GlobalApplicationCommandModifyBuilder : ApplicationCommandModifyBuilder {
    public var dmPermission: Boolean?
}

@KordDsl
public interface ApplicationCommandModifyBuilder : LocalizedNameModifyBuilder,
    RequestBuilder<ApplicationCommandModifyRequest> {

    public var defaultMemberPermissions: Permissions?

    @Deprecated("'defaultPermission' is deprecated in favor of 'defaultMemberPermissions' and 'dmPermission'. Setting 'defaultPermission' to false can be replaced by setting 'defaultMemberPermissions' to empty Permissions and 'dmPermission' to false ('dmPermission' is only available for global commands).")
    public var defaultPermission: Boolean?
}
