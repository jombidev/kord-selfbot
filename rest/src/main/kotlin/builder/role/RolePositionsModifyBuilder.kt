package dev.jombi.kordsb.rest.builder.role

import dev.jombi.kordsb.rest.builder.AuditRequestBuilder
import dev.jombi.kordsb.common.annotation.KordDsl
import dev.jombi.kordsb.common.entity.Snowflake
import dev.jombi.kordsb.rest.json.request.GuildRolePositionModifyRequest

@KordDsl
public class RolePositionsModifyBuilder : AuditRequestBuilder<GuildRolePositionModifyRequest> {
    override var reason: String? = null
    private val swaps: MutableList<Pair<Snowflake, Int>> = mutableListOf()

    public fun move(pair: Pair<Snowflake, Int>) {
        swaps += pair.first to pair.second
    }

    public fun move(vararg pairs: Pair<Snowflake, Int>) {
        swaps += pairs.map { it.first to it.second }
    }

    override fun toRequest(): GuildRolePositionModifyRequest =
        GuildRolePositionModifyRequest(swaps)
}
