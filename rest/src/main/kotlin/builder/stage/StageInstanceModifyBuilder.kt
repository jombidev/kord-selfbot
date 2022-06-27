package dev.jombi.kordsb.rest.builder.stage

import dev.jombi.kordsb.common.annotation.KordDsl
import dev.jombi.kordsb.common.entity.StageInstancePrivacyLevel
import dev.jombi.kordsb.common.entity.optional.Optional
import dev.jombi.kordsb.common.entity.optional.delegate.delegate
import dev.jombi.kordsb.rest.builder.AuditRequestBuilder
import dev.jombi.kordsb.rest.json.request.StageInstanceModifyRequest

@KordDsl
public class StageInstanceModifyBuilder : AuditRequestBuilder<StageInstanceModifyRequest> {

    override var reason: String? = null

    private var _topic: Optional<String> = Optional.Missing()

    /** The topic of the Stage instance (1-120 characters). */
    public var topic: String? by ::_topic.delegate()

    private var _privacyLevel: Optional<StageInstancePrivacyLevel> = Optional.Missing()

    /** The [privacy level][StageInstancePrivacyLevel] of the Stage instance. */
    public var privacyLevel: StageInstancePrivacyLevel? by ::_privacyLevel.delegate()

    override fun toRequest(): StageInstanceModifyRequest = StageInstanceModifyRequest(
        _topic,
        _privacyLevel,
    )
}
