package dev.jombi.kordsb.core.behavior

import dev.kord.cache.api.query
import dev.jombi.kordsb.common.annotation.DeprecatedSinceKord
import dev.jombi.kordsb.common.annotation.KordExperimental
import dev.jombi.kordsb.common.entity.DiscordUser
import dev.jombi.kordsb.common.entity.GuildScheduledEventPrivacyLevel
import dev.jombi.kordsb.common.entity.ScheduledEntityType
import dev.jombi.kordsb.common.entity.Snowflake
import dev.jombi.kordsb.common.entity.optional.Optional
import dev.jombi.kordsb.common.entity.optional.unwrap
import dev.jombi.kordsb.common.exception.RequestException
import dev.jombi.kordsb.core.Kord
import dev.jombi.kordsb.core.cache.data.*
import dev.jombi.kordsb.core.cache.idEq
import dev.jombi.kordsb.core.catchDiscordError
import dev.jombi.kordsb.core.entity.*
import dev.jombi.kordsb.core.entity.application.GuildApplicationCommand
import dev.jombi.kordsb.core.entity.application.GuildChatInputCommand
import dev.jombi.kordsb.core.entity.application.GuildMessageCommand
import dev.jombi.kordsb.core.entity.application.GuildUserCommand
import dev.jombi.kordsb.core.entity.channel.*
import dev.jombi.kordsb.core.entity.channel.thread.ThreadChannel
import dev.jombi.kordsb.core.event.guild.MembersChunkEvent
import dev.jombi.kordsb.core.exception.EntityNotFoundException
import dev.jombi.kordsb.core.supplier.*
import dev.jombi.kordsb.core.supplier.EntitySupplyStrategy.Companion.rest
import dev.jombi.kordsb.gateway.Gateway
import dev.jombi.kordsb.gateway.PrivilegedIntent
import dev.jombi.kordsb.gateway.RequestGuildMembers
import dev.jombi.kordsb.gateway.builder.RequestGuildMembersBuilder
import dev.jombi.kordsb.gateway.start
import dev.jombi.kordsb.rest.Image
import dev.jombi.kordsb.rest.NamedFile
import dev.jombi.kordsb.rest.builder.auditlog.AuditLogGetRequestBuilder
import dev.jombi.kordsb.rest.builder.ban.BanCreateBuilder
import dev.jombi.kordsb.rest.builder.channel.*
import dev.jombi.kordsb.rest.builder.guild.*
import dev.jombi.kordsb.rest.builder.interaction.*
import dev.jombi.kordsb.rest.builder.role.RoleCreateBuilder
import dev.jombi.kordsb.rest.builder.role.RolePositionsModifyBuilder
import dev.jombi.kordsb.rest.json.JsonErrorCode
import dev.jombi.kordsb.rest.json.request.CurrentUserNicknameModifyRequest
import dev.jombi.kordsb.rest.json.request.GuildStickerCreateRequest
import dev.jombi.kordsb.rest.json.request.MultipartGuildStickerCreateRequest
import dev.jombi.kordsb.rest.request.RestRequestException
import dev.jombi.kordsb.rest.service.*
import kotlinx.coroutines.flow.*
import kotlinx.datetime.Instant
import java.util.*
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

/**
 * The behavior of a [Discord Guild](https://discord.com/developers/docs/resources/guild).
 */
public interface GuildBehavior : KordEntity, Strategizable {
    /**
     * Requests to get all present bans for this guild.
     *
     * The returned flow is lazily executed, any [RequestException] will be thrown on
     * [terminal operators](https://kotlinlang.org/docs/reference/coroutines/flow.html#terminal-flow-operators) instead.
     */
    public val bans: Flow<Ban>
        get() = supplier.getGuildBans(id)

    /**
     * Returns all active public and private threads in this guild
     * Threads are ordered by their id, in descending order.
     *
     *  The returned flow is lazily executed, any [RequestException] will be thrown on
     * [terminal operators](https://kotlinlang.org/docs/reference/coroutines/flow.html#terminal-flow-operators) instead.

     */
    public val activeThreads: Flow<ThreadChannel>
        get() = supplier.getActiveThreads(id)

