package dev.jombi.kordsb.core.behavior.interaction.followup

import dev.jombi.kordsb.common.entity.Snowflake
import dev.jombi.kordsb.core.Kord
import dev.jombi.kordsb.core.cache.data.toData
import dev.jombi.kordsb.core.entity.Message
import dev.jombi.kordsb.core.entity.interaction.followup.EphemeralFollowupMessage
import dev.jombi.kordsb.core.supplier.EntitySupplier
import dev.jombi.kordsb.core.supplier.EntitySupplyStrategy
import dev.jombi.kordsb.rest.builder.message.modify.FollowupMessageModifyBuilder
import dev.jombi.kordsb.rest.request.RestRequestException
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

/**
 * The behavior of a [Discord Followup Message](https://discord.com/developers/docs/interactions/slash-commands#followup-messages)
 * This followup message is visible to *only* to the user who made the interaction.
 */

public interface EphemeralFollowupMessageBehavior : FollowupMessageBehavior {

    override fun withStrategy(strategy: EntitySupplyStrategy<*>): EphemeralFollowupMessageBehavior {
        return EphemeralFollowupMessageBehavior(id, applicationId, token, channelId, kord, strategy.supply(kord))
    }
}

/**
 * Requests to edit this followup message.
 *
 * @return The edited [EphemeralFollowupMessage] of the interaction response.
 *
 * @throws RestRequestException if something went wrong during the request.
 */
public suspend inline fun EphemeralFollowupMessageBehavior.edit(
    builder: FollowupMessageModifyBuilder.() -> Unit,
): EphemeralFollowupMessage {
    contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
    val response = kord.rest.interaction.modifyFollowupMessage(applicationId, token, id, builder)
    return EphemeralFollowupMessage(Message(response.toData(), kord), applicationId, token, kord)
}


public fun EphemeralFollowupMessageBehavior(
    id: Snowflake,
    applicationId: Snowflake,
    token: String,
    channelId: Snowflake,
    kord: Kord,
    supplier: EntitySupplier
): EphemeralFollowupMessageBehavior = object : EphemeralFollowupMessageBehavior {
    override val applicationId: Snowflake
        get() = applicationId
    override val token: String
        get() = token
    override val channelId: Snowflake
        get() = channelId
    override val kord: Kord
        get() = kord
    override val id: Snowflake
        get() = id
    override val supplier: EntitySupplier
        get() = supplier

}
