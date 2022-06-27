package dev.jombi.kordsb.core.entity.channel

import dev.jombi.kordsb.common.entity.Snowflake
import dev.jombi.kordsb.core.Kord
import dev.jombi.kordsb.core.behavior.channel.ChannelBehavior
import dev.jombi.kordsb.core.cache.data.WelcomeScreenChannelData
import dev.jombi.kordsb.core.supplier.EntitySupplier

/**
 * One of the channels shown in the welcome screen
 *
 * @property id the id of the channel.
 * @property description the description shown for the channel.
 * @property emojiId the emoji id if the emoji is custom.
 * @property emojiName the emoji name if custom, the unicode character if standard, or `null` if no emoji is set.
 */
public class WelcomeScreenChannel(
    public val data: WelcomeScreenChannelData,
    override val kord: Kord,
    override val supplier: EntitySupplier = kord.defaultSupplier,
) : ChannelBehavior {

    override val id: Snowflake
        get() = data.channelId

    public val description: String get() = data.description

    public val emojiId: Snowflake? get() = data.emojiId

    public val emojiName: String? get() = data.emojiName

}
