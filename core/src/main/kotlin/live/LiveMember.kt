package dev.jombi.kordsb.core.live

import dev.jombi.kordsb.common.annotation.KordPreview
import dev.jombi.kordsb.common.entity.Snowflake
import dev.jombi.kordsb.core.entity.KordEntity
import dev.jombi.kordsb.core.entity.Member
import dev.jombi.kordsb.core.event.Event
import dev.jombi.kordsb.core.event.guild.BanAddEvent
import dev.jombi.kordsb.core.event.guild.GuildDeleteEvent
import dev.jombi.kordsb.core.event.guild.MemberLeaveEvent
import dev.jombi.kordsb.core.event.guild.MemberUpdateEvent
import dev.jombi.kordsb.core.live.channel.LiveGuildChannel
import dev.jombi.kordsb.core.live.exception.LiveCancellationException
import kotlinx.coroutines.*

@KordPreview
public fun Member.live(
    coroutineScope: CoroutineScope = kord + SupervisorJob(kord.coroutineContext.job)
): LiveMember = LiveMember(this, coroutineScope)

@KordPreview
public inline fun Member.live(
    coroutineScope: CoroutineScope = kord + SupervisorJob(kord.coroutineContext.job),
    block: LiveMember.() -> Unit
): LiveMember = this.live(coroutineScope).apply(block)

@Deprecated(
    "The block is not called when the entity is deleted because the live entity is shut down",
    ReplaceWith("coroutineContext.job.invokeOnCompletion(block)", "kotlinx.coroutines.job"),
    DeprecationLevel.HIDDEN
)
@KordPreview
public fun LiveMember.onLeave(scope: CoroutineScope = this, block: suspend (MemberLeaveEvent) -> Unit): Job =
    on(scope = scope, consumer = block)

@KordPreview
public fun LiveMember.onUpdate(scope: CoroutineScope = this, block: suspend (MemberUpdateEvent) -> Unit): Job =
    on(scope = scope, consumer = block)

@Deprecated(
    "The block is not called when the entity is deleted because the live entity is shut down",
    ReplaceWith("coroutineContext.job.invokeOnCompletion(block)", "kotlinx.coroutines.job"),
    DeprecationLevel.HIDDEN
)
@KordPreview
public fun LiveMember.onBanAdd(scope: CoroutineScope = this, block: suspend (BanAddEvent) -> Unit): Job =
    on(scope = scope, consumer = block)

@Deprecated(
    "The block is not called when the live entity is shut down",
    ReplaceWith("coroutineContext.job.invokeOnCompletion(block)", "kotlinx.coroutines.job"),
    DeprecationLevel.HIDDEN
)
@KordPreview
public inline fun LiveGuildChannel.onShutdown(
    scope: CoroutineScope = this,
    crossinline block: suspend (Event) -> Unit
): Job =
    on<Event>(scope) {
        if (it is MemberLeaveEvent || it is BanAddEvent || it is GuildDeleteEvent) {
            block(it)
        }
    }

@Deprecated(
    "The block is not called when the entity is deleted because the live entity is shut down",
    ReplaceWith("coroutineContext.job.invokeOnCompletion(block)", "kotlinx.coroutines.job"),
    DeprecationLevel.HIDDEN
)
@KordPreview
public fun LiveGuildChannel.onGuildDelete(
    scope: CoroutineScope = this,
    block: suspend (GuildDeleteEvent) -> Unit
): Job =
    on(scope = scope, consumer = block)

@KordPreview
public class LiveMember(
    member: Member,
    coroutineScope: CoroutineScope = member.kord + SupervisorJob(member.kord.coroutineContext.job)
) : AbstractLiveKordEntity(member.kord, coroutineScope), KordEntity {

    override val id: Snowflake
        get() = member.id

    public var member: Member = member
        private set

    override fun filter(event: Event): Boolean = when (event) {
        is MemberLeaveEvent -> member.id == event.user.id
        is MemberUpdateEvent -> member.id == event.member.id
        is BanAddEvent -> member.id == event.user.id
        is GuildDeleteEvent -> member.guildId == event.guildId
        else -> false
    }

    override fun update(event: Event): Unit = when (event) {
        is MemberLeaveEvent -> shutDown(LiveCancellationException(event, "The member has left"))
        is BanAddEvent -> shutDown(LiveCancellationException(event, "The member is banned"))
        is GuildDeleteEvent -> shutDown(LiveCancellationException(event, "The guild is deleted"))
        is MemberUpdateEvent -> member = event.member

        else -> Unit
    }
}
