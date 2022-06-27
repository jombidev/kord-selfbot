package dev.jombi.kordsb.core.behavior.interaction

import dev.jombi.kordsb.common.entity.Snowflake
import dev.jombi.kordsb.core.Kord
import dev.jombi.kordsb.core.behavior.GuildBehavior
import dev.jombi.kordsb.core.behavior.channel.GuildMessageChannelBehavior
import dev.jombi.kordsb.core.entity.Guild
import dev.jombi.kordsb.core.entity.channel.GuildMessageChannel
import dev.jombi.kordsb.core.entity.channel.MessageChannel
import dev.jombi.kordsb.core.entity.interaction.GuildInteraction
import dev.jombi.kordsb.core.supplier.EntitySupplier
import dev.jombi.kordsb.core.supplier.EntitySupplyStrategy
import dev.jombi.kordsb.core.supplier.getChannelOf
import dev.jombi.kordsb.core.supplier.getChannelOfOrNull

/** The behavior of a [GuildInteraction]. */
public interface GuildInteractionBehavior : InteractionBehavior {

    /** The id of the guild the interaction was sent from. */
    public val guildId: Snowflake

    /** The behavior of the guild the interaction was sent from. */
    public val guild: GuildBehavior get() = GuildBehavior(guildId, kord)

    /**
     * The [GuildBehavior] for the guild the command was executed in.
     */
    @Deprecated("Renamed to 'guild'.", ReplaceWith("this.guild"), DeprecationLevel.ERROR)
    public val guildBehavior: GuildBehavior get() = guild

    override val channel: GuildMessageChannelBehavior
        get() = GuildMessageChannelBehavior(guildId, channelId, kord)

    public suspend fun getGuildOrNull(): Guild? = supplier.getGuildOrNull(guildId)

    public suspend fun getGuild(): Guild = supplier.getGuild(guildId)

    override suspend fun getChannel(): GuildMessageChannel = supplier.getChannelOf(channelId)

    override suspend fun getChannelOrNull(): GuildMessageChannel? = supplier.getChannelOfOrNull(channelId)

    override fun withStrategy(strategy: EntitySupplyStrategy<*>): GuildInteractionBehavior =
        GuildInteractionBehavior(guildId, id, channelId, applicationId, token, kord, supplier)
}

public fun GuildInteractionBehavior(
    guildId: Snowflake,
    id: Snowflake,
    channelId: Snowflake,
    applicationId: Snowflake,
    token: String,
    kord: Kord,
    supplier: EntitySupplier = kord.defaultSupplier
): GuildInteractionBehavior = object : GuildInteractionBehavior {
    override val guildId: Snowflake = guildId
    override val id: Snowflake = id
    override val channelId: Snowflake = channelId
    override val applicationId: Snowflake = applicationId
    override val token: String = token
    override val kord: Kord = kord
    override val supplier: EntitySupplier = supplier
}
