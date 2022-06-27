package dev.jombi.kordsb.core.entity.channel

import dev.jombi.kordsb.common.entity.ChannelType
import dev.jombi.kordsb.common.entity.optional.optionalSnowflake
import dev.jombi.kordsb.core.cache.data.ChannelData
import equality.GuildChannelEqualityTest
import mockKord

@Suppress("DELEGATED_MEMBER_HIDES_SUPERTYPE_OVERRIDE")
internal class TextChannelTest : GuildChannelEqualityTest<TextChannel> by GuildChannelEqualityTest({ id, guildId ->
    val kord = mockKord()
    TextChannel(ChannelData(id, guildId = guildId.optionalSnowflake(), type = ChannelType.GuildNews), kord)
})