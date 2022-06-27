package dev.jombi.kordsb.core.entity.component

import dev.jombi.kordsb.core.cache.data.ComponentData

/**
 * A component type unknown to Kord.
 */

public class UnknownComponent(override val data: ComponentData) : Component {

    override fun toString(): String = "UnknownComponent(data=$data)"

}
