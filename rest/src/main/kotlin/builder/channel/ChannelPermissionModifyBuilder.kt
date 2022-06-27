package dev.jombi.kordsb.rest.builder.channel

import dev.jombi.kordsb.common.annotation.KordDsl
import dev.jombi.kordsb.common.entity.OverwriteType
import dev.jombi.kordsb.common.entity.Permissions
import dev.jombi.kordsb.rest.builder.AuditRequestBuilder
import dev.jombi.kordsb.rest.json.request.ChannelPermissionEditRequest

@KordDsl
public class ChannelPermissionModifyBuilder(private var type: OverwriteType) :
    AuditRequestBuilder<ChannelPermissionEditRequest> {

    override var reason: String? = null

    /**
     * The permissions that are explicitly allowed for this channel.
     */
    public var allowed: Permissions = Permissions()

    /**
     * The permissions that are explicitly denied for this channel.
     */
    public var denied: Permissions = Permissions()

    override fun toRequest(): ChannelPermissionEditRequest = ChannelPermissionEditRequest(allowed, denied, type)

}
