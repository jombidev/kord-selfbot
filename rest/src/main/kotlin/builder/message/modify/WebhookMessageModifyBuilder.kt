package dev.jombi.kordsb.rest.builder.message.modify

import dev.jombi.kordsb.common.entity.DiscordAttachment
import dev.jombi.kordsb.common.entity.optional.delegate.delegate
import dev.jombi.kordsb.common.entity.optional.map
import dev.jombi.kordsb.common.entity.optional.mapList
import dev.jombi.kordsb.rest.NamedFile
import dev.jombi.kordsb.rest.builder.RequestBuilder
import dev.jombi.kordsb.rest.builder.component.MessageComponentBuilder
import dev.jombi.kordsb.rest.builder.message.AllowedMentionsBuilder
import dev.jombi.kordsb.rest.builder.message.EmbedBuilder
import dev.jombi.kordsb.rest.json.request.MultipartWebhookEditMessageRequest
import dev.jombi.kordsb.rest.json.request.WebhookEditMessageRequest

public class WebhookMessageModifyBuilder :
    MessageModifyBuilder,
    RequestBuilder<MultipartWebhookEditMessageRequest> {

    private var state = MessageModifyStateHolder()

    override var files: MutableList<NamedFile>? by state::files.delegate()

    override var attachments: MutableList<DiscordAttachment>? by state::attachments.delegate()

    override var content: String? by state::content.delegate()

    override var embeds: MutableList<EmbedBuilder>? by state::embeds.delegate()

    override var allowedMentions: AllowedMentionsBuilder? by state::allowedMentions.delegate()


    override var components: MutableList<MessageComponentBuilder>? by state::components.delegate()

    override fun toRequest(): MultipartWebhookEditMessageRequest {
        return MultipartWebhookEditMessageRequest(
            WebhookEditMessageRequest(
                content = state.content,
                embeds = state.embeds.mapList { it.toRequest() },
                allowedMentions = state.allowedMentions.map { it.build() },
                components = state.components.mapList { it.build() },
                attachments = state.attachments
            ),
            files = state.files
        )
    }

}
