package dev.jombi.kordsb.core.cache.data

import dev.jombi.kordsb.common.entity.DiscordClientStatus
import dev.jombi.kordsb.common.entity.PresenceStatus
import dev.jombi.kordsb.common.entity.optional.Optional
import kotlinx.serialization.Serializable

@Serializable
public data class ClientStatusData(
    val desktop: Optional<PresenceStatus> = Optional.Missing(),
    val mobile: Optional<PresenceStatus> = Optional.Missing(),
    val web: Optional<PresenceStatus> = Optional.Missing()
) {
    public companion object {
        public fun from(entity: DiscordClientStatus): ClientStatusData = with(entity) {
            ClientStatusData(desktop, mobile, web)
        }
    }
}
