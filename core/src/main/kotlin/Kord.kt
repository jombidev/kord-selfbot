package dev.jombi.kordsb.core

import dev.kord.cache.api.DataCache
import dev.jombi.kordsb.common.annotation.DeprecatedSinceKord
import dev.jombi.kordsb.common.annotation.KordExperimental
import dev.jombi.kordsb.common.annotation.KordUnsafe
import dev.jombi.kordsb.common.entity.Snowflake
import dev.jombi.kordsb.common.exception.RequestException
import dev.jombi.kordsb.core.builder.kord.KordBuilder
import dev.jombi.kordsb.core.builder.kord.KordRestOnlyBuilder
import dev.jombi.kordsb.core.cache.data.GuildData
import dev.jombi.kordsb.core.cache.data.UserData
import dev.jombi.kordsb.core.entity.*
import dev.jombi.kordsb.core.entity.channel.Channel
import dev.jombi.kordsb.core.event.Event
import dev.jombi.kordsb.core.exception.EntityNotFoundException
import dev.jombi.kordsb.core.exception.KordInitializationException
import dev.jombi.kordsb.core.gateway.MasterGateway
import dev.jombi.kordsb.core.gateway.handler.DefaultGatewayEventInterceptor
import dev.jombi.kordsb.core.gateway.handler.GatewayEventInterceptor
import dev.jombi.kordsb.core.gateway.start
import dev.jombi.kordsb.core.supplier.*
import dev.jombi.kordsb.gateway.Gateway
import dev.jombi.kordsb.gateway.builder.LoginBuilder
import dev.jombi.kordsb.gateway.builder.PresenceBuilder
import dev.jombi.kordsb.rest.builder.guild.GuildCreateBuilder
import dev.jombi.kordsb.rest.builder.user.CurrentUserModifyBuilder
import dev.jombi.kordsb.rest.request.RestRequestException
import dev.jombi.kordsb.rest.service.RestClient
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import mu.KLogger
import mu.KotlinLogging
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract
import kotlin.coroutines.CoroutineContext
import kotlinx.coroutines.channels.Channel as CoroutineChannel

public val kordLogger: KLogger = KotlinLogging.logger { }


/**
 * The central adapter between other Kord modules and source of core [events].
 */
