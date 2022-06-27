package dev.jombi.kordsb.gateway.builder

import dev.jombi.kordsb.common.annotation.KordDsl
import dev.jombi.kordsb.common.entity.PresenceStatus
import dev.jombi.kordsb.gateway.DiscordPresence
import dev.jombi.kordsb.gateway.Intents
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

@KordDsl
public class LoginBuilder {
    public var presence: DiscordPresence = DiscordPresence(PresenceStatus.Online, false)
    public var intents: Intents = Intents.nonPrivileged
    public var name: String = "Kord"

    public inline fun presence(builder: PresenceBuilder.() -> Unit) {
        contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
        this.presence = PresenceBuilder().apply(builder).toPresence()
    }

    public inline fun intents(builder: Intents.IntentsBuilder.() -> Unit) {
        contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
        this.intents = Intents(builder)
    }
}