    /**
     * Requests to get all present webhooks for this guild.
     *
     * The returned flow is lazily executed, any [RequestException] will be thrown on
     * [terminal operators](https://kotlinlang.org/docs/reference/coroutines/flow.html#terminal-flow-operators) instead.
     */
    public val webhooks: Flow<Webhook>
        get() = supplier.getGuildWebhooks(id)

    /**
     * Requests to get all present channels in this guild in an unspecified order,
     * call [toList()][toList].[sorted()][sorted] on the returned [Flow] to get a consistent order.
     *
     * The returned flow is lazily executed, any [RequestException] will be thrown on
     * [terminal operators](https://kotlinlang.org/docs/reference/coroutines/flow.html#terminal-flow-operators) instead.
     */
    public val channels: Flow<TopGuildChannel>
        get() = supplier.getGuildChannels(id)

    /**
     * Requests to get all custom emojis in this guild in an unspecified order.
     *
     * The returned flow is lazily executed, any [RequestException] will be thrown on
     * [terminal operators](https://kotlinlang.org/docs/reference/coroutines/flow.html#terminal-flow-operators) instead.
     */
    public val emojis: Flow<GuildEmoji>
        get() = supplier.getEmojis(id)

    /**
     * Requests to get the integrations of this guild.
     */
    public val integrations: Flow<Integration>
        get() = flow {
            kord.rest.guild.getGuildIntegrations(id).forEach {
                emit(Integration(IntegrationData.from(id, it), kord, supplier))
            }
        }

    /**
     * Requests to get all present presences of this guild.
     *
     * This property is not resolvable through REST and will always use [Kord.cache] instead.
     */
    public val presences: Flow<Presence>
        get() = kord.cache.query<PresenceData> { idEq(PresenceData::guildId, id) }
            .asFlow()
            .map { Presence(it, kord) }

    /**
     * Requests to get all present members in this guild.
     *
     * Unrestricted consumption of the returned [Flow] is a potentially performance-intensive operation, it is thus
     * recommended limiting the amount of messages requested by using [Flow.take], [Flow.takeWhile] or other functions
     * that limit the amount of messages requested.
     *
     * ```kotlin
     *  guild.members.first { it.displayName == targetName }
     * ```
     *
     * The returned flow is lazily executed, any [RequestException] will be thrown on
     * [terminal operators](https://kotlinlang.org/docs/reference/coroutines/flow.html#terminal-flow-operators) instead.
     */
    public val members: Flow<Member>
        get() = supplier.getGuildMembers(id)

    public val stickers: Flow<GuildSticker>
        get() = supplier.getGuildStickers(id)

    /**
     * Requests to get the present voice regions for this guild.
     *
     * The returned flow is lazily executed, any [RequestException] will be thrown on
     * [terminal operators](https://kotlinlang.org/docs/reference/coroutines/flow.html#terminal-flow-operators) instead.
     */
    public val regions: Flow<Region>
        get() = supplier.getGuildVoiceRegions(id)

    /**
     * Requests to get all present roles in the guild.
     *
     * The returned flow is lazily executed, any [RequestException] will be thrown on
     * [terminal operators](https://kotlinlang.org/docs/reference/coroutines/flow.html#terminal-flow-operators) instead.
     */
    public val roles: Flow<Role>
        get() = supplier.getGuildRoles(id)

    /**
     * Requests to get the present voice states of this guild.
     *
     * This property is not resolvable through REST and will always use [Kord.cache] instead.
     *
     * The returned flow is lazily executed, any [RequestException] will be thrown on
     * [terminal operators](https://kotlinlang.org/docs/reference/coroutines/flow.html#terminal-flow-operators) instead.
     */
    public val voiceStates: Flow<VoiceState>
        get() = kord.cache
            .query<VoiceStateData> { idEq(VoiceStateData::guildId, id) }
            .asFlow()
            .map { VoiceState(it, kord) }

    /**
     * Requests to get the [invites][InviteWithMetadata] for this guild.
     *
     * This property is not resolvable through cache and will always use the [RestClient] instead.
     *
     * The returned flow is lazily executed, any [RequestException] will be thrown on
     * [terminal operators](https://kotlinlang.org/docs/reference/coroutines/flow.html#terminal-flow-operators) instead.
     */
    public val invites: Flow<InviteWithMetadata>
        get() = flow {
            kord.rest.guild.getGuildInvites(id).forEach {
                val data = InviteWithMetadataData.from(it)
                emit(InviteWithMetadata(data, kord))
            }
        }

