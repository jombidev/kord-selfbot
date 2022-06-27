package dev.jombi.kordsb.rest.builder.webhook

import dev.jombi.kordsb.common.annotation.KordDsl
import dev.jombi.kordsb.common.entity.optional.Optional
import dev.jombi.kordsb.common.entity.optional.delegate.delegate
import dev.jombi.kordsb.common.entity.optional.map
import dev.jombi.kordsb.rest.Image
import dev.jombi.kordsb.rest.builder.AuditRequestBuilder
import dev.jombi.kordsb.rest.json.request.WebhookCreateRequest

@KordDsl
public class WebhookCreateBuilder(public var name: String) : AuditRequestBuilder<WebhookCreateRequest> {
    override var reason: String? = null

    private var _avatar: Optional<Image> = Optional.Missing()
    public var avatar: Image? by ::_avatar.delegate()

    override fun toRequest(): WebhookCreateRequest = WebhookCreateRequest(name, _avatar.map { it.dataUri })
}
