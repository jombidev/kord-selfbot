package dev.jombi.kordsb.core.event.guild

import dev.jombi.kordsb.common.entity.Snowflake
import dev.jombi.kordsb.common.exception.RequestException
import dev.jombi.kordsb.core.Kord
import dev.jombi.kordsb.core.entity.GuildScheduledEvent
import dev.jombi.kordsb.core.entity.channel.TopGuildChannel
import dev.jombi.kordsb.core.event.kordCoroutineScope
import dev.jombi.kordsb.core.supplier.EntitySupplier
import dev.jombi.kordsb.core.supplier.EntitySupplyStrategy
import kotlinx.coroutines.CoroutineScope

/**
 * Event fired if a [GuildScheduledEvent] gets updated.
 *
 * @property scheduledEvent the updated event, for the old event use [oldEvent]
 * @property oldEvent the event that was in the cache before the update
 *
 * @see GuildScheduledEvent
 * @see GuildScheduledEventEvent
 */
public data class GuildScheduledEventUpdateEvent(
    override val scheduledEvent: GuildScheduledEvent,
    public val oldEvent: GuildScheduledEvent?,
    override val kord: Kord,
    override val supplier: EntitySupplier = kord.defaultSupplier,
    public val coroutineScope: CoroutineScope = kordCoroutineScope(kord)
) : GuildScheduledEventEvent, CoroutineScope by coroutineScope {

    /**
     * The channel id of the channel the event is in.
     *
     * **Note:** This is going to represent the channel of [scheduledEvent] for the previous channel use [oldEvent]
     */
    override val channelId: Snowflake?
        get() = super.channelId

    /**
     * Requests the [TopGuildChannel] this event is in,
     * returns null if the [TopGuildChannel] isn't present or not set.
     *
     * **Note:** This is going to represent the channel of [scheduledEvent] for the previous channel use [oldEvent]
     * @throws [RequestException] if anything went wrong during the request.
     */
    override suspend fun getChannelOrNull(): TopGuildChannel? = super.getChannelOrNull()

    override fun withStrategy(strategy: EntitySupplyStrategy<*>): GuildScheduledEventUpdateEvent =
        copy(supplier = strategy.supply(kord))
}