    public val templates: Flow<Template>
        get() = supplier.getTemplates(id)

    /**
     * Requests to get this behavior as a [Guild].
     *
     * @throws [RequestException] if anything went wrong during the request.
     * @throws [EntityNotFoundException] if the guild wasn't present.
     */
    public suspend fun asGuild(): Guild = supplier.getGuild(id)

    /**
     * Requests to get this behavior as a [Guild],
     * returns null if the guild isn't present.
     *
     * @throws [RequestException] if anything went wrong during the request.
     */
    public suspend fun asGuildOrNull(): Guild? = supplier.getGuildOrNull(id)

    /**
     * Retrieve the [Guild] associated with this behaviour from the provided [EntitySupplier]
     *
     * @throws [RequestException] if anything went wrong during the request.
     * @throws [EntityNotFoundException] if the user wasn't present.
     */
    public suspend fun fetchGuild(): Guild = supplier.getGuild(id)


    /**
     * Retrieve the [Guild] associated with this behaviour from the provided [EntitySupplier]
     * returns null if the [Guild] isn't present.
     *
     * @throws [RequestException] if anything went wrong during the request.
     */
    public suspend fun fetchGuildOrNull(): Guild? = supplier.getGuildOrNull(id)

    /**
     * Requests to delete this guild.
     *
     * @throws [RestRequestException] if something went wrong during the request.
     */
    public suspend fun delete(): Unit = kord.rest.guild.deleteGuild(id)

    /**
     * Requests to leave this guild.
     *
     * @throws [RestRequestException] if something went wrong during the request.
     */
    public suspend fun leave(): Unit = kord.rest.user.leaveGuild(id)

    /**
     * Requests to get the [Member] represented by the [userId].
     *
     * @throws [RequestException] if anything went wrong during the request.
     * @throws [EntityNotFoundException] if the member wasn't present.
     */
    public suspend fun getMember(userId: Snowflake): Member = supplier.getMember(id, userId)

    /**
     * Requests to get up to [limit] members whose [Member.username] or [Member.nickname] match the [query].
     * The [limit] accepts a maximum value of `1000` and a minimum of `1`.
     *
     * This property is not resolvable through cache and will always use the [RestClient] instead.
     *
     * The returned flow is lazily executed, any [RequestException] will be thrown on
     * [terminal operators](https://kotlinlang.org/docs/reference/coroutines/flow.html#terminal-flow-operators) instead.
     */
    @KordExperimental
    public fun getMembers(query: String, limit: Int = 1000): Flow<Member> = flow {
        kord.rest.guild.getGuildMembers(id, query, limit).forEach {
            emit(
                Member(
                    MemberData.from(userId = it.user.unwrap(DiscordUser::id)!!, guildId = id, it),
                    UserData.from(it.user.value!!),
                    kord
                )
            )
        }
    }

    /**
     * Requests to get the [Member] represented by the [userId],
     * returns null if the [Member] isn't present.
     *
     * @throws [RequestException] if anything went wrong during the request.
     */
    public suspend fun getMemberOrNull(userId: Snowflake): Member? = supplier.getMemberOrNull(id, userId)


    /**
     * Requests to get the [Role] represented by the [roleId].
     *
     * @throws [RequestException] if anything went wrong during the request.
     * @throws [EntityNotFoundException] if the [Role] wasn't present.
     */
    public suspend fun getRole(roleId: Snowflake): Role = supplier.getRole(guildId = id, roleId = roleId)

    /**
     * Requests to get the [Role] represented by the [roleId],
     * returns null if the [Role] isn't present.
     *
     * @throws [RequestException] if anything went wrong during the request.
     */
    public suspend fun getRoleOrNull(roleId: Snowflake): Role? = supplier.getRoleOrNull(guildId = id, roleId = roleId)

    /**
     * Requests to get the [Invite] represented by the [code].
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
    ): Invite = kord.with(rest).getInvite(code, withCounts, withExpiration, scheduledEventId)

    /**
     * Requests to get the [Invite] represented by the [code],
     * returns null if the [Invite] isn't present.
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
    ): Invite? = kord.with(rest).getInviteOrNull(code, withCounts, withExpiration, scheduledEventId)


    /**
     *  Requests to change the nickname of the bot in this guild, passing `null` will remove it.
     *
     * @throws [RestRequestException] if something went wrong during the request.
     */
    @DeprecatedSinceKord("0.7.0")
    @Deprecated("Use editSelfNickname.", ReplaceWith("editSelfNickname(newNickname)"), DeprecationLevel.ERROR)
    public suspend fun modifySelfNickname(newNickname: String? = null): String = editSelfNickname(newNickname)

