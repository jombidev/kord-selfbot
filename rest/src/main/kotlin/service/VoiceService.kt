package dev.jombi.kordsb.rest.service

import dev.jombi.kordsb.common.entity.DiscordVoiceRegion
import dev.jombi.kordsb.rest.request.RequestHandler
import dev.jombi.kordsb.rest.route.Route

public class VoiceService(requestHandler: RequestHandler) : RestService(requestHandler) {

    public suspend fun getVoiceRegions(): List<DiscordVoiceRegion> = call(Route.VoiceRegionsGet)
}
