package dev.jombi.kordsb.core.event.channel

import dev.jombi.kordsb.core.Kord
import dev.jombi.kordsb.core.entity.channel.*
import dev.jombi.kordsb.core.event.Event
import kotlin.DeprecationLevel.ERROR

public interface ChannelCreateEvent : Event {
    public val channel: Channel
    override val kord: Kord
        get() = channel.kord
}

public class CategoryCreateEvent(
    override val channel: Category,
    override val customContext: Any?,
) : ChannelCreateEvent {
    override fun toString(): String {
        return "CategoryCreateEvent(channel=$channel)"
    }
}

public class DMChannelCreateEvent(
    override val channel: DmChannel,
    override val customContext: Any?,
) : ChannelCreateEvent {
    override fun toString(): String {
        return "DMChannelCreateEvent(channel=$channel)"
    }
}

public class NewsChannelCreateEvent(
    override val channel: NewsChannel,
    override val customContext: Any?,
) : ChannelCreateEvent {
    override fun toString(): String {
        return "NewsChannelCreateEvent(channel=$channel)"
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
public class StoreChannelCreateEvent(
    override val channel: StoreChannel,
    override val customContext: Any?,
) : ChannelCreateEvent {
    override fun toString(): String {
        return "StoreChannelCreateEvent(channel=$channel)"
    }
}

public class TextChannelCreateEvent(
    override val channel: TextChannel,
    override val customContext: Any?,
) : ChannelCreateEvent {
    override fun toString(): String {
        return "TextChannelCreateEvent(channel=$channel)"
    }
}

public class VoiceChannelCreateEvent(
    override val channel: VoiceChannel,
    override val customContext: Any?,
) : ChannelCreateEvent {
    override fun toString(): String {
        return "VoiceChannelCreateEvent(channel=$channel)"
    }
}


public class StageChannelCreateEvent(
    override val channel: StageChannel,
    override val customContext: Any?,
) : ChannelCreateEvent {
    override fun toString(): String {
        return "StageChannelCreateEvent(channel=$channel)"
    }
}

public class UnknownChannelCreateEvent(
    override val channel: Channel,
    override val customContext: Any?,
) : ChannelCreateEvent {
    override fun toString(): String {
        return "UnknownChannelCreateEvent(channel=$channel)"
    }
}
