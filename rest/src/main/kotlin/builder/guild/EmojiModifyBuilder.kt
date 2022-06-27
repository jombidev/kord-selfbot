package dev.jombi.kordsb.rest.builder.guild

import dev.jombi.kordsb.rest.builder.AuditRequestBuilder
import dev.jombi.kordsb.common.annotation.KordDsl
import dev.jombi.kordsb.common.entity.Snowflake
import dev.jombi.kordsb.common.entity.optional.Optional
import dev.jombi.kordsb.common.entity.optional.delegate.delegate
import dev.jombi.kordsb.rest.json.request.EmojiModifyRequest

@KordDsl
public class EmojiModifyBuilder : AuditRequestBuilder<EmojiModifyRequest> {
    override var reason: String? = null

    private var _name: Optional<String> = Optional.Missing()
    public var name: String? by ::_name.delegate()

    private var _roles: Optional<MutableSet<Snowflake>?> = Optional.Missing()
    public var roles: MutableSet<Snowflake>? by ::_roles.delegate()

    override fun toRequest(): EmojiModifyRequest = EmojiModifyRequest(
        name = _name,
        roles = _roles
    )
}
