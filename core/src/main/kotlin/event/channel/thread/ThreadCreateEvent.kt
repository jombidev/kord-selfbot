package dev.jombi.kordsb.core.event.channel.thread

import dev.jombi.kordsb.core.entity.channel.thread.NewsChannelThread
import dev.jombi.kordsb.core.entity.channel.thread.TextChannelThread
import dev.jombi.kordsb.core.entity.channel.thread.ThreadChannel
import dev.jombi.kordsb.core.event.channel.ChannelCreateEvent

public sealed interface ThreadChannelCreateEvent : ChannelCreateEvent {
    override val channel: ThreadChannel
}


public class TextChannelThreadCreateEvent(
    override val channel: TextChannelThread,
    override val customContext: Any?,
) : ThreadChannelCreateEvent {
    override fun toString(): String {
        return "TextThreadChannelCreateEvent(channel=$channel)"
    }
}


public class NewsChannelThreadCreateEvent(
    override val channel: NewsChannelThread,
    override val customContext: Any?,
) : ThreadChannelCreateEvent {
    override fun toString(): String {
        return "NewsThreadChannelCreateEvent(channel=$channel)"
    }
}

public class UnknownChannelThreadCreateEvent(
    override val channel: ThreadChannel,
    override val customContext: Any?,
) : ThreadChannelCreateEvent {
    override fun toString(): String {
        return "UnknownChannelThreadCreateEvent(channel=$channel)"
    }
}
