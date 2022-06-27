package dev.jombi.kordsb.core.cache.data

import dev.jombi.kordsb.common.entity.DiscordIntegrationsAccount
import kotlinx.serialization.Serializable

@Serializable
public data class IntegrationsAccountData(
    val id: String,
    val name: String,
) {
    public companion object {
        public fun from(entity: DiscordIntegrationsAccount): IntegrationsAccountData = with(entity) {
            IntegrationsAccountData(id, name)
        }
    }
}
