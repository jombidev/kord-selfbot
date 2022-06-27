package dev.jombi.kordsb.rest.builder.message.modify

import dev.jombi.kordsb.common.annotation.KordPreview
import dev.jombi.kordsb.common.entity.DiscordAttachment
import dev.jombi.kordsb.common.entity.MessageFlags
import dev.jombi.kordsb.common.entity.optional.delegate.delegate
import dev.jombi.kordsb.common.entity.optional.map
import dev.jombi.kordsb.common.entity.optional.mapList
import dev.jombi.kordsb.rest.NamedFile
import dev.jombi.kordsb.rest.builder.RequestBuilder
import dev.jombi.kordsb.rest.builder.component.MessageComponentBuilder
import dev.jombi.kordsb.rest.builder.message.AllowedMentionsBuilder
import dev.jombi.kordsb.rest.builder.message.EmbedBuilder
import dev.jombi.kordsb.rest.json.request.MessageEditPatchRequest
import dev.jombi.kordsb.rest.json.request.MultipartMessagePatchRequest

public class UserMessageModifyBuilder
    : MessageModifyBuilder,
    RequestBuilder<MultipartMessagePatchRequest> {

    private var state = MessageModifyStateHolder()

    override var files: MutableList<NamedFile>? by state::files.delegate()

    override var attachments: MutableList<DiscordAttachment>? by state::attachments.delegate()

    override var content: String? by state::content.delegate()

    override var embeds: MutableList<EmbedBuilder>? by state::embeds.delegate()

    public var flags: MessageFlags? by state::flags.delegate()

    override var allowedMentions: AllowedMentionsBuilder? by state::allowedMentions.delegate()


    override var components: MutableList<MessageComponentBuilder>? by state::components.delegate()

    @OptIn(KordPreview::class)
    override fun toRequest(): MultipartMessagePatchRequest = MultipartMessagePatchRequest(
        MessageEditPatchRequest(
            content = state.content,
            embeds = state.embeds.mapList { it.toRequest() },
            flags = state.flags,
            allowedMentions = state.allowedMentions.map { it.build() },
            components = state.components.mapList { it.build() },
            attachments = state.attachments
        ),
        state.files,
    )

}
