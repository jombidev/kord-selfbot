package dev.jombi.kordsb.core.event.channel.thread

import dev.jombi.kordsb.core.entity.channel.thread.NewsChannelThread
import dev.jombi.kordsb.core.entity.channel.thread.TextChannelThread
import dev.jombi.kordsb.core.entity.channel.thread.ThreadChannel
import dev.jombi.kordsb.core.event.channel.ChannelUpdateEvent
import dev.jombi.kordsb.core.event.kordCoroutineScope
import kotlinx.coroutines.CoroutineScope

public sealed interface ThreadUpdateEvent : ChannelUpdateEvent {
    override val channel: ThreadChannel
}


public class TextChannelThreadUpdateEvent(
    override val channel: TextChannelThread,
    override val old: TextChannelThread?,
    public val coroutineScope: CoroutineScope = kordCoroutineScope(channel.kord)
) :
    ThreadUpdateEvent, CoroutineScope by coroutineScope {
    override fun toString(): String {
        return "TextThreadChannelUpdateEvent(channel=$channel, old=$old)"
    }
}


public class NewsChannelThreadUpdateEvent(
    override val channel: NewsChannelThread,
    override val old: NewsChannelThread?,
    public val coroutineScope: CoroutineScope = kordCoroutineScope(channel.kord)
) : ThreadUpdateEvent, CoroutineScope by coroutineScope {
    override fun toString(): String {
        return "NewsThreadChannelUpdateEvent(channel=$channel, old=$old)"
    }
}


public class UnknownChannelThreadUpdateEvent(
    override val channel: ThreadChannel,
    override val old: ThreadChannel?,
    public val coroutineScope: CoroutineScope = kordCoroutineScope(channel.kord)
) : ThreadUpdateEvent, CoroutineScope by coroutineScope {
    override fun toString(): String {
        return "UnknownChannelThreadUpdateEvent(channel=$channel, old=$old)"
    }
}