    /**
     *  Requests to change the nickname of the bot in this guild, passing `null` will remove it.
     *
     * @param reason the reason showing up in the audit log
     * @throws [RestRequestException] if something went wrong during the request.
     */
    public suspend fun editSelfNickname(newNickname: String? = null, reason: String? = null): String {
        return kord.rest.guild.modifyCurrentUserNickname(
            id,
            CurrentUserNicknameModifyRequest(Optional(newNickname)),
            reason
        ).nick
    }

    /**
     * Requests to kick the given [userId].
     *
     * @param reason the reason showing up in the audit log
     * @throws [RestRequestException] if something went wrong during the request.
     */
    public suspend fun kick(userId: Snowflake, reason: String? = null) {
        kord.rest.guild.deleteGuildMember(guildId = id, userId = userId, reason = reason)
    }


    /**
     * Requests to get the [Ban] of the [User] represented by the [userId].
     *
     * @throws [RequestException] if anything went wrong during the request.
     * @throws [EntityNotFoundException] if the [Ban] wasn't present.
     */
    public suspend fun getBan(userId: Snowflake): Ban = supplier.getGuildBan(id, userId)

    /**
     * Requests to get the [Ban] of the [User] represented by the [userId],
     * returns null if the [Ban] isn't present.
     *
     * @throws [RequestException] if anything went wrong during the request.
     */
    public suspend fun getBanOrNull(userId: Snowflake): Ban? = supplier.getGuildBanOrNull(id, userId)

    /**
     * Requests to get the [TopGuildChannel] represented by the [channelId].
     *
     * @throws [RequestException] if anything went wrong during the request.
     * @throws [EntityNotFoundException] if the [TopGuildChannel] wasn't present.
     * @throws [ClassCastException] if the channel is not a [TopGuildChannel].
     * @throws [IllegalArgumentException] if the channel is not part of this guild.
     */
    public suspend fun getChannel(channelId: Snowflake): GuildChannel {
        val channel = supplier.getChannelOf<GuildChannel>(channelId)
        require(channel.guildId == this.id) { "channel ${channelId.value} is not in guild ${this.id}" }
        return channel
    }

    /**
     * Requests to get the [GuildChannel] represented by the [channelId],
     * returns null if the [GuildChannel] isn't present.
     *
     * @throws [RequestException] if anything went wrong during the request.
     * @throws [ClassCastException] if the channel is not a [TopGuildChannel].
     * @throws [IllegalArgumentException] if the channel is not part of this guild.
     */
    public suspend fun getChannelOrNull(channelId: Snowflake): GuildChannel? {
        val channel = supplier.getChannelOfOrNull<GuildChannel>(channelId) ?: return null
        require(channel.guildId == this.id) { "channel ${channelId.value} is not in guild ${this.id}" }
        return channel
    }

    /**
     * Requests to unban the given [userId].
     *
     * @param reason the reason showing up in the audit log
     * @throws [RestRequestException] if something went wrong during the request.
     */
    public suspend fun unban(userId: Snowflake, reason: String? = null) {
        kord.rest.guild.deleteGuildBan(guildId = id, userId = userId, reason = reason)
    }

    /**
     * Returns the preview of this guild. The bot does not need to present in this guild
     * for this to complete successfully.
     *
     * This property is not resolvable through cache and will always use the [RestClient] instead.
     *
     * @throws RequestException if the guild does not exist or is not public.
     * @throws [EntityNotFoundException] if the preview was not found.
     */
    public suspend fun getPreview(): GuildPreview = kord.with(rest).getGuildPreview(id)

    /**
     * Returns the preview of this guild. The bot does not need to present in this guild
     * for this to complete successfully. Returns null if the preview doesn't exist.
     *
     * This property is not resolvable through cache and will always use the [RestClient] instead.
     *
     * @throws RequestException if the guild does not exist or is not public.
     */
    public suspend fun getPreviewOrNull(): GuildPreview? = kord.with(rest).getGuildPreviewOrNull(id)

