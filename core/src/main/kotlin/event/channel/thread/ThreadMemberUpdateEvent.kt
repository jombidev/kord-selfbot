package dev.jombi.kordsb.core.event.channel.thread

import dev.jombi.kordsb.core.Kord
import dev.jombi.kordsb.core.entity.channel.thread.ThreadMember
import dev.jombi.kordsb.core.event.Event

public class ThreadMemberUpdateEvent(
    public val member: ThreadMember,
    override val kord: Kord,
    override val customContext: Any?,
) : Event
