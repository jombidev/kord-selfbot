package dev.jombi.kordsb.voice.encryption.strategies

import dev.jombi.kordsb.voice.io.ByteArrayView
import dev.jombi.kordsb.voice.io.MutableByteArrayCursor
import dev.jombi.kordsb.voice.udp.RTPPacket

/**
 * An [encryption mode, regarding the nonce](https://discord.com/developers/docs/topics/voice-connections#establishing-a-voice-udp-connection-encryption-modes), supported by Discord.
 */
public sealed interface NonceStrategy {
    /**
     * The amount of bytes this nonce will take up.
     */
    public val nonceLength: Int

    /**
     * Reads the nonce from this [packet] (also removes it if it resides in the payload), and returns a [ByteArrayView] of it.
     */
    public fun strip(packet: RTPPacket): ByteArrayView

    /**
     * Generates a nonce, may use the provided information.
     */
    public fun generate(header: () -> ByteArrayView): ByteArrayView

    /**
     * Writes the [nonce] to [cursor] in the correct relative position.
     */
    public fun append(nonce: ByteArrayView, cursor: MutableByteArrayCursor)
}