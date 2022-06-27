package dev.jombi.kordsb.rest.json.response

import dev.jombi.kordsb.common.entity.DiscordApplication

@Deprecated(
    "'ApplicationInfoResponse' was moved to common and renamed to 'DiscordApplication'.",
    ReplaceWith("DiscordApplication", "dev.jombi.kordsb.common.entity.DiscordApplication"),
    DeprecationLevel.ERROR,
)
public typealias ApplicationInfoResponse = DiscordApplication
