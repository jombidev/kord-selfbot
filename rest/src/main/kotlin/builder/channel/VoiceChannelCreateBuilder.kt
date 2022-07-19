package dev.jombi.kordsb.rest.builder.channel

import dev.jombi.kordsb.common.annotation.KordDsl
import dev.jombi.kordsb.common.entity.ChannelType
import dev.jombi.kordsb.common.entity.Overwrite
import dev.jombi.kordsb.common.entity.Snowflake
import dev.jombi.kordsb.common.entity.optional.Optional
import dev.jombi.kordsb.common.entity.optional.OptionalBoolean
import dev.jombi.kordsb.common.entity.optional.OptionalInt
import dev.jombi.kordsb.common.entity.optional.OptionalSnowflake
import dev.jombi.kordsb.common.entity.optional.delegate.delegate
import dev.jombi.kordsb.rest.builder.AuditRequestBuilder
import dev.jombi.kordsb.rest.json.request.GuildChannelCreateRequest

@KordDsl
public class VoiceChannelCreateBuilder(public var name: String) :
    PermissionOverwritesCreateBuilder,
    AuditRequestBuilder<GuildChannelCreateRequest> {
    override var reason: String? = null

    private var _bitrate: OptionalInt = OptionalInt.Missing
    public var bitrate: Int? by ::_bitrate.delegate()

    private var _userLimit: OptionalInt = OptionalInt.Missing
    public var userLimit: Int? by ::_userLimit.delegate()

    private var _parentId: OptionalSnowflake = OptionalSnowflake.Missing
    public var parentId: Snowflake? by ::_parentId.delegate()

    private var _nsfw: OptionalBoolean = OptionalBoolean.Missing
    public var nsfw: Boolean? by ::_nsfw.delegate()

    private var _position: OptionalInt = OptionalInt.Missing
    public var position: Int? by ::_position.delegate()

    override var permissionOverwrites: MutableSet<Overwrite> = mutableSetOf()

    override fun toRequest(): GuildChannelCreateRequest = GuildChannelCreateRequest(
        name = name,
        bitrate = _bitrate,
        userLimit = _userLimit,
        parentId = _parentId,
        nsfw = _nsfw,
        position = _position,
        permissionOverwrite = Optional.missingOnEmpty(permissionOverwrites),
        type = ChannelType.GuildVoice
    )
}