public class Kord(
    public val resources: ClientResources,
    public val cache: DataCache,
    public val gateway: MasterGateway,
    public val rest: RestClient,
    public val selfId: Snowflake,
    private val eventFlow: MutableSharedFlow<Event>,
    dispatcher: CoroutineDispatcher,
    interceptorBuilder: () -> GatewayEventInterceptor = {
        DefaultGatewayEventInterceptor(cache)
    }
) : CoroutineScope {

    private val interceptor = interceptorBuilder.invoke()

    public val nitroStickerPacks: Flow<StickerPack>
        get() = defaultSupplier.getNitroStickerPacks()


    /**
     * The default supplier, obtained through Kord's [resources] and configured through [KordBuilder.defaultStrategy].
     * By default a strategy from [EntitySupplyStrategy.rest].
     *
     * All [strategizable][Strategizable] [entities][KordEntity] created through this instance will use this supplier by default.
     */
    public val defaultSupplier: EntitySupplier = resources.defaultStrategy.supply(this)

    /**
     * A reference to all exposed [unsafe][KordUnsafe] entity constructors for this instance.
     */
    @OptIn(KordUnsafe::class, KordExperimental::class)
    public val unsafe: Unsafe = Unsafe(this)

    /**
     * The events emitted from the [gateway]. Call [Kord.login] to start receiving events.
     *
     * Events emitted by this flow are guaranteed to follow the same order as they were received
     * by the web socket. Any event flow order between multiple [MasterGateway.gateway] is not
     * guaranteed.
     *
     * Subscriptions to this flow will not complete normally, as per design of [SharedFlow].
     * Use [Flow.launchIn] with [Kord] as [CoroutineScope] to cease event processing
     * on [Kord.shutdown].
     *
     * Behavior like replay cache size, buffer size and overflow behavior are dependant on the
     * supplied [eventFlow]. See [KordBuilder.eventFlow] for more details.
     */
    public val events: SharedFlow<Event>
        get() = eventFlow

    override val coroutineContext: CoroutineContext = SupervisorJob() + dispatcher

    public val regions: Flow<Region>
        get() = defaultSupplier.regions

    public val guilds: Flow<Guild>
        get() = defaultSupplier.guilds

    init {
        gateway.events
            .buffer(kotlinx.coroutines.channels.Channel.UNLIMITED)
            .onEach { event ->
                val coreEvent = interceptor.handle(event, this)
                coreEvent?.let { eventFlow.emit(it) }
            }
            .launchIn(this)
    }

    /**
     * Logs in to the configured [Gateways][Gateway]. Suspends until [logout] or [shutdown] is called.
     */
    public suspend inline fun login(builder: LoginBuilder.() -> Unit = {}) {
        contract {
            callsInPlace(builder, InvocationKind.EXACTLY_ONCE)
        }
        val loginBuilder = LoginBuilder().apply(builder)
        gateway.start(resources.token) {
            //shard = DiscordShard(0, resources.shards.totalShards)
            presence = loginBuilder.presence
            intents = dev.jombi.kordsb.gateway.Intents(dev.jombi.kordsb.gateway.Intent.values)
            name = loginBuilder.name
        }
    }

    /**
     * Logs out to the configured [Gateways][Gateway].
     */
    public suspend fun logout(): Unit = gateway.gateway.stop()

    /**
     * Logs out of all connected [Gateways][Gateway] and frees all resources.
     */
    public suspend fun shutdown() {
        gateway.gateway.detach()

        // resolve ambiguous coroutineContext
        (this as CoroutineScope).cancel()
    }

    public fun <T : EntitySupplier> with(strategy: EntitySupplyStrategy<T>): T = strategy.supply(this)

    public suspend fun getApplicationInfo(): Application = with(EntitySupplyStrategy.rest).getApplicationInfo()

    /**
     * Requests to create a new Guild configured through the [builder].
     * At least the [GuildCreateBuilder.name] has to be set.
     *
     * @throws [RequestException] if anything went wrong during the request.
     * @return The newly created Guild.
     */
    @DeprecatedSinceKord("0.7.0")
    @Deprecated(
        "guild name is a mandatory field",
        ReplaceWith("createGuild(\"name\", builder)"),
        DeprecationLevel.WARNING
    )
    public suspend inline fun createGuild(builder: GuildCreateBuilder.() -> Unit): Guild {
        contract {
            callsInPlace(builder, InvocationKind.EXACTLY_ONCE)
        }
        return createGuild("name", builder)
    }

    /**
     * Requests to create a new Guild configured through the [builder].
     * At least the [GuildCreateBuilder.name] has to be set.
     *
     * @throws [RequestException] if anything went wrong during the request.
     * @return The newly created Guild.
     */
    public suspend inline fun createGuild(name: String, builder: GuildCreateBuilder.() -> Unit): Guild {
        contract {
            callsInPlace(builder, InvocationKind.EXACTLY_ONCE)
        }
        val response = rest.guild.createGuild(name, builder)
        val data = GuildData.from(response)

        return Guild(data, this)
    }

    /**
     * Requests to get the [GuildPreview] of a guild with the [guildId] through the [strategy],
     * returns null if the [GuildPreview] isn't present.
     *
     * @throws [RequestException] if anything went wrong during the request.
     * @throws [EntityNotFoundException] if the preview wasn't present.
     */
    public suspend fun getGuildPreview(
        guildId: Snowflake,
        strategy: EntitySupplyStrategy<*> = resources.defaultStrategy,
    ): GuildPreview = strategy.supply(this).getGuildPreview(guildId)

    /**
     * Requests to get the [GuildPreview] of a guild with the [guildId] through the [strategy],
     * returns null if the [GuildPreview] isn't present.
     *
     * @throws [RequestException] if anything went wrong during the request.
     */
    public suspend fun getGuildPreviewOrNull(
        guildId: Snowflake,
        strategy: EntitySupplyStrategy<*> = resources.defaultStrategy,
    ): GuildPreview? = strategy.supply(this).getGuildPreviewOrNull(guildId)


    /**
     * Requests to get the [Channel] with the [id] through the [strategy],
     * returns null if the [Channel] isn't present.
     *
     * @throws [RequestException] if anything went wrong during the request.
     * @throws [EntityNotFoundException] if the channel wasn't present.
     */
    public suspend fun getChannel(
        id: Snowflake,
        strategy: EntitySupplyStrategy<*> =
            resources.defaultStrategy,
    ): Channel? = strategy.supply(this).getChannelOrNull(id)

    /**
     * Requests to get the [Channel] as type [T] through the [strategy],
     * returns null if the [Channel] isn't present or is not of type [T].
     *
     * @throws [RequestException] if anything went wrong during the request.
     */
    public suspend inline fun <reified T : Channel> getChannelOf(
        id: Snowflake,
        strategy: EntitySupplyStrategy<*> = resources.defaultStrategy,
    ): T? = strategy.supply(this).getChannelOfOrNull(id)

    public suspend fun getGuild(
        id: Snowflake,
        strategy: EntitySupplyStrategy<*> =
            resources.defaultStrategy,
    ): Guild? = strategy.supply(this).getGuildOrNull(id)

    /**
     * Requests to get the [Webhook] in this guild.
     *
     * @throws [RestRequestException] if something went wrong during the request.
     * @throws [EntityNotFoundException] if the webhook was not present.
     */
    public suspend fun getWebhook(
        id: Snowflake,
        strategy: EntitySupplyStrategy<*> = resources.defaultStrategy
    ): Webhook = strategy.supply(this).getWebhook(id)

    /**
     * Requests to get the [Webhook] in this guild with an authentication token,
     * returns null if the webhook was not present.
     *
     * @throws [RestRequestException] if something went wrong during the request.
     */

    public suspend fun getWebhookOrNull(
        id: Snowflake,
        strategy: EntitySupplyStrategy<*> = resources.defaultStrategy
    ): Webhook? = strategy.supply(this).getWebhookOrNull(id)

    /**
     * Requests to get the [Webhook] in this guild with an authentication token.
     *
     * @throws [RestRequestException] if something went wrong during the request.
     * @throws [EntityNotFoundException] if the webhook was not present.
     */
    public suspend fun getWebhookWithToken(
        id: Snowflake,
        token: String,
        strategy: EntitySupplyStrategy<*> = resources.defaultStrategy
    ): Webhook = strategy.supply(this).getWebhookWithToken(id, token)

    /**
     * Requests to get the [Webhook] in this guild with an authentication token,
     * returns null if the webhook was not present.
     *
     * @throws [RestRequestException] if something went wrong during the request.
     */

    public suspend fun getWebhookWithTokenOrNull(
        id: Snowflake,
        token: String,
        strategy: EntitySupplyStrategy<*> = resources.defaultStrategy
    ): Webhook? =
        strategy.supply(this).getWebhookWithTokenOrNull(id, token)


    /**
     * Requests to get the [User] that represents this bot account through the [strategy],
     *
     * @throws [RequestException] if anything went wrong during the request.
     * @throws [EntityNotFoundException] if the user wasn't present.
     */
    public suspend fun getSelf(strategy: EntitySupplyStrategy<*> = resources.defaultStrategy): User =
        strategy.supply(this).getSelf()

    public suspend fun editSelf(builder: CurrentUserModifyBuilder.() -> Unit): User {
        contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
        return User(UserData.from(rest.user.modifyCurrentUser(builder)), this)
    }

    /**
     * Requests to get the [User] that with the [id] through the [strategy],
     *
     * @throws [RequestException] if anything went wrong during the request.
     * @throws [EntityNotFoundException] if the user wasn't present.
     */
    public suspend fun getUser(id: Snowflake, strategy: EntitySupplyStrategy<*> = resources.defaultStrategy): User? =
        strategy.supply(this).getUserOrNull(id)

    /**
     * Requests to get the [Invite] represented by the [code].
     *
     * The returned [Invite], if found, uses the default strategy used by Kord.
     *
     * This is not resolvable through cache and will always use the [rest strategy][EntitySupplyStrategy.rest] instead.
     *
     * @throws RestRequestException if anything went wrong during the request.
     * @throws EntityNotFoundException if the [Invite] wasn't present.
     */
    public suspend fun getInvite(
        code: String,
        withCounts: Boolean = true,
        withExpiration: Boolean = true,
        scheduledEventId: Snowflake? = null,
    ): Invite = with(EntitySupplyStrategy.rest).getInvite(code, withCounts, withExpiration, scheduledEventId)

    /**
     * Requests to get the [Invite] represented by the [code],
     * returns null if the [Invite] isn't present.
     *
     * The returned [Invite], if found, uses the default strategy used by Kord.
     *
     * This is not resolvable through cache and will always use the [rest strategy][EntitySupplyStrategy.rest] instead.
     *
     * @throws RestRequestException if anything went wrong during the request.
     */
    public suspend fun getInviteOrNull(
        code: String,
        withCounts: Boolean = true,
        withExpiration: Boolean = true,
        scheduledEventId: Snowflake? = null,
    ): Invite? = with(EntitySupplyStrategy.rest).getInviteOrNull(code, withCounts, withExpiration, scheduledEventId)


    public suspend fun getSticker(id: Snowflake): Sticker = defaultSupplier.getSticker(id)



    /**
     * Requests to edit the presence of the bot user configured by the [builder].
     * The new presence will be shown on all shards. Use [MasterGateway.gateway] or [Event.gateway] to
     * set the presence of a single shard.
     *
     * @throws [RequestException] if anything went wrong during the request.
     */
    public suspend inline fun editPresence(builder: PresenceBuilder.() -> Unit) {
        contract {
            callsInPlace(builder, InvocationKind.EXACTLY_ONCE)
        }
        val status = PresenceBuilder().apply(builder).toUpdateStatus()
        gateway.gateway.send(status)
    }

    override fun equals(other: Any?): Boolean {
        val kord = other as? Kord ?: return false

        return resources.token == kord.resources.token
    }

    override fun toString(): String {
        return "Kord(resources=$resources, cache=$cache, gateway=$gateway, rest=$rest, selfId=$selfId)"
    }

    public companion object {

        /**
         * Builds a [Kord] instance configured by the [builder].
         *
         * The instance only allows for configuration of REST related APIs,
         * interacting with the [gateway][Kord.gateway] or its [events][Kord.events] will result in no-ops.
         *
         * Similarly, [cache][Kord.cache] related functionality has been disabled and
         * replaced with a no-op implementation.
         */
        @KordExperimental
        public inline fun restOnly(token: String, builder: KordRestOnlyBuilder.() -> Unit = {}): Kord {
            contract {
                callsInPlace(builder, InvocationKind.EXACTLY_ONCE)
            }
            return KordRestOnlyBuilder(token).apply(builder).build()
        }
    }
}

