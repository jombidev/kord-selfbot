package dev.jombi.kordsb.core.live.channel

import dev.jombi.kordsb.common.annotation.KordPreview
import dev.jombi.kordsb.common.entity.Snowflake
import dev.jombi.kordsb.core.entity.KordEntity
import dev.jombi.kordsb.core.entity.channel.TopGuildMessageChannel
import dev.jombi.kordsb.core.event.Event
import dev.jombi.kordsb.core.event.channel.ChannelCreateEvent
import dev.jombi.kordsb.core.event.channel.ChannelDeleteEvent
import dev.jombi.kordsb.core.event.channel.ChannelUpdateEvent
import dev.jombi.kordsb.core.event.guild.GuildDeleteEvent
import dev.jombi.kordsb.core.live.exception.LiveCancellationException
import dev.jombi.kordsb.core.live.on
import kotlinx.coroutines.*

@KordPreview
public fun TopGuildMessageChannel.live(
    coroutineScope: CoroutineScope = kord + SupervisorJob(kord.coroutineContext.job)
): LiveGuildMessageChannel = LiveGuildMessageChannel(this, coroutineScope)

@KordPreview
public inline fun TopGuildMessageChannel.live(
    coroutineScope: CoroutineScope = kord + SupervisorJob(kord.coroutineContext.job),
    block: LiveGuildMessageChannel.() -> Unit
): LiveGuildMessageChannel = this.live(coroutineScope).apply(block)

@Suppress("DeprecatedCallableAddReplaceWith")
@Deprecated(
    "The block is never called because the channel is already created, use LiveGuild.onChannelCreate(block)",
    level = DeprecationLevel.ERROR
)
@KordPreview
public fun LiveGuildMessageChannel.onCreate(scope: CoroutineScope = this, block: suspend (ChannelCreateEvent) -> Unit): Job =
    on(scope = scope, consumer = block)

@KordPreview
public fun LiveGuildMessageChannel.onUpdate(scope: CoroutineScope = this, block: suspend (ChannelUpdateEvent) -> Unit): Job =
    on(scope = scope, consumer = block)

@Deprecated(
    "The block is not called when the live entity is shut down",
    ReplaceWith("coroutineContext.job.invokeOnCompletion(block)", "kotlinx.coroutines.job"),
    DeprecationLevel.ERROR
)
@KordPreview
public inline fun LiveGuildMessageChannel.onShutDown(
    scope: CoroutineScope = this,
    crossinline block: suspend (Event) -> Unit
): Job = on<Event>(scope) {
    if (it is ChannelDeleteEvent || it is GuildDeleteEvent) {
        block(it)
    }
}

@Deprecated(
    "The block is not called when the entity is deleted because the live entity is shut down",
    ReplaceWith("coroutineContext.job.invokeOnCompletion(block)", "kotlinx.coroutines.job"),
    DeprecationLevel.ERROR
)
@KordPreview
public fun LiveGuildMessageChannel.onChannelDelete(scope: CoroutineScope = this, block: suspend (ChannelDeleteEvent) -> Unit): Job =
    on(scope = scope, consumer = block)

@Deprecated(
    "The block is not called when the entity is deleted because the live entity is shut down",
    ReplaceWith("coroutineContext.job.invokeOnCompletion(block)", "kotlinx.coroutines.job"),
    DeprecationLevel.ERROR
)
@KordPreview
public fun LiveGuildMessageChannel.onDelete(scope: CoroutineScope = this, block: suspend (GuildDeleteEvent) -> Unit): Job =
    on(scope = scope, consumer = block)

@KordPreview
public class LiveGuildMessageChannel(
    channel: TopGuildMessageChannel,
    coroutineScope: CoroutineScope = channel.kord + SupervisorJob(channel.kord.coroutineContext.job)
) : LiveChannel(channel.kord, coroutineScope), KordEntity {

    override val id: Snowflake
        get() = channel.id

    override var channel: TopGuildMessageChannel = channel
        private set

    override fun update(event: Event): Unit = when (event) {
        is ChannelCreateEvent -> channel = event.channel as TopGuildMessageChannel
        is ChannelUpdateEvent -> channel = event.channel as TopGuildMessageChannel
        is ChannelDeleteEvent -> shutDown(LiveCancellationException(event, "The channel is deleted"))

        is GuildDeleteEvent -> shutDown(LiveCancellationException(event, "The guild is deleted"))

        else -> Unit
    }

}
