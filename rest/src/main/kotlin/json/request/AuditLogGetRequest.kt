package dev.jombi.kordsb.rest.json.request

import dev.jombi.kordsb.common.entity.AuditLogEvent
import dev.jombi.kordsb.common.entity.Snowflake

public data class AuditLogGetRequest(
    val userId: Snowflake? = null,
    val action: AuditLogEvent? = null,
    val before: Snowflake? = null,
    val limit: Int? = null,
)
