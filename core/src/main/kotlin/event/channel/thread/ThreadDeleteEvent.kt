package dev.jombi.kordsb.core.event.channel.thread

import dev.jombi.kordsb.core.Kord
import dev.jombi.kordsb.core.entity.channel.thread.DeletedThreadChannel
import dev.jombi.kordsb.core.entity.channel.thread.NewsChannelThread
import dev.jombi.kordsb.core.entity.channel.thread.TextChannelThread
import dev.jombi.kordsb.core.entity.channel.thread.ThreadChannel
import dev.jombi.kordsb.core.event.Event
import dev.jombi.kordsb.core.event.kordCoroutineScope
import kotlinx.coroutines.CoroutineScope
import kotlin.coroutines.CoroutineContext

public sealed interface ThreadChannelDeleteEvent : Event {
    public val channel: DeletedThreadChannel

    public val old: ThreadChannel?

    override val kord: Kord
        get() = channel.kord

}


public class TextChannelThreadDeleteEvent(
    override val channel: DeletedThreadChannel,
    override val old: TextChannelThread?,
    public val coroutineScope: CoroutineScope = kordCoroutineScope(channel.kord)
) : ThreadChannelDeleteEvent, CoroutineScope by coroutineScope {
    override fun toString(): String {
        return "TextThreadChannelDeleteEvent(channel=$channel)"
    }
}


public class NewsChannelThreadDeleteEvent(
    override val channel: DeletedThreadChannel,
    override val old: NewsChannelThread?,
    public val coroutineScope: CoroutineScope = kordCoroutineScope(channel.kord)
) : ThreadChannelDeleteEvent, CoroutineScope by coroutineScope {
    override fun toString(): String {
        return "NewsThreadChannelDeleteEvent(channel=$channel)"
    }
}


public class UnknownChannelThreadDeleteEvent(
    override val channel: DeletedThreadChannel,
    override val old: ThreadChannel?,
    public val coroutineScope: CoroutineScope = kordCoroutineScope(channel.kord)
) : ThreadChannelDeleteEvent, CoroutineScope by coroutineScope {
    override fun toString(): String {
        return "UnknownChannelThreadDeleteEvent(channel=$channel)"
    }
}
