@file:Suppress("PropertyName")

package dev.jombi.kordsb.rest.builder.component

import dev.jombi.kordsb.common.annotation.KordDsl
import dev.jombi.kordsb.common.entity.DiscordComponent
import dev.jombi.kordsb.common.entity.optional.OptionalBoolean
import dev.jombi.kordsb.common.entity.optional.delegate.delegate

@KordDsl
public sealed interface ComponentBuilder {
    public fun build(): DiscordComponent
}

@KordDsl
public sealed class ActionRowComponentBuilder : ComponentBuilder {

    protected var _disabled: OptionalBoolean = OptionalBoolean.Missing
        private set

    /** Whether the component is disabled. Defaults to `false`. */
    public var disabled: Boolean? by ::_disabled.delegate()
}

@KordDsl
public sealed interface MessageComponentBuilder : ComponentBuilder
