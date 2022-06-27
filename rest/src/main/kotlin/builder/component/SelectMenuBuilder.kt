package dev.jombi.kordsb.rest.builder.component

import dev.jombi.kordsb.common.annotation.KordDsl
import dev.jombi.kordsb.common.entity.ComponentType
import dev.jombi.kordsb.common.entity.DiscordChatComponent
import dev.jombi.kordsb.common.entity.optional.Optional
import dev.jombi.kordsb.common.entity.optional.OptionalInt
import dev.jombi.kordsb.common.entity.optional.delegate.delegate
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

/**
 * A builder for a
 * [Discord Select Menu](https://discord.com/developers/docs/interactions/message-components#select-menus).
 *
 * @param customId The identifier for the menu, max 100 characters.
 */
@KordDsl
public class SelectMenuBuilder(
    public var customId: String,
) : ActionRowComponentBuilder() {

    /**
     * The choices in the select, max 25.
     */
    public val options: MutableList<SelectOptionBuilder> = mutableListOf()

    /**
     * The range of values that can be accepted. Accepts any range between [0,25].
     * Defaults to `1..1`.
     */
    public var allowedValues: ClosedRange<Int> = 1..1


    private var _placeholder: Optional<String> = Optional.Missing()

    /**
     * Custom placeholder if no value is selected, max 150 characters.
     *
     * [Option defaults][SelectOptionBuilder.default] have priority over placeholders,
     * if any option is marked as default then that label will be shown instead.
     */
    public var placeholder: String? by ::_placeholder.delegate()

    /**
     * Adds a new option to the select menu with the given [label] and [value] and configured by the [builder].
     *
     * @param label The user-facing name of the option, max 100 characters.
     * @param value The dev-defined value of the option, max 100 characters.
     */
    public inline fun option(label: String, value: String, builder: SelectOptionBuilder.() -> Unit = {}) {
        contract {
            callsInPlace(builder, InvocationKind.EXACTLY_ONCE)
        }

        options.add(SelectOptionBuilder(label = label, value = value).apply(builder))
    }

    override fun build(): DiscordChatComponent {
        return DiscordChatComponent(
            ComponentType.SelectMenu,
            customId = Optional(customId),
            disabled = _disabled,
            placeholder = _placeholder,
            minValues = OptionalInt.Value(allowedValues.start),
            maxValues = OptionalInt.Value(allowedValues.endInclusive),
            options = Optional(options.map { it.build() })
        )
    }

}
