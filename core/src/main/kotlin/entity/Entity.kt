package dev.jombi.kordsb.core.entity

import dev.jombi.kordsb.common.entity.Snowflake
import dev.jombi.kordsb.core.KordObject
import java.util.Comparator

public interface Entity : Comparable<Entity> {
    /**
     * The unique identifier of this entity.
     */
    public val id: Snowflake

    /**
     * Compares entities on [id].
     */
    override operator fun compareTo(other: Entity): Int = comparator.compare(this, other)

    public companion object {
        public val comparator: Comparator<Entity> = compareBy(Entity::id)
    }
}

/**
 * An object that is identified by its [id].
 * This object holds a [KordObject]
 */
public interface KordEntity : KordObject, Entity
