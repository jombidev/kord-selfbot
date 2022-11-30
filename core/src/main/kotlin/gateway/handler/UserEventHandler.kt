package dev.jombi.kordsb.core.gateway.handler

import dev.jombi.kordsb.core.Kord
import dev.jombi.kordsb.core.cache.data.UserData
import dev.jombi.kordsb.core.cache.idEq
import dev.jombi.kordsb.core.entity.User
import dev.jombi.kordsb.core.event.user.UserUpdateEvent
import dev.jombi.kordsb.gateway.Event
import dev.jombi.kordsb.gateway.UserUpdate
import dev.kord.cache.api.put
import dev.kord.cache.api.query
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.singleOrNull
import dev.jombi.kordsb.core.event.Event as CoreEvent

internal class UserEventHandler : BaseGatewayEventHandler() {

    override suspend fun handle(event: Event, kord: Kord, context: LazyContext?): CoreEvent? = when (event) {
        is UserUpdate -> handle(event, kord, context)
        else -> null
    }

    private suspend fun handle(event: UserUpdate, kord: Kord, context: LazyContext?): UserUpdateEvent {
        val data = UserData.from(event.user)

        val old = kord.cache.query<UserData> { idEq(UserData::id, data.id) }
            .asFlow().map { User(it, kord) }.singleOrNull()

        kord.cache.put(data)
        val new = User(data, kord)

        return UserUpdateEvent(old, new, context?.get())
    }

}
