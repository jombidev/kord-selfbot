package dev.jombi.kordsb.rest.builder.guild

import dev.jombi.kordsb.common.annotation.KordDsl
import dev.jombi.kordsb.common.entity.Snowflake
import dev.jombi.kordsb.rest.Image
import dev.jombi.kordsb.rest.builder.AuditRequestBuilder
import dev.jombi.kordsb.rest.json.request.EmojiCreateRequest

@KordDsl
public class EmojiCreateBuilder(
    public var name: String,
    public var image: Image,
) : AuditRequestBuilder<EmojiCreateRequest> {
    override var reason: String? = null

    public var roles: MutableSet<Snowflake> = mutableSetOf()

    override fun toRequest(): EmojiCreateRequest = EmojiCreateRequest(
        name = name,
        image = image.dataUri,
        roles = roles
    )
}
