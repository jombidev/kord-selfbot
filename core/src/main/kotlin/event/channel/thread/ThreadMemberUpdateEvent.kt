package dev.jombi.kordsb.core.event.channel.thread

import dev.jombi.kordsb.core.Kord
import dev.jombi.kordsb.core.entity.channel.thread.ThreadMember
import dev.jombi.kordsb.core.event.Event
import dev.jombi.kordsb.core.event.kordCoroutineScope
import kotlinx.coroutines.CoroutineScope
import kotlin.coroutines.CoroutineContext

public class ThreadMemberUpdateEvent(
    public val member: ThreadMember,
    override val kord: Kord,
    public val coroutineScope: CoroutineScope = kordCoroutineScope(kord)
) : Event, CoroutineScope by coroutineScope
