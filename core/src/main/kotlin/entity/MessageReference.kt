package dev.jombi.kordsb.core.entity

import dev.jombi.kordsb.core.Kord
import dev.jombi.kordsb.core.KordObject
import dev.jombi.kordsb.core.behavior.GuildBehavior
import dev.jombi.kordsb.core.behavior.MessageBehavior
import dev.jombi.kordsb.core.behavior.channel.MessageChannelBehavior
import dev.jombi.kordsb.core.cache.data.MessageReferenceData

public class MessageReference(public val data: MessageReferenceData, override val kord: Kord) : KordObject {

    public val guild: GuildBehavior? get() = data.guildId.value?.let { GuildBehavior(it, kord) }

    public val channel: MessageChannelBehavior get() = MessageChannelBehavior(data.channelId.value!!, kord)

    public val message: MessageBehavior? get() = data.id.value?.let { MessageBehavior(channel.id, it, kord) }


}
