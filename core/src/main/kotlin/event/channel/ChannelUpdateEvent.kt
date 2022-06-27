package dev.jombi.kordsb.core.event.channel

import dev.jombi.kordsb.core.Kord
import dev.jombi.kordsb.core.entity.channel.*
import dev.jombi.kordsb.core.event.Event
import dev.jombi.kordsb.core.event.kordCoroutineScope
import kotlinx.coroutines.CoroutineScope
import kotlin.DeprecationLevel.WARNING


public interface ChannelUpdateEvent : Event {
    public val channel: Channel
    public val old: Channel?
    override val kord: Kord
        get() = channel.kord
}

public class CategoryUpdateEvent(
    override val channel: Category,
    override val old: Category?,
    public val coroutineScope: CoroutineScope = kordCoroutineScope(channel.kord)
) : ChannelUpdateEvent, CoroutineScope by coroutineScope {
    override fun toString(): String {
        return "CategoryUpdateEvent(channel=$channel, old=$old)"
    }
}

public class DMChannelUpdateEvent(
    override val channel: DmChannel,
    override val old: DmChannel?,
    public val coroutineScope: CoroutineScope = kordCoroutineScope(channel.kord)
) : ChannelUpdateEvent, CoroutineScope by coroutineScope {
    override fun toString(): String {
        return "DMChannelUpdateEvent(channel=$channel, old=$old)"
    }
}

public class NewsChannelUpdateEvent(
    override val channel: NewsChannel,
    override val old: NewsChannel?,
    public val coroutineScope: CoroutineScope = kordCoroutineScope(channel.kord)
) : ChannelUpdateEvent, CoroutineScope by coroutineScope {
    override fun toString(): String {
        return "NewsChannelUpdateEvent(channel=$channel, old=$old)"
    }
}

@Suppress("DEPRECATION")
@Deprecated(
    """
    Discord no longer offers the ability to purchase a license to sell PC games on Discord and store channels were
    removed on March 10, 2022.
    
    See https://support-dev.discord.com/hc/en-us/articles/4414590563479 for more information.
    """,
    level = WARNING,
)
public class StoreChannelUpdateEvent(
    override val channel: StoreChannel,
    override val old: StoreChannel?,
    public val coroutineScope: CoroutineScope = kordCoroutineScope(channel.kord)
) : ChannelUpdateEvent, CoroutineScope by coroutineScope {
    override fun toString(): String {
        return "StoreChannelUpdateEvent(channel=$channel, old=$old)"
    }
}

public class TextChannelUpdateEvent(
    override val channel: TextChannel,
    override val old: TextChannel?,
    public val coroutineScope: CoroutineScope = kordCoroutineScope(channel.kord)
) : ChannelUpdateEvent, CoroutineScope by coroutineScope {
    override fun toString(): String {
        return "TextChannelUpdateEvent(channel=$channel, old=$old)"
    }
}

public class VoiceChannelUpdateEvent(
    override val channel: VoiceChannel,
    override val old: VoiceChannel?,
    public val coroutineScope: CoroutineScope = kordCoroutineScope(channel.kord)
) : ChannelUpdateEvent, CoroutineScope by coroutineScope{
    override fun toString(): String {
        return "VoiceChannelUpdateEvent(channel=$channel, old=$old)"
    }
}


public class StageChannelUpdateEvent(
    override val channel: StageChannel,
    override val old: StageChannel?,
    public val coroutineScope: CoroutineScope = kordCoroutineScope(channel.kord)
) : ChannelUpdateEvent, CoroutineScope by coroutineScope {
    override fun toString(): String {
        return "StageChannelUpdateEvent(channel=$channel, old=$old)"
    }
}

public class UnknownChannelUpdateEvent(
    override val channel: Channel,
    override val old: Channel?,
    public val coroutineScope: CoroutineScope = kordCoroutineScope(channel.kord)
) : ChannelUpdateEvent, CoroutineScope by coroutineScope {
    override fun toString(): String {
        return "UnknownChannelUpdateEvent(channel=$channel, old=$old)"
    }
}
