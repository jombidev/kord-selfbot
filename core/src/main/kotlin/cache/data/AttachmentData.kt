package dev.jombi.kordsb.core.cache.data

import dev.jombi.kordsb.common.entity.DiscordAttachment
import dev.jombi.kordsb.common.entity.Snowflake
import dev.jombi.kordsb.common.entity.optional.Optional
import dev.jombi.kordsb.common.entity.optional.OptionalBoolean
import dev.jombi.kordsb.common.entity.optional.OptionalInt
import kotlinx.serialization.Serializable

@Serializable
public data class AttachmentData(
    val id: Snowflake,
    val filename: String,
    val description: Optional<String> = Optional.Missing(),
    val contentType: Optional<String> = Optional.Missing(),
    val size: Int,
    val url: String,
    val proxyUrl: String,
    val height: OptionalInt? = OptionalInt.Missing,
    val width: OptionalInt? = OptionalInt.Missing,
    val ephemeral: OptionalBoolean = OptionalBoolean.Missing
) {
    public companion object {
        public fun from(entity: DiscordAttachment): AttachmentData = with(entity) {
            AttachmentData(id, filename, description, contentType, size, url, proxyUrl, height, width, ephemeral)
        }
    }
}
