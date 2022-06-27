package dev.jombi.kordsb.core.live.channel

import dev.jombi.kordsb.common.annotation.KordPreview
import dev.jombi.kordsb.common.entity.Snowflake
import dev.jombi.kordsb.core.entity.KordEntity
import dev.jombi.kordsb.core.entity.channel.TopGuildChannel
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
public fun TopGuildChannel.live(
    coroutineScope: CoroutineScope = kord + SupervisorJob(kord.coroutineContext.job)
): LiveGuildChannel = LiveGuildChannel(this, coroutineScope)

@KordPreview
public inline fun TopGuildChannel.live(
    coroutineScope: CoroutineScope = kord + SupervisorJob(kord.coroutineContext.job),
    block: LiveGuildChannel.() -> Unit
): LiveGuildChannel = this.live(coroutineScope).apply(block)

@Suppress("DeprecatedCallableAddReplaceWith")
@Deprecated(
    "The block is never called because the channel is already created, use LiveGuild.onChannelCreate(block)",
    level = DeprecationLevel.ERROR
)
@KordPreview
public fun LiveGuildChannel.onCreate(scope: CoroutineScope = this, block: suspend (ChannelCreateEvent) -> Unit): Job =
    on(scope = scope, consumer = block)

@KordPreview
public fun LiveGuildChannel.onUpdate(scope: CoroutineScope = this, block: suspend (ChannelUpdateEvent) -> Unit): Job =
    on(scope = scope, consumer = block)

@Deprecated(
    "The block is not called when the live entity is shut down",
    ReplaceWith("coroutineContext.job.invokeOnCompletion(block)", "kotlinx.coroutines.job"),
    DeprecationLevel.ERROR
)
@KordPreview
public inline fun LiveGuildChannel.onShutDown(scope: CoroutineScope = this, crossinline block: suspend (Event) -> Unit): Job =
    on<Event>(scope) {
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
public fun LiveGuildChannel.onDelete(scope: CoroutineScope = this, block: suspend (ChannelDeleteEvent) -> Unit): Job =
    on(scope = scope, consumer = block)

@Deprecated(
    "The block is not called when the entity is deleted because the live entity is shut down",
    ReplaceWith("coroutineContext.job.invokeOnCompletion(block)", "kotlinx.coroutines.job"),
    DeprecationLevel.ERROR
)
@KordPreview
public fun LiveGuildChannel.onGuildDelete(scope: CoroutineScope = this, block: suspend (GuildDeleteEvent) -> Unit): Job =
    on(scope = scope, consumer = block)

@KordPreview
public class LiveGuildChannel(
    channel: TopGuildChannel,
    coroutineScope: CoroutineScope = channel.kord + SupervisorJob(channel.kord.coroutineContext.job)
) : LiveChannel(channel.kord, coroutineScope), KordEntity {

    override val id: Snowflake
        get() = channel.id

    override var channel: TopGuildChannel = channel
        private set

    override fun update(event: Event): Unit = when (event) {
        is ChannelCreateEvent -> channel = event.channel as TopGuildMessageChannel
        is ChannelUpdateEvent -> channel = event.channel as TopGuildMessageChannel
        is ChannelDeleteEvent -> shutDown(LiveCancellationException(event, "The channel is deleted"))

        is GuildDeleteEvent -> shutDown(LiveCancellationException(event, "The guild is deleted"))

        else -> Unit
    }

}
