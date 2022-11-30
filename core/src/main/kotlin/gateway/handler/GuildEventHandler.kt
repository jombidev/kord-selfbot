package dev.jombi.kordsb.core.gateway.handler

import dev.jombi.kordsb.common.entity.optional.optionalSnowflake
import dev.jombi.kordsb.common.entity.optional.orEmpty
import dev.jombi.kordsb.core.Kord
import dev.jombi.kordsb.core.cache.data.*
import dev.jombi.kordsb.core.cache.idEq
import dev.jombi.kordsb.core.entity.*
import dev.jombi.kordsb.core.event.guild.*
import dev.jombi.kordsb.core.event.role.RoleCreateEvent
import dev.jombi.kordsb.core.event.role.RoleDeleteEvent
import dev.jombi.kordsb.core.event.role.RoleUpdateEvent
import dev.jombi.kordsb.core.event.user.PresenceUpdateEvent
import dev.jombi.kordsb.gateway.*
import dev.kord.cache.api.DataCache
import dev.kord.cache.api.put
import dev.kord.cache.api.putAll
import dev.kord.cache.api.query
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.singleOrNull
import kotlinx.coroutines.flow.toSet
import dev.jombi.kordsb.common.entity.DiscordGuild as GatewayGuild
import dev.jombi.kordsb.core.event.Event as CoreEvent

internal class GuildEventHandler : BaseGatewayEventHandler() {

    override suspend fun handle(event: Event, kord: Kord, context: LazyContext?): CoreEvent? =
        when (event) {
            is GuildCreate -> handle(event, kord, context)
            is GuildUpdate -> handle(event, kord, context)
            is GuildDelete -> handle(event, kord, context)
            is GuildBanAdd -> handle(event, kord, context)
            is GuildBanRemove -> handle(event, kord, context)
            is GuildEmojisUpdate -> handle(event, kord, context)
            is GuildIntegrationsUpdate -> handle(event, kord, context)
            is GuildMemberAdd -> handle(event, kord, context)
            is GuildMemberRemove -> handle(event, kord, context)
            is GuildMemberUpdate -> handle(event, kord, context)
            is GuildRoleCreate -> handle(event, kord, context)
            is GuildRoleUpdate -> handle(event, kord, context)
            is GuildRoleDelete -> handle(event, kord, context)
            is GuildMembersChunk -> handle(event, kord, context)
            is GuildScheduledEventCreate -> handle(event, kord, context)
            is GuildScheduledEventUpdate -> handle(event, kord, context)
            is GuildScheduledEventDelete -> handle(event, kord, context)
            is GuildScheduledEventUserAdd -> handle(event, kord, context)
            is GuildScheduledEventUserRemove -> handle(event, kord, context)
            is PresenceUpdate -> handle(event, kord, context)
            is InviteCreate -> handle(event, kord, context)
            is InviteDelete -> handle(event, kord, context)
            else -> null
        }

    private suspend fun GatewayGuild.cache(cache: DataCache) {
        for (member in members.orEmpty()) {
            cache.put(MemberData.from(member.user.value!!.id, id, member))
            cache.put(UserData.from(member.user.value!!))
        }

        for (role in roles) {
            cache.put(RoleData.from(id, role))
        }

        for (channel in channels.orEmpty()) {
            cache.put(ChannelData.from(channel.copy(guildId = this.id.optionalSnowflake()))) //guild id always empty
        }

        for (thread in threads.orEmpty()) {
            cache.put(ChannelData.from(thread.copy(guildId = this.id.optionalSnowflake()))) //guild id always empty
        }

        for (presence in presences.orEmpty()) {
            cache.put(PresenceData.from(id, presence))
        }

        for (voiceState in voiceStates.orEmpty()) {
            cache.put(VoiceStateData.from(id, voiceState))
        }

        for (emoji in emojis) {
            cache.put(EmojiData.from(id, emoji.id!!, emoji))
        }
    }

    private suspend fun handle(
        event: GuildCreate,
        kord: Kord,
        context: LazyContext?,
    ): GuildCreateEvent {
        val data = GuildData.from(event.guild)
        kord.cache.put(data)
        event.guild.cache(kord.cache)

        return GuildCreateEvent(Guild(data, kord), context?.get())
    }

    private suspend fun handle(
        event: GuildUpdate,
        kord: Kord,
        context: LazyContext?,
    ): GuildUpdateEvent {
        val data = GuildData.from(event.guild)
        val old = kord.cache.query<GuildData> { idEq(GuildData::id, event.guild.id) }.singleOrNull()
        kord.cache.put(data)
        event.guild.cache(kord.cache)

        return GuildUpdateEvent(Guild(data, kord), old?.let { Guild(it, kord) }, context?.get())
    }

