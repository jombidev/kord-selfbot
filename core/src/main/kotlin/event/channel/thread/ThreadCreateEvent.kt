package dev.jombi.kordsb.core.event.channel.thread

import dev.jombi.kordsb.core.entity.channel.thread.NewsChannelThread
import dev.jombi.kordsb.core.entity.channel.thread.TextChannelThread
import dev.jombi.kordsb.core.entity.channel.thread.ThreadChannel
import dev.jombi.kordsb.core.event.channel.ChannelCreateEvent
import dev.jombi.kordsb.core.event.kordCoroutineScope
import kotlinx.coroutines.CoroutineScope
import kotlin.coroutines.CoroutineContext

public sealed interface ThreadChannelCreateEvent : ChannelCreateEvent {
    override val channel: ThreadChannel
}


public class TextChannelThreadCreateEvent(
    override val channel: TextChannelThread,
    public val coroutineScope: CoroutineScope = kordCoroutineScope(channel.kord)
) : ThreadChannelCreateEvent, CoroutineScope by coroutineScope {
    override fun toString(): String {
        return "TextThreadChannelCreateEvent(channel=$channel)"
    }
}


public class NewsChannelThreadCreateEvent(
    override val channel: NewsChannelThread,
    public val coroutineScope: CoroutineScope = kordCoroutineScope(channel.kord)
) : ThreadChannelCreateEvent, CoroutineScope by coroutineScope {
    override fun toString(): String {
        return "NewsThreadChannelCreateEvent(channel=$channel)"
    }
}

public class UnknownChannelThreadCreateEvent(
    override val channel: ThreadChannel,
    public val coroutineScope: CoroutineScope = kordCoroutineScope(channel.kord)
) : ThreadChannelCreateEvent, CoroutineScope by coroutineScope {
    override fun toString(): String {
        return "UnknownChannelThreadCreateEvent(channel=$channel)"
    }
}
