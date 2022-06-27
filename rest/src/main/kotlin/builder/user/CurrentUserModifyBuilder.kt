package dev.jombi.kordsb.rest.builder.user

import dev.jombi.kordsb.common.annotation.KordDsl
import dev.jombi.kordsb.common.entity.optional.Optional
import dev.jombi.kordsb.common.entity.optional.delegate.delegate
import dev.jombi.kordsb.common.entity.optional.map
import dev.jombi.kordsb.rest.Image
import dev.jombi.kordsb.rest.builder.RequestBuilder
import dev.jombi.kordsb.rest.json.request.CurrentUserModifyRequest

@KordDsl
public class CurrentUserModifyBuilder : RequestBuilder<CurrentUserModifyRequest> {

    private var _username: Optional<String> = Optional.Missing()
    public var username: String? by ::_username.delegate()

    private var _avatar: Optional<Image?> = Optional.Missing()
    public var avatar: Image? by ::_avatar.delegate()

    override fun toRequest(): CurrentUserModifyRequest = CurrentUserModifyRequest(
        _username, _avatar.map { it.dataUri }
    )

}
