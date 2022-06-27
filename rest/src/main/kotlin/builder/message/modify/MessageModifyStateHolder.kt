package dev.jombi.kordsb.rest.builder.message.modify

import dev.jombi.kordsb.common.annotation.KordPreview
import dev.jombi.kordsb.common.entity.DiscordAttachment
import dev.jombi.kordsb.common.entity.MessageFlags
import dev.jombi.kordsb.common.entity.optional.Optional
import dev.jombi.kordsb.rest.NamedFile
import dev.jombi.kordsb.rest.builder.component.MessageComponentBuilder
import dev.jombi.kordsb.rest.builder.message.AllowedMentionsBuilder
import dev.jombi.kordsb.rest.builder.message.EmbedBuilder
import java.io.InputStream

/**
 * Utility container for message modify builder. This class contains
 * all possible fields as optionals.
 */
internal class MessageModifyStateHolder {

    var files: Optional<MutableList<NamedFile>> = Optional.Missing()

    var content: Optional<String?> = Optional.Missing()

    var embeds: Optional<MutableList<EmbedBuilder>> = Optional.Missing()

    var flags: Optional<MessageFlags?> = Optional.Missing()

    var allowedMentions: Optional<AllowedMentionsBuilder> = Optional.Missing()

    var attachments: Optional<MutableList<DiscordAttachment>> = Optional.Missing()

    var components: Optional<MutableList<MessageComponentBuilder>> = Optional.Missing()

}
