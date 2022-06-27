package cache.data;

import dev.jombi.kordsb.common.entity.DiscordMessageInteraction
import dev.jombi.kordsb.common.entity.InteractionType
import dev.jombi.kordsb.common.entity.Snowflake
import kotlinx.serialization.Serializable

@Serializable
public data class MessageInteractionData(
    val id: Snowflake,
    val type: InteractionType,
    val name: String,
    val user: Snowflake
) {
    public companion object {
        public fun from(entity: DiscordMessageInteraction): MessageInteractionData = with(entity) {
            MessageInteractionData(id, type, name, user.id)
        }
    }
}
