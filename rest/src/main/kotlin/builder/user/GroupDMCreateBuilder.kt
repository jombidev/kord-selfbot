package dev.jombi.kordsb.rest.builder.user

import dev.jombi.kordsb.common.annotation.KordDsl
import dev.jombi.kordsb.common.entity.Snowflake
import dev.jombi.kordsb.rest.builder.RequestBuilder
import dev.jombi.kordsb.rest.json.request.GroupDMCreateRequest

@KordDsl
public class GroupDMCreateBuilder : RequestBuilder<GroupDMCreateRequest> {

    public val tokens: MutableList<String> = mutableListOf()
    public val nicknames: MutableMap<Snowflake, String> = mutableMapOf()

    override fun toRequest(): GroupDMCreateRequest = GroupDMCreateRequest(
        tokens.toList(),
        nicknames.mapKeys { it.value }
    )
}
