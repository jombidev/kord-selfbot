package dev.jombi.kordsb.core.event.guild

import dev.jombi.kordsb.core.Kord
import dev.jombi.kordsb.core.entity.GuildScheduledEvent
import dev.jombi.kordsb.core.supplier.EntitySupplier
import dev.jombi.kordsb.core.supplier.EntitySupplyStrategy

/**
 * Event fired when a scheduled event got deleted.
 * Use [GuildScheduledEvent.status] to know why the event got deleted.
 *
 * @see GuildScheduledEvent
 * @see GuildScheduledEvent
 */
public data class GuildScheduledEventDeleteEvent(
    public override val scheduledEvent: GuildScheduledEvent,
    override val kord: Kord,
    override val customContext: Any?,
    override val supplier: EntitySupplier = kord.defaultSupplier,
) : GuildScheduledEventEvent {
    override fun withStrategy(strategy: EntitySupplyStrategy<*>): GuildScheduledEventDeleteEvent =
        copy(supplier = strategy.supply(kord))
}
