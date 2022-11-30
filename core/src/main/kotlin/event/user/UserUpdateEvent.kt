package dev.jombi.kordsb.core.event.user

import dev.jombi.kordsb.core.Kord
import dev.jombi.kordsb.core.entity.User
import dev.jombi.kordsb.core.event.Event

public class UserUpdateEvent(
    public val old: User?,
    public val user: User,
    override val customContext: Any?,
) : Event {
    override val kord: Kord get() = user.kord

    override fun toString(): String {
        return "UserUpdateEvent(old=$old, user=$user)"
    }
}