    /**
     * Requests to get the amount of users that would be pruned in this guild.
     *
     * A user is pruned if they have not been seen within the given [days]
     * and don't have a [Role] assigned in this guild.
     *
     * @throws [RestRequestException] if something went wrong during the request.
     */
    public suspend fun getPruneCount(days: Int = 7): Int =
        kord.rest.guild.getGuildPruneCount(id, days).pruned

    /**
     * Requests to prune users in this guild.
     *
     * A user is pruned if they have not been seen within the given [days]
     * and don't have a [Role] assigned in this guild.
     *
     * @param reason the reason showing up in the audit log
     * @throws [RestRequestException] if something went wrong during the request.
     */
    public suspend fun prune(days: Int = 7, reason: String? = null): Int {
        return kord.rest.guild.beginGuildPrune(id, days, true, reason).pruned!!
    }

    public suspend fun getWelcomeScreenOrNull(): WelcomeScreen? =
        rest.supply(kord).getGuildWelcomeScreenOrNull(id)

    public suspend fun getWelcomeScreen(): WelcomeScreen =
        rest.supply(kord).getGuildWelcomeScreen(id)

    public suspend fun editWelcomeScreen(builder: WelcomeScreenModifyBuilder.() -> Unit): WelcomeScreen {
        val request = kord.rest.guild.modifyGuildWelcomeScreen(id, builder)
        val data = WelcomeScreenData.from(request)
        return WelcomeScreen(data, kord)
    }

    /**
     * Requests to get the vanity url of this guild, if present.
     *
     * This function is not resolvable through cache and will always use the [RestClient] instead.
     * Request exceptions containing the [JsonErrorCode.InviteCodeInvalidOrTaken] reason will be transformed
     * into `null` instead.
     *
     * @throws [RestRequestException] if something went wrong during the request.
     */
    public suspend fun getVanityUrl(): String? {
        val identifier = catchDiscordError(JsonErrorCode.InviteCodeInvalidOrTaken, JsonErrorCode.MissingAccess) {
            kord.rest.guild.getVanityInvite(id).code
        } ?: return null
        return "https://discord.gg/$identifier"
    }


    /**
     * Requests a [GuildScheduledEvent] by its [id].
     *
     * @throws [RequestException] if anything went wrong during the request.
     */
    public suspend fun getGuildScheduledEvent(eventId: Snowflake): GuildScheduledEvent =
        supplier.getGuildScheduledEvent(id, eventId)

    /**
     * Requests a [GuildScheduledEvent] by its [id] returns `null` if none could be found.
     *
     * @throws [RequestException] if anything went wrong during the request.
     */
    public suspend fun getGuildScheduledEventOrNull(eventId: Snowflake): GuildScheduledEvent? =
        supplier.getGuildScheduledEventOrNull(id, eventId)

    public suspend fun getWidget(): GuildWidget = supplier.getGuildWidget(id)

    public suspend fun getWidgetOrNull(): GuildWidget? = supplier.getGuildWidgetOrNull(id)

    public suspend fun getTemplate(code: String): Template = supplier.getTemplate(code)

    public suspend fun getTemplateOrNull(code: String): Template? = supplier.getTemplateOrNull(code)

    public suspend fun getSticker(stickerId: Snowflake): GuildSticker = supplier.getGuildSticker(id, stickerId)

    public suspend fun getStickerOrNull(stickerId: Snowflake): GuildSticker? =
        supplier.getGuildStickerOrNull(id, stickerId)

    public suspend fun createSticker(name: String, description: String, tags: String, file: NamedFile): GuildSticker {
        val request = MultipartGuildStickerCreateRequest(GuildStickerCreateRequest(name, description, tags), file)
        val response = kord.rest.sticker.createGuildSticker(id, request)
        val data = StickerData.from(response)
        return GuildSticker(data, kord)
    }

    /**
     * Returns a new [GuildBehavior] with the given [strategy].
     */
    override fun withStrategy(strategy: EntitySupplyStrategy<*>): GuildBehavior = GuildBehavior(id, kord, strategy)
}