/**
 * Builds a [Kord] instance configured by the [builder].
 *
 * @throws KordInitializationException if something went wrong while getting the bot's gateway information.
 */
public suspend inline fun Kord(token: String, builder: KordBuilder.() -> Unit = {}): Kord {
    contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
    KORD_TEMP_TOKEN_STORE = token
    return KordBuilder(token).apply(builder).build()
}

public var KORD_TEMP_TOKEN_STORE: String = ""

/**
 * Convenience method that will invoke the [consumer] on every event [T] created by [Kord.events].
 *
 * The events are buffered in an [unlimited][CoroutineChannel.UNLIMITED] [buffer][Flow.buffer] and
 * [launched][CoroutineScope.launch] in the supplied [scope], which is [Kord] by default.
 * Each event will be [launched][CoroutineScope.launch] inside the [scope] separately and
 * any thrown [Throwable] will be caught and logged.
 *
 * The returned [Job] is a reference to the created coroutine, call [Job.cancel] to cancel the processing of any further
 * events for this [consumer].
 */
public inline fun <reified T : Event> Kord.on(
    scope: CoroutineScope = this,
    noinline consumer: suspend T.() -> Unit
): Job =
    events.buffer(CoroutineChannel.UNLIMITED).filterIsInstance<T>()
        .onEach { event ->
            scope.launch(event.coroutineContext) { runCatching { consumer(event) }.onFailure { kordLogger.catching(it) } }
        }
        .launchIn(scope)
