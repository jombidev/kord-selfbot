package dev.jombi.kordsb.core.entity.channel

import dev.jombi.kordsb.common.entity.ChannelType
import dev.jombi.kordsb.core.cache.data.ChannelData
import equality.ChannelEqualityTest
import mockKord

internal class DmChannelTest: ChannelEqualityTest<DmChannel> by ChannelEqualityTest ({ id ->
    val kord = mockKord()
    DmChannel(ChannelData(id, type = ChannelType.DM), kord)
})