    private suspend fun handle(
        event: GuildDelete,
        kord: Kord,
        context: LazyContext?,
    ): GuildDeleteEvent = with(event.guild) {
        val query = kord.cache.query<GuildData> { idEq(GuildData::id, id) }

        val old = query.asFlow().map { Guild(it, kord) }.singleOrNull()
        query.remove()

        return GuildDeleteEvent(id, unavailable.orElse(false), old, kord, context?.get())
    }

    private suspend fun handle(
        event: GuildBanAdd,
        kord: Kord,
        context: LazyContext?,
    ): BanAddEvent =
        with(event.ban) {
            val data = UserData.from(user)
            kord.cache.put(user)
            val user = User(data, kord)

            return BanAddEvent(user, guildId, context?.get())
        }

    private suspend fun handle(
        event: GuildBanRemove,
        kord: Kord,
        context: LazyContext?,
    ): BanRemoveEvent = with(event.ban) {
        val data = UserData.from(user)
        kord.cache.put(user)
        val user = User(data, kord)

        return BanRemoveEvent(user, guildId, context?.get())
    }

    private suspend fun handle(
        event: GuildEmojisUpdate,
        kord: Kord,
        context: LazyContext?,
    ): EmojisUpdateEvent =
        with(event.emoji) {

            val data = emojis.map { EmojiData.from(guildId, it.id!!, it) }
            val old = kord.cache.query<EmojiData> { idEq(EmojiData::guildId, guildId) }.asFlow().map {
                GuildEmoji(it, kord)
            }.toSet()
            kord.cache.putAll(data)

            val emojis = data.map { GuildEmoji(it, kord) }

            kord.cache.query<GuildData> { idEq(GuildData::id, guildId) }.update {
                it.copy(emojis = emojis.map { emoji -> emoji.id })
            }

            return EmojisUpdateEvent(guildId, emojis.toSet(), old, kord, context?.get())
        }


    private suspend fun handle(
        event: GuildIntegrationsUpdate,
        kord: Kord,
        context: LazyContext?,
    ): IntegrationsUpdateEvent {
        return IntegrationsUpdateEvent(event.integrations.guildId, kord, context?.get())
    }

    private suspend fun handle(
        event: GuildMemberAdd,
        kord: Kord,
        context: LazyContext?,
    ): MemberJoinEvent = with(event.member) {
        val userData = UserData.from(user.value!!)
        val memberData = MemberData.from(user.value!!.id, event.member)

        kord.cache.put(userData)
        kord.cache.put(memberData)

        val member = Member(memberData, userData, kord)

        return MemberJoinEvent(member, context?.get())
    }

    private suspend fun handle(
        event: GuildMemberRemove,
        kord: Kord,
        context: LazyContext?,
    ): MemberLeaveEvent =
        with(event.member) {
            val userData = UserData.from(user)

            val oldData = kord.cache.query<MemberData> {
                idEq(MemberData::userId, userData.id)
                idEq(MemberData::guildId, guildId)
            }.singleOrNull()

            kord.cache.query<UserData> { idEq(UserData::id, userData.id) }.remove()

            val user = User(userData, kord)
            val old = oldData?.let { Member(it, userData, kord) }

            return MemberLeaveEvent(user, old, guildId, context?.get())
        }

    private suspend fun handle(
        event: GuildMemberUpdate,
        kord: Kord,
        context: LazyContext?,
    ): MemberUpdateEvent =
        with(event.member) {
            val userData = UserData.from(user)
            kord.cache.put(userData)

            val query = kord.cache.query<MemberData> {
                idEq(MemberData::userId, userData.id)
                idEq(MemberData::guildId, guildId)
            }
            val old = query.asFlow().map { Member(it, userData, kord) }.singleOrNull()

            val new = Member(MemberData.from(this), userData, kord)
            kord.cache.put(new.memberData)

            return MemberUpdateEvent(new, old, kord, context?.get())
        }

    private suspend fun handle(
        event: GuildRoleCreate,
        kord: Kord,
        context: LazyContext?,
    ): RoleCreateEvent {
        val data = RoleData.from(event.role)
        kord.cache.put(data)

        return RoleCreateEvent(Role(data, kord), context?.get())
    }

    private suspend fun handle(
        event: GuildRoleUpdate,
        kord: Kord,
        context: LazyContext?,
    ): RoleUpdateEvent {
        val data = RoleData.from(event.role)

        val oldData = kord.cache.query<RoleData> {
            idEq(RoleData::id, data.id)
            idEq(RoleData::guildId, data.guildId) // TODO("Is this worth keeping?") yes
        }.singleOrNull()

        val old = oldData?.let { Role(it, kord) }
        kord.cache.put(data)

        return RoleUpdateEvent(Role(data, kord), old, context?.get())
    }

