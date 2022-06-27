/*
@file:JvmName("VoiceBaseVoiceChannelBehavior")

package dev.jombi.kordsb.core.behavior.channel

import dev.jombi.kordsb.common.annotation.KordVoice
import dev.jombi.kordsb.core.entity.channel.VoiceChannel
import dev.jombi.kordsb.core.exception.GatewayNotFoundException
import dev.jombi.kordsb.voice.VoiceConnection
import dev.jombi.kordsb.voice.VoiceConnectionBuilder

*/
/**
 * Connect to this [VoiceChannel] and create a [VoiceConnection] for this voice session.
 *
 * @param builder a builder for the [VoiceConnection].
 * @throws GatewayNotFoundException when there is no associated [dev.jombi.kordsb.gateway.Gateway] for the [dev.jombi.kordsb.core.entity.Guild] this channel is in.
 * @throws dev.jombi.kordsb.voice.exception.VoiceConnectionInitializationException when there was a problem retrieving voice information from Discord.
 * @return a [VoiceConnection] representing the connection to this [VoiceConnection].
 *//*

@KordVoice
suspend fun BaseVoiceChannelBehavior.connect(builder: VoiceConnectionBuilder.() -> Unit): VoiceConnection {
    val voiceConnection = VoiceConnection(
        guild.gateway ?: GatewayNotFoundException.voiceConnectionGatewayNotFound(guildId),
        kord.selfId,
        id,
        guildId,
        builder
    )

    voiceConnection.connect()

    return voiceConnection
}
*/
