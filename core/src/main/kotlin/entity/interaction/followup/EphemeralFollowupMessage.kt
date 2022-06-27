package dev.jombi.kordsb.core.entity.interaction.followup

import dev.jombi.kordsb.common.entity.Snowflake
import dev.jombi.kordsb.core.Kord
import dev.jombi.kordsb.core.behavior.interaction.followup.EphemeralFollowupMessageBehavior
import dev.jombi.kordsb.core.entity.Message
import dev.jombi.kordsb.core.supplier.EntitySupplier
import dev.jombi.kordsb.core.supplier.EntitySupplyStrategy

/**
 * Holds the followup [Message] resulting from an ephemeral followup message
 * and behaves on it through [EphemeralFollowupMessageBehavior].
 *
 * @param message The message created by this followup. Any rest calls made through the message behavior, e.g.
 * `message.delete()`, will throw since ephemeral messages are not accessible through bot authorization.
 */
public class EphemeralFollowupMessage(
    message: Message,
    override val applicationId: Snowflake,
    override val token: String,
    override val kord: Kord,
    override val supplier: EntitySupplier = kord.defaultSupplier
) : FollowupMessage(message), EphemeralFollowupMessageBehavior {

    override fun withStrategy(strategy: EntitySupplyStrategy<*>): EphemeralFollowupMessage {
        return EphemeralFollowupMessage(message, applicationId, token, kord, strategy.supply(kord))
    }
}
