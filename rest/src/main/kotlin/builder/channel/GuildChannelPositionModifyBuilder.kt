package dev.jombi.kordsb.rest.builder.channel

import dev.jombi.kordsb.common.annotation.KordDsl
import dev.jombi.kordsb.common.annotation.KordExperimental
import dev.jombi.kordsb.common.entity.Snowflake
import dev.jombi.kordsb.common.entity.optional.OptionalInt
import dev.jombi.kordsb.common.entity.optional.delegate.delegate
import dev.jombi.kordsb.rest.builder.AuditRequestBuilder
import dev.jombi.kordsb.rest.json.request.ChannelPositionSwapRequest
import dev.jombi.kordsb.rest.json.request.GuildChannelPositionModifyRequest
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

@KordDsl
public class GuildChannelPositionModifyBuilder : AuditRequestBuilder<GuildChannelPositionModifyRequest> {
    override var reason: String? = null
    public var swaps: MutableList<GuildChannelSwapBuilder> = mutableListOf()

    public fun move(pair: Pair<Snowflake, Int>) {
        move(pair.first) { position = pair.second }
    }

    public fun move(vararg pairs: Pair<Snowflake, Int>) {
        pairs.forEach { move(it) }
    }

    public inline fun move(channel: Snowflake, builder: GuildChannelSwapBuilder.() -> Unit) {
        contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
        swaps.firstOrNull { it.channelId == channel }?.builder() ?: run {
            swaps.add(GuildChannelSwapBuilder(channel).also(builder))
        }
    }

    override fun toRequest(): GuildChannelPositionModifyRequest =
        GuildChannelPositionModifyRequest(swaps.map { it.toRequest() })
}


public class GuildChannelSwapBuilder(public var channelId: Snowflake) {


    private var _position: OptionalInt? = OptionalInt.Missing

    /**
     * The new position of this channel
     */
    public var position: Int? by ::_position.delegate()

    /**
     * The new parent of this channel, has to be a category.
     *
     * **Only one channel can have a parent id modified per request**.
     *
     * This field is not officially supported by the Discord API, and might change/be removed in the future.
     */
    @KordExperimental
    public var parentId: Snowflake? = null

    /**
     * Locks the permissions of this channel to the new category it is moved to.
     * Only works if [parentId] is set.
     *
     * This field is not officially supported by the Discord API, and might change/be removed in the future.
     */
    @KordExperimental
    public var lockPermissionsToParent: Boolean? = null

    @OptIn(KordExperimental::class)
    public fun toRequest(): ChannelPositionSwapRequest = ChannelPositionSwapRequest(
        channelId, _position, lockPermissionsToParent, parentId
    )

}
