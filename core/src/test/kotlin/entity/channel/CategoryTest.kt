package dev.jombi.kordsb.core.entity.channel

import dev.jombi.kordsb.common.entity.ChannelType
import dev.jombi.kordsb.common.entity.optional.optionalSnowflake
import dev.jombi.kordsb.core.cache.data.ChannelData
import equality.GuildChannelEqualityTest
import mockKord

@Suppress("DELEGATED_MEMBER_HIDES_SUPERTYPE_OVERRIDE")
internal class CategoryTest : GuildChannelEqualityTest<Category> by GuildChannelEqualityTest ({ id, guildId ->
    val kord = mockKord()
    Category(ChannelData(id, guildId = guildId.optionalSnowflake(), type = ChannelType.GuildCategory), kord)
}) {

}