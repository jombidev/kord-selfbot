package dev.jombi.kordsb.rest.builder.message.create

import dev.jombi.kordsb.common.annotation.KordPreview
import dev.jombi.kordsb.common.entity.DiscordMessageReference
import dev.jombi.kordsb.common.entity.Snowflake
import dev.jombi.kordsb.common.entity.optional.*
import dev.jombi.kordsb.rest.NamedFile
import dev.jombi.kordsb.rest.builder.RequestBuilder
import dev.jombi.kordsb.rest.builder.component.MessageComponentBuilder
import dev.jombi.kordsb.rest.builder.message.AllowedMentionsBuilder
import dev.jombi.kordsb.rest.builder.message.EmbedBuilder
import dev.jombi.kordsb.rest.json.request.MessageCreateRequest
import dev.jombi.kordsb.rest.json.request.MultipartMessageCreateRequest

/**
 * Message builder for creating messages as a bot user.
 */
public class UserMessageCreateBuilder
    : MessageCreateBuilder,
    RequestBuilder<MultipartMessageCreateRequest> {

    override var content: String? = null

    /**
     * An identifier that can be used to validate the message was sent.
     */
    public var nonce: String? = null

    override var tts: Boolean? = null

    override val embeds: MutableList<EmbedBuilder> = mutableListOf()

    override var allowedMentions: AllowedMentionsBuilder? = null


    override val components: MutableList<MessageComponentBuilder> = mutableListOf()

    /**
     * The id of the message being replied to.
     * Requires the [ReadMessageHistory][dev.jombi.kordsb.common.entity.Permission.ReadMessageHistory] permission.
     *
     * Replying will not mention the author by default,
     * set [AllowedMentionsBuilder.repliedUser] to `true` via [allowedMentions]  to mention the author.
     */
    public var messageReference: Snowflake? = null

    /**
     * whether to error if the referenced message doesn't exist instead of sending as a normal (non-reply) message,
     * defaults to true.
     */
    public var failIfNotExists: Boolean? = null

    override val files: MutableList<NamedFile> = mutableListOf()

    @OptIn(KordPreview::class)
    override fun toRequest(): MultipartMessageCreateRequest {
        return MultipartMessageCreateRequest(
            MessageCreateRequest(
                content = Optional(content).coerceToMissing(),
                nonce = Optional(nonce).coerceToMissing(),
                tts = Optional(tts).coerceToMissing().toPrimitive(),
                embeds = Optional(embeds).mapList { it.toRequest() },
                allowedMentions = Optional(allowedMentions).coerceToMissing().map { it.build() },
                messageReference = messageReference?.let {
                    Optional(
                        DiscordMessageReference(
                            OptionalSnowflake.Value(it),
                            failIfNotExists = Optional(failIfNotExists).coerceToMissing().toPrimitive()
                        )
                    )
                } ?: Optional.Missing(),
                components = Optional(components).coerceToMissing().mapList { it.build() }
            ),
            files
        )
    }

}
