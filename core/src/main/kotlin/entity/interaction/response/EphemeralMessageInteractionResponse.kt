package dev.jombi.kordsb.core.entity.interaction.response

import dev.jombi.kordsb.common.entity.Snowflake
import dev.jombi.kordsb.core.Kord
import dev.jombi.kordsb.core.behavior.interaction.response.EphemeralMessageInteractionResponseBehavior
import dev.jombi.kordsb.core.entity.Message
import dev.jombi.kordsb.core.supplier.EntitySupplier
import dev.jombi.kordsb.core.supplier.EntitySupplyStrategy

/**
 * An [EphemeralMessageInteractionResponseBehavior] that holds the [message] this is a handle to.
 *
 * @param message The message. Any rest calls made through the message behavior, e.g. `message.delete()`, will throw
 * since ephemeral messages are not accessible through bot authorization.
 */
public class EphemeralMessageInteractionResponse(
    message: Message,
    override val applicationId: Snowflake,
    override val token: String,
    override val kord: Kord,
    override val supplier: EntitySupplier = kord.defaultSupplier,
) : MessageInteractionResponse(message), EphemeralMessageInteractionResponseBehavior {

    override fun withStrategy(strategy: EntitySupplyStrategy<*>): EphemeralMessageInteractionResponse =
        EphemeralMessageInteractionResponse(message, applicationId, token, kord, strategy.supply(kord))
}
