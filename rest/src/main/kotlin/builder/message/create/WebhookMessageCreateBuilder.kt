package dev.jombi.kordsb.rest.builder.message.create

import dev.jombi.kordsb.common.entity.optional.*
import dev.jombi.kordsb.rest.NamedFile
import dev.jombi.kordsb.rest.builder.RequestBuilder
import dev.jombi.kordsb.rest.builder.component.MessageComponentBuilder
import dev.jombi.kordsb.rest.builder.message.AllowedMentionsBuilder
import dev.jombi.kordsb.rest.builder.message.EmbedBuilder
import dev.jombi.kordsb.rest.json.request.MultiPartWebhookExecuteRequest
import dev.jombi.kordsb.rest.json.request.WebhookExecuteRequest

/**
 * Message builder for creating messages as a webhook user.
 */
public class WebhookMessageCreateBuilder :
    MessageCreateBuilder,
    RequestBuilder<MultiPartWebhookExecuteRequest> {

    override var content: String? = null

    public var username: String? = null

    public var avatarUrl: String? = null

    override var tts: Boolean? = null

    override val embeds: MutableList<EmbedBuilder> = mutableListOf()

    override var allowedMentions: AllowedMentionsBuilder? = null


    override val components: MutableList<MessageComponentBuilder> = mutableListOf()

    override val files: MutableList<NamedFile> = mutableListOf()

    override fun toRequest(): MultiPartWebhookExecuteRequest {
        return MultiPartWebhookExecuteRequest(
            WebhookExecuteRequest(
                content = Optional(content).coerceToMissing(),
                username = Optional(username).coerceToMissing(),
                avatar = Optional(avatarUrl).coerceToMissing(),
                tts = Optional(tts).coerceToMissing().toPrimitive(),
                embeds = Optional(embeds).mapList { it.toRequest() },
                allowedMentions = Optional(allowedMentions).coerceToMissing().map { it.build() },
                components = Optional(components).coerceToMissing().mapList { it.build() }
            ),
            files
        )
    }
}
