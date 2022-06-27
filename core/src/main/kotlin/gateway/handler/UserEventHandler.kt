package dev.jombi.kordsb.core.gateway.handler

import dev.kord.cache.api.DataCache
import dev.kord.cache.api.put
import dev.kord.cache.api.query
import dev.jombi.kordsb.core.Kord
import dev.jombi.kordsb.core.cache.data.UserData
import dev.jombi.kordsb.core.cache.idEq
import dev.jombi.kordsb.core.entity.User
import dev.jombi.kordsb.core.event.user.UserUpdateEvent
import dev.jombi.kordsb.gateway.Event
import dev.jombi.kordsb.gateway.UserUpdate
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.singleOrNull
import dev.jombi.kordsb.core.event.Event as CoreEvent

internal class UserEventHandler(
    cache: DataCache
) : BaseGatewayEventHandler(cache) {

    override suspend fun handle(event: Event, kord: Kord, coroutineScope: CoroutineScope): CoreEvent? = when (event) {
        is UserUpdate -> handle(event, kord, coroutineScope)
        else -> null
    }

    private suspend fun handle(event: UserUpdate, kord: Kord, coroutineScope: CoroutineScope): UserUpdateEvent {
        val data = UserData.from(event.user)

        val old = cache.query<UserData> { idEq(UserData::id, data.id) }
            .asFlow().map { User(it, kord) }.singleOrNull()

        cache.put(data)
        val new = User(data, kord)

        return UserUpdateEvent(old, new, coroutineScope)
    }

}