public fun GuildBehavior(
    id: Snowflake,
    kord: Kord,
    strategy: EntitySupplyStrategy<*> = kord.resources.defaultStrategy,
): GuildBehavior = object : GuildBehavior {
    override val id: Snowflake = id
    override val kord: Kord = kord
    override val supplier: EntitySupplier = strategy.supply(kord)

    override fun hashCode(): Int = Objects.hash(id)

    override fun equals(other: Any?): Boolean = when (other) {
        is GuildBehavior -> other.id == id
        else -> false
    }

    override fun toString(): String {
        return "GuildBehavior(id=$id, kord=$kord, supplier=$supplier)"
    }
}

/**
 * Requests to edit this guild.
 *
 * @return The edited [Guild].
 *
 * @throws [RestRequestException] if something went wrong during the request.
 */
public suspend inline fun GuildBehavior.edit(builder: GuildModifyBuilder.() -> Unit): Guild {
    contract {
        callsInPlace(builder, InvocationKind.EXACTLY_ONCE)
    }
    val response = kord.rest.guild.modifyGuild(id, builder)
    val data = GuildData.from(response)

    return Guild(data, kord)
}

@Deprecated(
    "emoji name and image are mandatory fields.",
    ReplaceWith("createEmoji(\"name\", Image.fromUrl(\"url\"), builder)")
)
@DeprecatedSinceKord("0.7.0")
public suspend inline fun GuildBehavior.createEmoji(builder: EmojiCreateBuilder.() -> Unit): GuildEmoji {
    contract {
        callsInPlace(builder, InvocationKind.EXACTLY_ONCE)
    }
    return createEmoji("name", Image.raw(byteArrayOf(), Image.Format.PNG), builder)
}

public suspend inline fun GuildBehavior.createEmoji(
    name: String,
    image: Image,
    builder: EmojiCreateBuilder.() -> Unit = {}
): GuildEmoji {
    contract {
        callsInPlace(builder, InvocationKind.EXACTLY_ONCE)
    }
    val discordEmoji = kord.rest.emoji.createEmoji(guildId = id, name, image, builder)
    return GuildEmoji(EmojiData.from(guildId = id, id = discordEmoji.id!!, discordEmoji), kord)
}

/**
 * Requests to create a new text channel.
 *
 * @return The created [TextChannel].
 *
 * @throws [RestRequestException] if something went wrong during the request.
 */
@Deprecated(
    "channel name is a mandatory field",
    ReplaceWith("createTextChannel(\"name\", builder)"),
    DeprecationLevel.WARNING
)
@DeprecatedSinceKord("0.7.0")
public suspend inline fun GuildBehavior.createTextChannel(builder: TextChannelCreateBuilder.() -> Unit): TextChannel {
    contract {
        callsInPlace(builder, InvocationKind.EXACTLY_ONCE)
    }
    return createTextChannel("name", builder)
}

/**
 * Requests to create a new text channel.
 *
 * @return The created [TextChannel].
 *
 * @throws [RestRequestException] if something went wrong during the request.
 */

public suspend inline fun GuildBehavior.createTextChannel(
    name: String,
    builder: TextChannelCreateBuilder.() -> Unit = {}
): TextChannel {
    contract {
        callsInPlace(builder, InvocationKind.EXACTLY_ONCE)
    }
    val response = kord.rest.guild.createTextChannel(id, name, builder)
    val data = ChannelData.from(response)

    return Channel.from(data, kord) as TextChannel
}

/**
 * Requests to create a new voice channel.
 *
 * @return The created [VoiceChannel].
 *
 * @throws [RestRequestException] if something went wrong during the request.
 */
@Deprecated(
    "channel name is a mandatory field.",
    ReplaceWith("createVoiceChannel(\"name\", builder)"),
    DeprecationLevel.WARNING
)
@DeprecatedSinceKord("0.7.0")
public suspend inline fun GuildBehavior.createVoiceChannel(builder: VoiceChannelCreateBuilder.() -> Unit): VoiceChannel {
    contract {
        callsInPlace(builder, InvocationKind.EXACTLY_ONCE)
    }
    return createVoiceChannel("name", builder)
}

/**
 * Requests to create a new voice channel.
 *
 * @return The created [VoiceChannel].
 *
 * @throws [RestRequestException] if something went wrong during the request.
 */
