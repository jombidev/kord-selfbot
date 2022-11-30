package dev.jombi.kordsb.core.live.channel

import dev.jombi.kordsb.common.annotation.KordPreview
import dev.jombi.kordsb.common.entity.Snowflake
import dev.jombi.kordsb.core.entity.KordEntity
import dev.jombi.kordsb.core.entity.channel.VoiceChannel
import dev.jombi.kordsb.core.event.Event
import dev.jombi.kordsb.core.event.channel.VoiceChannelCreateEvent
import dev.jombi.kordsb.core.event.channel.VoiceChannelDeleteEvent
import dev.jombi.kordsb.core.event.channel.VoiceChannelUpdateEvent
import dev.jombi.kordsb.core.event.guild.GuildDeleteEvent
import dev.jombi.kordsb.core.live.exception.LiveCancellationException
import dev.jombi.kordsb.core.live.on
import kotlinx.coroutines.*

@KordPreview
public fun VoiceChannel.live(
    coroutineScope: CoroutineScope = kord + SupervisorJob(kord.coroutineContext.job)
): LiveVoiceChannel = LiveVoiceChannel(this, coroutineScope)

@KordPreview
public inline fun VoiceChannel.live(
    coroutineScope: CoroutineScope = kord + SupervisorJob(kord.coroutineContext.job),
    block: LiveVoiceChannel.() -> Unit
): LiveVoiceChannel = this.live(coroutineScope).apply(block)

@Suppress("DeprecatedCallableAddReplaceWith")
@Deprecated(
    "The block is never called because the channel is already created, use LiveGuild.onChannelCreate(block)",
    level = DeprecationLevel.HIDDEN
)
@KordPreview
public fun LiveVoiceChannel.onCreate(
    scope: CoroutineScope = this,
    block: suspend (VoiceChannelCreateEvent) -> Unit
): Job =
    on(scope = scope, consumer = block)

@KordPreview
public fun LiveVoiceChannel.onUpdate(
    scope: CoroutineScope = this,
    block: suspend (VoiceChannelUpdateEvent) -> Unit
): Job =
    on(scope = scope, consumer = block)

@Deprecated(
    "The block is not called when the live entity is shut down",
    ReplaceWith("coroutineContext.job.invokeOnCompletion(block)", "kotlinx.coroutines.job"),
    DeprecationLevel.HIDDEN
)
@KordPreview
public inline fun LiveVoiceChannel.onShutDown(
    scope: CoroutineScope = this,
    crossinline block: suspend (Event) -> Unit
): Job =
    on<Event>(scope) {
        if (it is VoiceChannelDeleteEvent || it is GuildDeleteEvent) {
            block(it)
        }
    }

@Deprecated(
    "The block is not called when the entity is deleted because the live entity is shut down",
    ReplaceWith("coroutineContext.job.invokeOnCompletion(block)", "kotlinx.coroutines.job"),
    DeprecationLevel.HIDDEN
)
@KordPreview
public fun LiveVoiceChannel.onDelete(
    scope: CoroutineScope = this,
    block: suspend (VoiceChannelDeleteEvent) -> Unit
): Job =
    on(scope = scope, consumer = block)

@Deprecated(
    "The block is not called when the entity is deleted because the live entity is shut down",
    ReplaceWith("coroutineContext.job.invokeOnCompletion(block)", "kotlinx.coroutines.job"),
    DeprecationLevel.HIDDEN
)
@KordPreview
public fun LiveVoiceChannel.onGuildDelete(
    scope: CoroutineScope = this,
    block: suspend (GuildDeleteEvent) -> Unit
): Job =
    on(scope = scope, consumer = block)

@KordPreview
public class LiveVoiceChannel(
    channel: VoiceChannel,
    coroutineScope: CoroutineScope = channel.kord + SupervisorJob(channel.kord.coroutineContext.job)
) : LiveChannel(channel.kord, coroutineScope), KordEntity {

    override val id: Snowflake
        get() = channel.id

    override var channel: VoiceChannel = channel
        private set

    override fun update(event: Event): Unit = when (event) {
        is VoiceChannelCreateEvent -> channel = event.channel
        is VoiceChannelUpdateEvent -> channel = event.channel
        is VoiceChannelDeleteEvent -> shutDown(LiveCancellationException(event, "The channel is deleted"))

        is GuildDeleteEvent -> shutDown(LiveCancellationException(event, "The guild is deleted"))

        else -> Unit
    }

}
