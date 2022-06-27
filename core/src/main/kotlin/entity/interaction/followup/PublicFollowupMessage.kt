package dev.jombi.kordsb.core.entity.interaction.followup

import dev.jombi.kordsb.common.entity.Snowflake
import dev.jombi.kordsb.core.Kord
import dev.jombi.kordsb.core.behavior.interaction.followup.PublicFollowupMessageBehavior
import dev.jombi.kordsb.core.entity.Message
import dev.jombi.kordsb.core.supplier.EntitySupplier
import dev.jombi.kordsb.core.supplier.EntitySupplyStrategy

/**
 * Holds the followup [Message] resulting from a public followup message
 * and behaves on it through [PublicFollowupMessageBehavior]
 *
 * @param message The message created by this followup.
 * To use the message behavior your application must be authorized as a bot.
 */
public class PublicFollowupMessage(
    message: Message,
    override val applicationId: Snowflake,
    override val token: String,
    override val kord: Kord,
    override val supplier: EntitySupplier = kord.defaultSupplier
) : FollowupMessage(message), PublicFollowupMessageBehavior {

    override fun withStrategy(strategy: EntitySupplyStrategy<*>): PublicFollowupMessage {
        return PublicFollowupMessage(message, applicationId, token, kord, strategy.supply(kord))
    }
}