public suspend inline fun GuildBehavior.createVoiceChannel(
    name: String,
    builder: VoiceChannelCreateBuilder.() -> Unit = {}
): VoiceChannel {
    contract {
        callsInPlace(builder, InvocationKind.EXACTLY_ONCE)
    }
    val response = kord.rest.guild.createVoiceChannel(id, name, builder)
    val data = ChannelData.from(response)

    return Channel.from(data, kord) as VoiceChannel
}

/**
 * Requests to create a new news channel.
 *
 * @return The created [NewsChannel].
 *
 * @throws [RestRequestException] if something went wrong during the request.
 */
@Deprecated(
    "channel name is a mandatory field.",
    ReplaceWith("createNewsChannel(\"name\", builder)"),
    DeprecationLevel.WARNING
)
@DeprecatedSinceKord("0.7.0")
public suspend inline fun GuildBehavior.createNewsChannel(builder: NewsChannelCreateBuilder.() -> Unit): NewsChannel {
    contract {
        callsInPlace(builder, InvocationKind.EXACTLY_ONCE)
    }
    return createNewsChannel("name", builder)
}

/**
 * Requests to create a new news channel.
 *
 * @return The created [NewsChannel].
 *
 * @throws [RestRequestException] if something went wrong during the request.
 */
public suspend inline fun GuildBehavior.createNewsChannel(
    name: String,
    builder: NewsChannelCreateBuilder.() -> Unit = {}
): NewsChannel {
    contract {
        callsInPlace(builder, InvocationKind.EXACTLY_ONCE)
    }
    val response = kord.rest.guild.createNewsChannel(id, name, builder)
    val data = ChannelData.from(response)

    return Channel.from(data, kord) as NewsChannel
}


/**
 * Requests to create a new category.
 *
 * @return The created [Category].
 *
 * @throws [RestRequestException] if something went wrong during the request.
 */
@Deprecated(
    "channel name is a mandatory field.",
    ReplaceWith("createCategoryChannel(\"name\", builder)"),
    DeprecationLevel.WARNING
)
@DeprecatedSinceKord("0.7.0")
public suspend inline fun GuildBehavior.createCategory(builder: CategoryCreateBuilder.() -> Unit): Category {
    contract {
        callsInPlace(builder, InvocationKind.EXACTLY_ONCE)
    }
    return createCategory("name", builder)
}

/**
 * Requests to create a new category.
 *
 * @return The created [Category].
 *
 * @throws [RestRequestException] if something went wrong during the request.
 */
public suspend inline fun GuildBehavior.createCategory(
    name: String,
    builder: CategoryCreateBuilder.() -> Unit = {}
): Category {
    contract {
        callsInPlace(builder, InvocationKind.EXACTLY_ONCE)
    }
    val response = kord.rest.guild.createCategory(id, name, builder)
    val data = ChannelData.from(response)

    return Channel.from(data, kord) as Category
}

/**
 * Requests to swap positions of channels in this guild.
 *
 * @throws [RestRequestException] if something went wrong during the request.
 */
public suspend inline fun GuildBehavior.swapChannelPositions(builder: GuildChannelPositionModifyBuilder.() -> Unit) {
    contract {
        callsInPlace(builder, InvocationKind.EXACTLY_ONCE)
    }
    kord.rest.guild.modifyGuildChannelPosition(id, builder)
}

/**
 * Requests to swap positions of roles in this guild.
 *
 * This request will execute regardless of the consumption of the return value.
 *
 * @return the roles of this guild after the update in an unspecified order, call [toList()][toList].[sorted()][sorted]
 * on the returned [Flow] to get a consistent order.
 *
 * @throws [RestRequestException] if something went wrong during the request.
 */
public suspend inline fun GuildBehavior.swapRolePositions(builder: RolePositionsModifyBuilder.() -> Unit): Flow<Role> {
    contract {
        callsInPlace(builder, InvocationKind.EXACTLY_ONCE)
    }
    val response = kord.rest.guild.modifyGuildRolePosition(id, builder)
    return response.asFlow().map { RoleData.from(id, it) }.map { Role(it, kord) }

}

/**
 * Requests to add a new role to this guild.
 *
 * @return The created [Role].
 *
 * @throws [RestRequestException] if something went wrong during the request.
 */
@DeprecatedSinceKord("0.7.0")
@Deprecated("Use createRole instead.", ReplaceWith("createRole(builder)"), DeprecationLevel.ERROR)
public suspend inline fun GuildBehavior.addRole(builder: RoleCreateBuilder.() -> Unit = {}): Role = createRole(builder)

