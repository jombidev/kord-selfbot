package dev.jombi.kordsb.rest.service

import dev.jombi.kordsb.common.entity.DiscordApplication
import dev.jombi.kordsb.rest.request.RequestHandler
import dev.jombi.kordsb.rest.route.Route

public class ApplicationService(handler: RequestHandler) : RestService(handler) {

    public suspend fun getCurrentApplicationInfo(): DiscordApplication = call(Route.CurrentApplicationInfo)
}
