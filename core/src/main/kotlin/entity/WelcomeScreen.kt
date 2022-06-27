package dev.jombi.kordsb.core.entity

import dev.jombi.kordsb.core.Kord
import dev.jombi.kordsb.core.KordObject
import dev.jombi.kordsb.core.cache.data.WelcomeScreenData
import dev.jombi.kordsb.core.entity.channel.WelcomeScreenChannel

/**
 * Shown to new members in community guild, returned when in the invite object.
 *
 * @property description the server description shown in the welcome screen.
 * @property welcomeScreenChannels  The channels shown in the welcome screen.
 */
public class WelcomeScreen(public val data: WelcomeScreenData, override val kord: Kord) : KordObject {

    public val description: String? get() = data.description

    public val welcomeScreenChannels: List<WelcomeScreenChannel>
        get() = data.welcomeChannels.map { WelcomeScreenChannel(it, kord) }

}
