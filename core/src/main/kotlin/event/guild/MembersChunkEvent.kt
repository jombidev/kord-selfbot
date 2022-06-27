package dev.jombi.kordsb.core.event.guild

import dev.jombi.kordsb.common.annotation.DeprecatedSinceKord
import dev.jombi.kordsb.common.entity.Snowflake
import dev.jombi.kordsb.common.entity.optional.orEmpty
import dev.jombi.kordsb.core.Kord
import dev.jombi.kordsb.core.behavior.GuildBehavior
import dev.jombi.kordsb.core.cache.data.MembersChunkData
import dev.jombi.kordsb.core.entity.Guild
import dev.jombi.kordsb.core.entity.Member
import dev.jombi.kordsb.core.entity.Presence
import dev.jombi.kordsb.core.entity.Strategizable
import dev.jombi.kordsb.core.event.Event
import dev.jombi.kordsb.core.event.kordCoroutineScope
import dev.jombi.kordsb.core.supplier.EntitySupplier
import dev.jombi.kordsb.core.supplier.EntitySupplyStrategy
import kotlinx.coroutines.CoroutineScope
import kotlin.coroutines.CoroutineContext


@DeprecatedSinceKord("0.7.0")
@Deprecated("Renamed to MembersChunkEvent", ReplaceWith("MembersChunkEvent"), DeprecationLevel.ERROR)
public typealias MemberChunksEvent = MembersChunkEvent

public class MembersChunkEvent(
    public val data: MembersChunkData,
    override val kord: Kord,
    override val supplier: EntitySupplier = kord.defaultSupplier,
    public val coroutineScope: CoroutineScope = kordCoroutineScope(kord)
) : Event, CoroutineScope by coroutineScope, Strategizable {

    public val guildId: Snowflake get() = data.guildId

    public val guild: GuildBehavior get() = GuildBehavior(guildId, kord)

    public val members: Set<Member>
        get() = data.members.zip(data.users)
            .map { (member, user) -> Member(member, user, kord) }
            .toSet()

    public val chunkIndex: Int get() = data.chunkIndex

    public val chunkCount: Int get() = data.chunkCount

    public val invalidIds: Set<Snowflake> get() = data.notFound.orEmpty()

    public val presences: List<Presence> get() = data.presences.orEmpty().map { Presence(it, kord) }

    public val nonce: String? get() = data.nonce.value

    public suspend fun getGuild(): Guild = supplier.getGuild(guildId)

    public suspend fun getGuildOrNull(): Guild? = supplier.getGuildOrNull(guildId)

    override fun withStrategy(strategy: EntitySupplyStrategy<*>): MembersChunkEvent =
        MembersChunkEvent(data, kord, strategy.supply(kord))

    override fun toString(): String {
        return "MemberChunksEvent(guildId=$guildId, members=$members, kord=$kord, supplier=$supplier)"
    }
}
