package dev.jombi.kordsb.rest.builder.channel.thread

import dev.jombi.kordsb.common.entity.ArchiveDuration
import dev.jombi.kordsb.rest.builder.AuditRequestBuilder
import dev.jombi.kordsb.rest.json.request.StartThreadRequest

public class StartThreadWithMessageBuilder(
    public var name: String,
    public var autoArchiveDuration: ArchiveDuration,
) : AuditRequestBuilder<StartThreadRequest> {
    override var reason: String? = null

    override fun toRequest(): StartThreadRequest {
        return StartThreadRequest(
            name = name,
            autoArchiveDuration = autoArchiveDuration
        )
    }
}