/**
 * Requests to add a new role to this guild.
 *
 * @return The created [Role].
 *
 * @throws [RestRequestException] if something went wrong during the request.
 */
public suspend inline fun GuildBehavior.createRole(builder: RoleCreateBuilder.() -> Unit = {}): Role {
    contract {
        callsInPlace(builder, InvocationKind.EXACTLY_ONCE)
    }
    val response = kord.rest.guild.createGuildRole(id, builder)
    val data = RoleData.from(id, response)

    return Role(data, kord)
}

/**
 * Requests to ban the given [userId] in this guild.
 *
 * @throws [RestRequestException] if something went wrong during the request.
 */
public suspend inline fun GuildBehavior.ban(userId: Snowflake, builder: BanCreateBuilder.() -> Unit) {
    contract {
        callsInPlace(builder, InvocationKind.EXACTLY_ONCE)
    }
    kord.rest.guild.addGuildBan(guildId = id, userId = userId, builder = builder)
}

/**
 * Requests to get the [GuildChannel] represented by the [channelId] as type [T].
 *
 * @throws [RequestException] if anything went wrong during the request.
 * @throws [EntityNotFoundException] if the [T] wasn't present.
 * @throws [ClassCastException] if the channel is not of type [T].
 * @throws [IllegalArgumentException] if the channel is not part of this guild.
 */
public suspend inline fun <reified T : GuildChannel> GuildBehavior.getChannelOf(channelId: Snowflake): T {
    val channel = supplier.getChannelOf<T>(channelId)
    require(channel.guildId == this.id) { "channel ${channelId.value} is not in guild ${this.id}" }
    return channel
}

/**
 * Requests to get the [GuildChannel] represented by the [channelId] as type [T],
 * returns null if the [GuildChannel] isn't present.
 *
 * @throws [RequestException] if anything went wrong during the request.
 * @throws [ClassCastException] if the channel is not of type [T].
 * @throws [IllegalArgumentException] if the channel is not part of this guild.
 */
public suspend inline fun <reified T : GuildChannel> GuildBehavior.getChannelOfOrNull(channelId: Snowflake): T? {
    val channel = supplier.getChannelOfOrNull<T>(channelId) ?: return null
    require(channel.guildId == this.id) { "channel ${channelId.value} is not in guild ${this.id}" }
    return channel
}

public suspend inline fun GuildBehavior.editWidget(builder: GuildWidgetModifyBuilder.() -> Unit): GuildWidget {
    contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
    return GuildWidget(GuildWidgetData.from(kord.rest.guild.modifyGuildWidget(id, builder)), id, kord)
}

/**
 * The [Audit log entries][AuditLogEntry] from this guild, configured by the [builder].
 *
 * The returned flow is lazily executed, any [RequestException] will be thrown on
 * [terminal operators](https://kotlinlang.org/docs/reference/coroutines/flow.html#terminal-flow-operators) instead.
 *
 * ```kotlin
 *  val change = guild.getAuditLogEntries {
 *      userId = user.id
 *      action = AuditLogEvent.MemberUpdate
 *  }.mapNotNull { it[AuditLogChangeKey.Nick] }.firstOrNull() ?: return
 *
 *  println("user changed nickname from $old to $new")
 *  ```
 */
public inline fun GuildBehavior.getAuditLogEntries(builder: AuditLogGetRequestBuilder.() -> Unit = {}): Flow<AuditLogEntry> {
    contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
    return kord.with(rest).getAuditLogEntries(id, builder).map { AuditLogEntry(it, kord) }
}

/**
 * Creates a new [GuildScheduledEvent].
 */
public suspend fun GuildBehavior.createScheduledEvent(
    name: String,
    privacyLevel: GuildScheduledEventPrivacyLevel,
    scheduledStartTime: Instant,
    entityType: ScheduledEntityType,
    builder: ScheduledEventCreateBuilder.() -> Unit
): GuildScheduledEvent {
    contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }

    val event = kord.rest.guild.createScheduledEvent(id, name, privacyLevel, scheduledStartTime, entityType, builder)
    val data = GuildScheduledEventData.from(event)

    return GuildScheduledEvent(data, kord, supplier)
}
