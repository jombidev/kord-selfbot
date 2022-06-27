package dev.jombi.kordsb.core.entity.interaction

import dev.jombi.kordsb.common.entity.Permissions
import dev.jombi.kordsb.common.entity.Snowflake
import dev.jombi.kordsb.core.behavior.interaction.GuildInteractionBehavior
import dev.jombi.kordsb.core.entity.Guild
import dev.jombi.kordsb.core.entity.Member
import dev.jombi.kordsb.core.supplier.EntitySupplyStrategy

/** An [Interaction] that took place in the context of a [Guild]. */
public sealed interface GuildInteraction : Interaction, GuildInteractionBehavior {

    override val guildId: Snowflake get() = data.guildId.value!!

    /** Overridden permissions of the interaction [invoker][user] in the [channel][GuildInteractionBehavior.channel]. */
    public val permissions: Permissions get() = data.permissions.value!!

    /** The invoker of the interaction as a [Member]. */
    override val user: Member get() = Member(data.member.value!!, data.user.value!!, kord)

    override fun withStrategy(strategy: EntitySupplyStrategy<*>): GuildInteraction
}
