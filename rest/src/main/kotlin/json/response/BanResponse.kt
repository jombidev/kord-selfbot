package dev.jombi.kordsb.rest.json.response

import dev.jombi.kordsb.common.entity.DiscordUser
import kotlinx.serialization.Serializable

@Serializable
public data class BanResponse(
    val reason: String?,
    val user: DiscordUser
)
