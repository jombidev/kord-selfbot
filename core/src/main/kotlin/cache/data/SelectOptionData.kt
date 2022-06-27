package dev.jombi.kordsb.core.cache.data

import dev.jombi.kordsb.common.entity.DiscordPartialEmoji
import dev.jombi.kordsb.common.entity.DiscordSelectOption
import dev.jombi.kordsb.common.entity.optional.Optional
import dev.jombi.kordsb.common.entity.optional.OptionalBoolean
import kotlinx.serialization.Serializable

@Serializable
public data class SelectOptionData(
    val label: String,
    val value: String,
    val description: Optional<String> = Optional.Missing(),
    val emoji: Optional<DiscordPartialEmoji> = Optional.Missing(),
    val default: OptionalBoolean = OptionalBoolean.Missing
) {
    public companion object {
        public fun from(entity: DiscordSelectOption): SelectOptionData = with(entity) {
            SelectOptionData(
                label = label,
                value = value,
                description = description,
                emoji = emoji,
                default = default
            )
        }
    }
}