    private suspend fun handle(
        event: GuildRoleDelete,
        kord: Kord,
        context: LazyContext?,
    ): RoleDeleteEvent = with(event.role) {
        val query = kord.cache.query<RoleData> { idEq(RoleData::id, event.role.id) }

        val old = run {
            val data = query.singleOrNull() ?: return@run null
            Role(data, kord)
        }

        query.remove()

        return RoleDeleteEvent(guildId, id, old, kord, context?.get())
    }

    private suspend fun handle(
        event: GuildMembersChunk,
        kord: Kord,
        context: LazyContext?,
    ): MembersChunkEvent = with(event.data) {
        val presences = presences.orEmpty().map { PresenceData.from(guildId, it) }
        kord.cache.putAll(presences)

        members.map { member ->
            val memberData = MemberData.from(member.user.value!!.id, guildId, member)
            kord.cache.put(memberData)
            val userData = UserData.from(member.user.value!!)
            kord.cache.put(userData)
        }

        return MembersChunkEvent(MembersChunkData.from(this), kord, context?.get())
    }

    private suspend fun handle(
        event: GuildScheduledEventCreate,
        kord: Kord,
        context: LazyContext?,
    ): GuildScheduledEventCreateEvent {
        val eventData = GuildScheduledEventData.from(event.event)
        kord.cache.put(eventData)
        val scheduledEvent = GuildScheduledEvent(eventData, kord)

        return GuildScheduledEventCreateEvent(scheduledEvent, kord, context?.get())
    }

    private suspend fun handle(
        event: GuildScheduledEventUpdate,
        kord: Kord,
        context: LazyContext?,
    ): GuildScheduledEventUpdateEvent {
        val eventData = GuildScheduledEventData.from(event.event)
        val oldData = kord.cache.query<GuildScheduledEventData> {
            idEq(GuildScheduledEventData::id, event.event.id)
        }.singleOrNull()
        val old = oldData?.let { GuildScheduledEvent(it, kord) }
        kord.cache.put(eventData)
        val scheduledEvent = GuildScheduledEvent(eventData, kord)

        return GuildScheduledEventUpdateEvent(scheduledEvent, old, kord, context?.get())
    }

    private suspend fun handle(
        event: GuildScheduledEventDelete,
        kord: Kord,
        context: LazyContext?,
    ): GuildScheduledEventDeleteEvent {
        val query = kord.cache.query<GuildScheduledEvent> {
            idEq(GuildScheduledEvent::id, event.event.id)
        }
        query.remove()

        val eventData = GuildScheduledEventData.from(event.event)
        val scheduledEvent = GuildScheduledEvent(eventData, kord)

        return GuildScheduledEventDeleteEvent(scheduledEvent, kord, context?.get())
    }

    private suspend fun handle(
        event: GuildScheduledEventUserAdd,
        kord: Kord,
        context: LazyContext?,
    ): GuildScheduledEventUserAddEvent = with(event.data) {
        GuildScheduledEventUserAddEvent(
            guildScheduledEventId,
            userId,
            guildId,
            kord,
            context?.get(),
        )
    }

    private suspend fun handle(
        event: GuildScheduledEventUserRemove,
        kord: Kord,
        context: LazyContext?,
    ): GuildScheduledEventUserRemoveEvent = with(event.data) {
        GuildScheduledEventUserRemoveEvent(
            guildScheduledEventId,
            userId,
            guildId,
            kord,
            context?.get(),
        )
    }

    private suspend fun handle(
        event: PresenceUpdate,
        kord: Kord,
        context: LazyContext?,
    ): PresenceUpdateEvent =
        with(event.presence) {
            val data = PresenceData.from(this.guildId.value!!, this)

            val old = kord.cache.query<PresenceData> { idEq(PresenceData::id, data.id) }
                .asFlow().map { Presence(it, kord) }.singleOrNull()

            kord.cache.put(data)
            val new = Presence(data, kord)

            val user = kord.cache
                .query<UserData> { idEq(UserData::id, event.presence.user.id) }
                .singleOrNull()
                ?.let { User(it, kord) }

            return PresenceUpdateEvent(
                user,
                this.user,
                guildId.value!!,
                old,
                new,
                context?.get(),
            )
        }

    private suspend fun handle(
        event: InviteCreate,
        kord: Kord,
        context: LazyContext?,
    ): InviteCreateEvent = with(event) {
        val data = InviteCreateData.from(invite)

        invite.inviter.value?.let { kord.cache.put(UserData.from(it)) }
        invite.targetUser.value?.let { kord.cache.put(UserData.from(it)) }

        return InviteCreateEvent(data, kord, context?.get())
    }

    private suspend fun handle(
        event: InviteDelete,
        kord: Kord,
        context: LazyContext?,
    ): InviteDeleteEvent = with(event) {
        val data = InviteDeleteData.from(invite)
        return InviteDeleteEvent(data, kord, context?.get())
    }
}
