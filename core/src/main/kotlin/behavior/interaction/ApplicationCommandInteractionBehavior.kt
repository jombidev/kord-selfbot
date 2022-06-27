package dev.jombi.kordsb.core.behavior.interaction

import dev.jombi.kordsb.common.entity.Snowflake
import dev.jombi.kordsb.core.Kord
import dev.jombi.kordsb.core.entity.interaction.ApplicationCommandInteraction
import dev.jombi.kordsb.core.supplier.EntitySupplier
import dev.jombi.kordsb.core.supplier.EntitySupplyStrategy

/** The behavior of an [ApplicationCommandInteraction]. */
public interface ApplicationCommandInteractionBehavior : ModalParentInteractionBehavior {

    override fun withStrategy(strategy: EntitySupplyStrategy<*>): ApplicationCommandInteractionBehavior =
        ApplicationCommandInteractionBehavior(id, channelId, token, applicationId, kord, supplier)
}

internal fun ApplicationCommandInteractionBehavior(
    id: Snowflake,
    channelId: Snowflake,
    token: String,
    applicationId: Snowflake,
    kord: Kord,
    supplier: EntitySupplier = kord.defaultSupplier
) = object : ApplicationCommandInteractionBehavior {
    override val id: Snowflake = id
    override val channelId: Snowflake = channelId
    override val token: String = token
    override val applicationId: Snowflake = applicationId
    override val kord: Kord = kord
    override val supplier: EntitySupplier = supplier
}
