package dev.jombi.kordsb.rest.service

import dev.jombi.kordsb.common.entity.DiscordAuditLog
import dev.jombi.kordsb.common.entity.Snowflake
import dev.jombi.kordsb.rest.builder.auditlog.AuditLogGetRequestBuilder
import dev.jombi.kordsb.rest.json.request.AuditLogGetRequest
import dev.jombi.kordsb.rest.request.RequestHandler
import dev.jombi.kordsb.rest.route.Route

public class AuditLogService(requestHandler: RequestHandler) : RestService(requestHandler) {

    public suspend inline fun getAuditLogs(
        guildId: Snowflake,
        builder: AuditLogGetRequestBuilder.() -> Unit,
    ): DiscordAuditLog {
        val request = AuditLogGetRequestBuilder().apply(builder).toRequest()
        return getAuditLogs(guildId, request)
    }

    public suspend fun getAuditLogs(
        guildId: Snowflake,
        request: AuditLogGetRequest,
    ): DiscordAuditLog = call(Route.AuditLogGet) {
        keys[Route.GuildId] = guildId
        request.userId?.let { parameter("user_id", it) }
        request.action?.let { parameter("action_type", "${it.value}") }
        request.before?.let { parameter("before", it) }
        request.limit?.let { parameter("limit", it) }
    }
}
