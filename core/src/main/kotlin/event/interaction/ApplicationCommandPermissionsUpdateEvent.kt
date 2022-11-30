package dev.jombi.kordsb.core.event.interaction

import dev.jombi.kordsb.core.Kord
import dev.jombi.kordsb.core.entity.application.ApplicationCommandPermissions
import dev.jombi.kordsb.core.event.Event

public class ApplicationCommandPermissionsUpdateEvent(
    public val permissions: ApplicationCommandPermissions,
    override val kord: Kord,
    override val customContext: Any?,
) : Event
