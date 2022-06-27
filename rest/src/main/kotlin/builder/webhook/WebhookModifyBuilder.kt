package dev.jombi.kordsb.rest.builder.webhook

import dev.jombi.kordsb.common.annotation.KordDsl
import dev.jombi.kordsb.common.entity.Snowflake
import dev.jombi.kordsb.common.entity.optional.Optional
import dev.jombi.kordsb.common.entity.optional.OptionalSnowflake
import dev.jombi.kordsb.common.entity.optional.delegate.delegate
import dev.jombi.kordsb.common.entity.optional.map
import dev.jombi.kordsb.rest.Image
import dev.jombi.kordsb.rest.builder.AuditRequestBuilder
import dev.jombi.kordsb.rest.json.request.WebhookModifyRequest

@KordDsl
public class WebhookModifyBuilder : AuditRequestBuilder<WebhookModifyRequest> {
    override var reason: String? = null

    private var _name: Optional<String> = Optional.Missing()
    public var name: String? by ::_name.delegate()

    private var _avatar: Optional<Image?> = Optional.Missing()
    public var avatar: Image? by ::_avatar.delegate()

    private var _channelId: OptionalSnowflake = OptionalSnowflake.Missing
    public var channelId: Snowflake? by ::_channelId.delegate()

    override fun toRequest(): WebhookModifyRequest = WebhookModifyRequest(
        name = _name,
        avatar = _avatar.map { it.dataUri },
        channelId = _channelId
    )
}
