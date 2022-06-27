package dev.jombi.kordsb.core.entity.component

import dev.jombi.kordsb.common.entity.ComponentType
import dev.jombi.kordsb.core.cache.data.ChatComponentData
import dev.jombi.kordsb.core.cache.data.ComponentData
import dev.jombi.kordsb.core.cache.data.TextInputComponentData
import dev.jombi.kordsb.core.entity.Message

/**
 * An interactive element inside a [Message].
 */

public sealed interface Component {

    /**
     * The type of component.
     * @see ButtonComponent
     * @see ActionRowComponent
     * @see SelectMenuComponent
     * @see UnknownComponent
     */
    public val type: ComponentType get() = data.type

    public val data: ComponentData
}

/**
 * Creates a [Component] from the [data].
 * @see ActionRowComponent
 * @see ButtonComponent
 * @see SelectMenuComponent
 * @see UnknownComponent
 */
public fun Component(data: ComponentData): Component = when (data.type) {
    ComponentType.ActionRow -> ActionRowComponent(data)
    ComponentType.Button -> ButtonComponent(data as ChatComponentData)
    ComponentType.SelectMenu -> SelectMenuComponent(data)
    ComponentType.TextInput -> TextInputComponent(data as TextInputComponentData)
    is ComponentType.Unknown -> UnknownComponent(data)
}
