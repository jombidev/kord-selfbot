package dev.jombi.kordsb.core.entity.interaction.response

import dev.jombi.kordsb.common.entity.Snowflake
import dev.jombi.kordsb.core.Kord
import dev.jombi.kordsb.core.behavior.interaction.response.PublicMessageInteractionResponseBehavior
import dev.jombi.kordsb.core.entity.Message
import dev.jombi.kordsb.core.supplier.EntitySupplier
import dev.jombi.kordsb.core.supplier.EntitySupplyStrategy

/**
 * A [PublicMessageInteractionResponseBehavior] that holds the [message] this is a handle to.
 *
 * @param message The message. To use the message behavior your application must be authorized as a bot.
 */
public class PublicMessageInteractionResponse(
    message: Message,
    override val applicationId: Snowflake,
    override val token: String,
    override val kord: Kord,
    override val supplier: EntitySupplier = kord.defaultSupplier,
) : MessageInteractionResponse(message), PublicMessageInteractionResponseBehavior {

    override fun withStrategy(strategy: EntitySupplyStrategy<*>): PublicMessageInteractionResponse =
        PublicMessageInteractionResponse(message, applicationId, token, kord, strategy.supply(kord))
}
