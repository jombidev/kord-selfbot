package dev.jombi.kordsb.rest.json.response

import dev.jombi.kordsb.common.entity.ChannelType
import dev.jombi.kordsb.common.entity.DiscordChannel
import dev.jombi.kordsb.common.entity.DiscordThreadMember
import kotlinx.serialization.Serializable

@Serializable
public data class PartialChannelResponse(val name: String, val type: ChannelType)


@Serializable
public data class ListThreadsResponse(
    val threads: List<DiscordChannel>,
    val members: List<DiscordThreadMember>,
)
