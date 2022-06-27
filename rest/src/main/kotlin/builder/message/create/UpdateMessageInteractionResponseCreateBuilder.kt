package dev.jombi.kordsb.rest.builder.message.create

import dev.jombi.kordsb.common.entity.InteractionResponseType
import dev.jombi.kordsb.common.entity.optional.*
import dev.jombi.kordsb.rest.NamedFile
import dev.jombi.kordsb.rest.builder.RequestBuilder
import dev.jombi.kordsb.rest.builder.component.MessageComponentBuilder
import dev.jombi.kordsb.rest.builder.message.AllowedMentionsBuilder
import dev.jombi.kordsb.rest.builder.message.EmbedBuilder
import dev.jombi.kordsb.rest.json.request.InteractionApplicationCommandCallbackData
import dev.jombi.kordsb.rest.json.request.InteractionResponseCreateRequest
import dev.jombi.kordsb.rest.json.request.MultipartInteractionResponseCreateRequest


public class UpdateMessageInteractionResponseCreateBuilder :
    MessageCreateBuilder,
    RequestBuilder<MultipartInteractionResponseCreateRequest> {


    override var files: MutableList<NamedFile> = mutableListOf()

    override var content: String? = null

    override var tts: Boolean? = null

    override var embeds: MutableList<EmbedBuilder> = mutableListOf()

    override var allowedMentions: AllowedMentionsBuilder? = null

    override var components: MutableList<MessageComponentBuilder> = mutableListOf()

    override fun toRequest(): MultipartInteractionResponseCreateRequest {
        return MultipartInteractionResponseCreateRequest(
            InteractionResponseCreateRequest(
                InteractionResponseType.UpdateMessage,
                InteractionApplicationCommandCallbackData(
                    content = Optional(content).coerceToMissing(),
                    embeds = Optional(embeds).mapList { it.toRequest() },
                    allowedMentions = Optional(allowedMentions).map { it.build() },
                    components = Optional(components).mapList { it.build() },
                    tts = Optional(tts).coerceToMissing().toPrimitive(),
                ).optional()
            ),
            Optional(files)
        )

    }
}
