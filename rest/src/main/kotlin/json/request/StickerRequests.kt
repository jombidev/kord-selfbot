package dev.jombi.kordsb.rest.json.request

import dev.jombi.kordsb.common.entity.optional.Optional
import dev.jombi.kordsb.rest.NamedFile
import kotlinx.serialization.Serializable

@Serializable
public data class GuildStickerCreateRequest(
    val name: String,
    val description: String,
    val tags: String,
)

public data class MultipartGuildStickerCreateRequest(
    val request: GuildStickerCreateRequest,
    val file: NamedFile
)

@Serializable
public data class GuildStickerModifyRequest(
    val name: Optional<String> = Optional.Missing(),
    val description: Optional<String> = Optional.Missing(),
    val tags: Optional<String> = Optional.Missing(),
)
