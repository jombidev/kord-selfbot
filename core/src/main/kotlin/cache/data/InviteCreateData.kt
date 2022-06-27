package dev.jombi.kordsb.core.cache.data

import dev.jombi.kordsb.common.entity.InviteTargetType
import dev.jombi.kordsb.common.entity.Snowflake
import dev.jombi.kordsb.common.entity.optional.Optional
import dev.jombi.kordsb.common.entity.optional.OptionalSnowflake
import dev.jombi.kordsb.common.entity.optional.map
import dev.jombi.kordsb.common.entity.optional.mapSnowflake
import dev.jombi.kordsb.common.serialization.DurationInSeconds
import dev.jombi.kordsb.gateway.DiscordCreatedInvite
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
public data class InviteCreateData(
    val channelId: Snowflake,
    val code: String,
    val createdAt: Instant,
    val guildId: OptionalSnowflake = OptionalSnowflake.Missing,
    val inviterId: OptionalSnowflake = OptionalSnowflake.Missing,
    val maxAge: DurationInSeconds,
    val maxUses: Int,
    val targetType: Optional<InviteTargetType> = Optional.Missing(),
    val targetUserId: OptionalSnowflake = OptionalSnowflake.Missing,
    val targetApplication: Optional<PartialApplicationData> = Optional.Missing(),
    @Deprecated("No longer documented. Use 'targetType' instead.", ReplaceWith("this.targetType"))
    val targetUserType: Optional<@Suppress("DEPRECATION") dev.jombi.kordsb.common.entity.TargetUserType> = Optional.Missing(),
    val temporary: Boolean,
    val uses: Int,
) {

    public companion object {
        public fun from(entity: DiscordCreatedInvite): InviteCreateData = with(entity) {
            InviteCreateData(
                channelId,
                code,
                createdAt,
                guildId,
                inviter.mapSnowflake { it.id },
                maxAge,
                maxUses,
                targetType,
                targetUser.mapSnowflake { it.id },
                targetApplication.map { PartialApplicationData.from(it) },
                @Suppress("DEPRECATION")
                targetUserType,
                temporary,
                uses,
            )
        }
    }
}
