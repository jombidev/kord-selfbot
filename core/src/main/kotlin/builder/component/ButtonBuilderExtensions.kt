package dev.jombi.kordsb.core.builder.components

import dev.jombi.kordsb.common.entity.DiscordPartialEmoji
import dev.jombi.kordsb.common.entity.optional.optional
import dev.jombi.kordsb.core.entity.GuildEmoji
import dev.jombi.kordsb.core.entity.ReactionEmoji
import dev.jombi.kordsb.rest.builder.component.ButtonBuilder


public fun ButtonBuilder.emoji(emoji: ReactionEmoji.Unicode) {
    this.emoji = DiscordPartialEmoji(name = emoji.name, id = null)
}

public fun ButtonBuilder.emoji(emoji: ReactionEmoji.Custom) {
    this.emoji = DiscordPartialEmoji(name = emoji.name, id = emoji.id, animated = emoji.isAnimated.optional())
}

public fun ButtonBuilder.emoji(emoji: GuildEmoji) {
    this.emoji = DiscordPartialEmoji(id = emoji.id, name = null, animated = emoji.isAnimated.optional())
}
