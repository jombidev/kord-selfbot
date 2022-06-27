package dev.jombi.kordsb.core.event.gateway

import dev.jombi.kordsb.common.entity.Snowflake
import dev.jombi.kordsb.core.Kord
import dev.jombi.kordsb.core.behavior.GuildBehavior
import dev.jombi.kordsb.core.entity.Guild
import dev.jombi.kordsb.core.entity.Strategizable
import dev.jombi.kordsb.core.entity.User
import dev.jombi.kordsb.core.event.Event
import dev.jombi.kordsb.core.event.kordCoroutineScope
import dev.jombi.kordsb.core.supplier.EntitySupplier
import dev.jombi.kordsb.core.supplier.EntitySupplyStrategy
import dev.jombi.kordsb.gateway.Command
import dev.jombi.kordsb.gateway.Gateway
import dev.jombi.kordsb.gateway.GatewayCloseCode
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlin.coroutines.CoroutineContext

public sealed class GatewayEvent : Event

public class ConnectEvent(
    override val kord: Kord,
    public val coroutineScope: CoroutineScope = kordCoroutineScope(kord)
) : GatewayEvent(), CoroutineScope by coroutineScope

public sealed class DisconnectEvent : GatewayEvent() {

    /**
     * A Gateway was detached, all resources tied to that gateway should be freed.
     */
    public class DetachEvent(
        override val kord: Kord,
        public val coroutineScope: CoroutineScope = kordCoroutineScope(kord)
    ) : DisconnectEvent(), CoroutineScope by coroutineScope {
        override fun toString(): String {
            return "DetachEvent(kord=$kord)"
        }
    }

    /**
     * The user closed the Gateway connection.
     */
    public class UserCloseEvent(
        override val kord: Kord,
        public val coroutineScope: CoroutineScope = kordCoroutineScope(kord)
    ) : DisconnectEvent(), CoroutineScope by coroutineScope {
        override fun toString(): String {
            return "UserCloseEvent(kord=$kord)"
        }
    }

    /**
     * The connection was closed because of a timeout, probably due to a loss of internet connection.
     */
    public class TimeoutEvent(
        override val kord: Kord,
        public val coroutineScope: CoroutineScope = kordCoroutineScope(kord)
    ) : DisconnectEvent(), CoroutineScope by coroutineScope {
        override fun toString(): String {
            return "TimeoutEvent(kord=$kord)"
        }
    }

    /**
     * Discord closed the connection with a [closeCode].
     *
     * @param recoverable true if the gateway will automatically try to reconnect.
     */
    public class DiscordCloseEvent(
        override val kord: Kord,
        public val closeCode: GatewayCloseCode,
        public val recoverable: Boolean,
        public val coroutineScope: CoroutineScope = kordCoroutineScope(kord)
    ) : DisconnectEvent(), CoroutineScope by coroutineScope {
        override fun toString(): String {
            return "DiscordCloseEvent(kord=$kord, closeCode=$closeCode, recoverable=$recoverable)"
        }
    }

    /**
     *  The Gateway has failed to establish a connection too many times and will not try to reconnect anymore.
     *  The user is free to manually connect again using [Gateway.start], otherwise all resources linked to the Gateway should free and the Gateway [detached][Gateway.detach].
     */
    public class RetryLimitReachedEvent(
        override val kord: Kord,
        public val coroutineScope: CoroutineScope = kordCoroutineScope(kord)
    ) : DisconnectEvent(), CoroutineScope by coroutineScope {
        override fun toString(): String {
            return "RetryLimitReachedEvent(kord=$kord)"
        }
    }

    /**
     * Discord requested a reconnect, the gateway will close and attempt to resume the session.
     */
    public class ReconnectingEvent(
        override val kord: Kord,
        public val coroutineScope: CoroutineScope = kordCoroutineScope(kord)
    ) : DisconnectEvent(), CoroutineScope by coroutineScope {
        override fun toString(): String {
            return "ReconnectingEvent(kord=$kord)"
        }
    }

    /**
     * The gateway closed and will attempt to start a new session.
     */
    public class SessionReset(
        override val kord: Kord,
        public val coroutineScope: CoroutineScope = kordCoroutineScope(kord)
    ) : DisconnectEvent(), CoroutineScope by coroutineScope {
        override fun toString(): String {
            return "SessionReset(kord=$kord)"
        }
    }

    /**
     * Discord is no longer responding to the gateway commands, the connection will be closed and an attempt to resume the session will be made.
     * Any [commands][Command] send recently might not complete, and won't be automatically requeued.
     */
    public class ZombieConnectionEvent(
        override val kord: Kord,
        public val coroutineScope: CoroutineScope = kordCoroutineScope(kord)
    ) : DisconnectEvent(), CoroutineScope by coroutineScope {
        override fun toString(): String {
            return "ZombieConnectionEvent(kord=$kord)"
        }
    }

}

public class ReadyEvent(
    public val gatewayVersion: Int,
    public val guildIds: Set<Snowflake>,
    public val self: User,
    public val sessionId: String,
    override val kord: Kord,
    override val supplier: EntitySupplier = kord.defaultSupplier,
    public val coroutineScope: CoroutineScope = kordCoroutineScope(kord)
) : GatewayEvent(), CoroutineScope by coroutineScope, Strategizable {

    public val guilds: Set<GuildBehavior> get() = guildIds.map { GuildBehavior(it, kord) }.toSet()

    public suspend fun getGuilds(): Flow<Guild> = supplier.guilds.filter { it.id in guildIds }

    override fun withStrategy(strategy: EntitySupplyStrategy<*>): ReadyEvent =
        ReadyEvent(gatewayVersion, guildIds, self, sessionId, kord, strategy.supply(kord))

    override fun toString(): String {
        return "ReadyEvent(gatewayVersion=$gatewayVersion, guildIds=$guildIds, self=$self, sessionId='$sessionId', kord=$kord, supplier=$supplier)"
    }
}

public class ResumedEvent(
    override val kord: Kord,
    public val coroutineScope: CoroutineScope = kordCoroutineScope(kord)
) : GatewayEvent(), CoroutineScope by coroutineScope {
    override fun toString(): String {
        return "ResumedEvent(kord=$kord)"
    }
}
