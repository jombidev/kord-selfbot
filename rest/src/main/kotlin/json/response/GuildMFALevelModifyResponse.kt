package json.response

import dev.jombi.kordsb.common.entity.MFALevel
import kotlinx.serialization.Serializable

@Serializable
public data class GuildMFALevelModifyResponse(val level: MFALevel)