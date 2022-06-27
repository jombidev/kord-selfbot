package dev.jombi.kordsb.core.event.user

import dev.jombi.kordsb.core.Kord
import dev.jombi.kordsb.core.entity.User
import dev.jombi.kordsb.core.event.Event
import dev.jombi.kordsb.core.event.kordCoroutineScope
import kotlinx.coroutines.CoroutineScope
import kotlin.coroutines.CoroutineContext

public class UserUpdateEvent(
    public val old: User?,
    public val user: User,
    public val coroutineScope: CoroutineScope = kordCoroutineScope(user.kord)
) : Event, CoroutineScope by coroutineScope {
    override val kord: Kord get() = user.kord

    override fun toString(): String {
        return "UserUpdateEvent(old=$old, user=$user)"
    }
}
