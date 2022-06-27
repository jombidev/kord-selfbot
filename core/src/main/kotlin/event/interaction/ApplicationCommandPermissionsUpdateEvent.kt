package dev.jombi.kordsb.core.event.interaction

import dev.jombi.kordsb.core.Kord
import dev.jombi.kordsb.core.entity.application.ApplicationCommandPermissions
import dev.jombi.kordsb.core.event.Event
import dev.jombi.kordsb.core.event.kordCoroutineScope
import kotlinx.coroutines.CoroutineScope

public class ApplicationCommandPermissionsUpdateEvent(
    public val permissions: ApplicationCommandPermissions,
    override val kord: Kord,
    public val coroutineScope: CoroutineScope = kordCoroutineScope(kord),
) : Event, CoroutineScope by coroutineScope
