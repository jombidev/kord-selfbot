package dev.jombi.kordsb.core.event.message

import dev.jombi.kordsb.common.entity.Snowflake
import dev.jombi.kordsb.common.exception.RequestException
import dev.jombi.kordsb.core.Kord
import dev.jombi.kordsb.core.entity.Guild
import dev.jombi.kordsb.core.entity.Member
import dev.jombi.kordsb.core.entity.Message
import dev.jombi.kordsb.core.entity.Strategizable
import dev.jombi.kordsb.core.event.Event
import dev.jombi.kordsb.core.supplier.EntitySupplier
import dev.jombi.kordsb.core.supplier.EntitySupplyStrategy
import dev.jombi.kordsb.core.entity.channel.DmChannel
import dev.jombi.kordsb.core.event.kordCoroutineScope
import kotlinx.coroutines.CoroutineScope
import kotlin.coroutines.CoroutineContext

public class MessageCreateEvent(
    public val message: Message,
    public val guildId: Snowflake?,
    public val member: Member?,
    override val supplier: EntitySupplier = message.kord.defaultSupplier,
    public val coroutineScope: CoroutineScope = kordCoroutineScope(message.kord)
) : Event, CoroutineScope by coroutineScope, Strategizable {
    override val kord: Kord get() = message.kord

    /**
     * Requests to get the guild this message was created in, if it was created in one,
     * returns null if the [Guild] isn't present or the message was a [DM][DmChannel].
     *
     * @throws [RequestException] if anything went wrong during the request.
     */
    public suspend fun getGuild(): Guild? = guildId?.let { supplier.getGuildOrNull(it) }

    override fun withStrategy(strategy: EntitySupplyStrategy<*>): MessageCreateEvent =
        MessageCreateEvent(message, guildId, member, strategy.supply(message.kord))

    override fun toString(): String {
        return "MessageCreateEvent(message=$message, guildId=$guildId, member=$member, supplier=$supplier)"
    }
}
