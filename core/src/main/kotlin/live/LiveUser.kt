package dev.jombi.kordsb.core.live

import dev.jombi.kordsb.common.annotation.KordPreview
import dev.jombi.kordsb.common.entity.Snowflake
import dev.jombi.kordsb.core.entity.KordEntity
import dev.jombi.kordsb.core.entity.User
import dev.jombi.kordsb.core.event.Event
import dev.jombi.kordsb.core.event.user.UserUpdateEvent
import kotlinx.coroutines.*

@KordPreview
public fun User.live(
    coroutineScope: CoroutineScope = kord + SupervisorJob(kord.coroutineContext.job)
): LiveUser = LiveUser(this, coroutineScope)

@KordPreview
public inline fun User.live(
    coroutineScope: CoroutineScope = kord + SupervisorJob(kord.coroutineContext.job),
    block: LiveUser.() -> Unit
): LiveUser = this.live(coroutineScope).apply(block)

@KordPreview
public fun LiveUser.onUpdate(scope: CoroutineScope = this, block: suspend (UserUpdateEvent) -> Unit): Job =
    on(scope = scope, consumer = block)

@KordPreview
public class LiveUser(
    user: User,
    coroutineScope: CoroutineScope = user.kord + SupervisorJob(user.kord.coroutineContext.job)
) : AbstractLiveKordEntity(user.kord, coroutineScope), KordEntity {

    override val id: Snowflake
        get() = user.id

    public var user: User = user
        private set

    override fun filter(event: Event): Boolean = when (event) {
        is UserUpdateEvent -> user.id == event.user.id
        else -> false
    }

    override fun update(event: Event): Unit = when (event) {
        is UserUpdateEvent -> user = event.user
        else -> Unit
    }

}
