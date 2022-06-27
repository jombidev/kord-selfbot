package dev.jombi.kordsb.gateway

import dev.jombi.kordsb.common.entity.DiscordShard
import dev.jombi.kordsb.common.entity.optional.Optional
import dev.jombi.kordsb.common.entity.optional.coerceToMissing
import dev.jombi.kordsb.common.entity.optional.optional
import dev.jombi.kordsb.gateway.builder.PresenceBuilder
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

public data class GatewayConfiguration(
    val token: String,
    val name: String,
    val presence: Optional<DiscordPresence> = Optional.Missing(),
    val threshold: Int,
    val intents: Intents
)

public class GatewayConfigurationBuilder(
    /**
     * The token of the bot.
     */
    public val token: String,
    /**
     * The name of the library.
     */
    public var name: String = "Kord",
    /**
     * The presence the bot should show on login.
     */
    public var presence: DiscordPresence? = null,
    /**
     * A value between 50 and 250, representing the maximum amount of members in a guild
     * before the gateway will stop sending info on offline members.
     */
    public var threshold: Int = 250,

    public var intents: Intents = Intents.nonPrivileged,
) {

    /**
     * Calls the [builder] on a new [PresenceBuilder] and assigns the result to [presence].
     */
    public inline fun presence(builder: PresenceBuilder.() -> Unit) {
        contract {
            callsInPlace(builder, InvocationKind.EXACTLY_ONCE)
        }
        presence = PresenceBuilder().apply(builder).toPresence()
    }

    /**
     * Returns an immutable version of this builder.
     */
    public fun build(): GatewayConfiguration = GatewayConfiguration(
        token,
        name,
        presence.optional().coerceToMissing(),
        threshold,
        intents
    )
}
