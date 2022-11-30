package dev.jombi.kordsb.core.event.channel

import dev.jombi.kordsb.core.Kord
import dev.jombi.kordsb.core.entity.channel.*
import dev.jombi.kordsb.core.event.Event
import kotlin.DeprecationLevel.ERROR

public interface ChannelDeleteEvent : Event {
    public val channel: Channel
    override val kord: Kord
        get() = channel.kord
}

public class CategoryDeleteEvent(
    override val channel: Category,
    override val customContext: Any?,
) : ChannelDeleteEvent {
    override fun toString(): String {
        return "CategoryDeleteEvent(channel=$channel)"
    }
}

public class DMChannelDeleteEvent(
    override val channel: DmChannel,
    override val customContext: Any?,
) : ChannelDeleteEvent {
    override fun toString(): String {
        return "DMChannelDeleteEvent(channel=$channel)"
    }
}

public class NewsChannelDeleteEvent(
    override val channel: NewsChannel,
    override val customContext: Any?,
) : ChannelDeleteEvent {
    override fun toString(): String {
        return "NewsChannelDeleteEvent(channel=$channel)"
    }
}

/** @suppress */
@Suppress("DEPRECATION_ERROR")
@Deprecated(
    """
    Discord no longer offers the ability to purchase a license to sell PC games on Discord and store channels were
    removed on March 10, 2022.
    
    See https://support-dev.discord.com/hc/en-us/articles/4414590563479 for more information.
    """,
    level = ERROR,
)
public class StoreChannelDeleteEvent(
    override val channel: StoreChannel,
    override val customContext: Any?,
) : ChannelDeleteEvent {
    override fun toString(): String {
        return "StoreChannelDeleteEvent(channel=$channel)"
    }
}

public class TextChannelDeleteEvent(
    override val channel: TextChannel,
    override val customContext: Any?,
) : ChannelDeleteEvent {
    override fun toString(): String {
        return "TextChannelDeleteEvent(channel=$channel)"
    }
}

public class VoiceChannelDeleteEvent(
    override val channel: VoiceChannel,
    override val customContext: Any?,
) : ChannelDeleteEvent {
    override fun toString(): String {
        return "VoiceChannelDeleteEvent(channel=$channel)"
    }
}

public class StageChannelDeleteEvent(
    override val channel: StageChannel,
    override val customContext: Any?,
) : ChannelDeleteEvent {
    override fun toString(): String {
        return "StageChannelDeleteEvent(channel=$channel)"
    }
}

public class UnknownChannelDeleteEvent(
    override val channel: Channel,
    override val customContext: Any?,
) : ChannelDeleteEvent {
    override fun toString(): String {
        return "UnknownChannelDeleteEvent(channel=$channel)"
    }
}
