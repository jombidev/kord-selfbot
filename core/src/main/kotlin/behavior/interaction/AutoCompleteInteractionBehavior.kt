package dev.jombi.kordsb.core.behavior.interaction

import dev.jombi.kordsb.common.entity.Choice
import dev.jombi.kordsb.common.entity.DiscordAutoComplete
import dev.jombi.kordsb.core.entity.interaction.AutoCompleteInteraction
import dev.jombi.kordsb.core.supplier.EntitySupplyStrategy
import dev.jombi.kordsb.rest.builder.interaction.IntegerOptionBuilder
import dev.jombi.kordsb.rest.builder.interaction.NumberOptionBuilder
import dev.jombi.kordsb.rest.builder.interaction.StringChoiceBuilder
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

/**
 * The behavior of an [AutoCompleteInteraction].
 *
 * @see suggestString
 * @see suggestInt
 * @see suggestNumber
 * @see suggest
 */
public interface AutoCompleteInteractionBehavior : DataInteractionBehavior {

    override fun withStrategy(strategy: EntitySupplyStrategy<*>): AutoCompleteInteractionBehavior
}

/**
 * Responds to the interaction with the int choices specified by [builder].
 *
 * The provided choices are only suggestions and the user can provide any other input as well.
 *
 * @see IntegerOptionBuilder
 */
public suspend inline fun AutoCompleteInteractionBehavior.suggestInt(builder: IntegerOptionBuilder.() -> Unit) {
    contract {
        callsInPlace(builder, InvocationKind.EXACTLY_ONCE)
    }

    kord.rest.interaction.createIntAutoCompleteInteractionResponse(id, token, builder)
}

/**
 * Responds to the interaction with the number choices specified by [builder].
 *
 * The provided choices are only suggestions and the user can provide any other input as well.
 *
 * @see NumberOptionBuilder
 */
public suspend inline fun AutoCompleteInteractionBehavior.suggestNumber(builder: NumberOptionBuilder.() -> Unit) {
    contract {
        callsInPlace(builder, InvocationKind.EXACTLY_ONCE)
    }

    kord.rest.interaction.createNumberAutoCompleteInteractionResponse(id, token, builder)
}

/**
 * Responds to the interaction with the string choices specified by [builder].
 *
 * The provided choices are only suggestions and the user can provide any other input as well.
 *
 * @see StringChoiceBuilder
 */
public suspend inline fun AutoCompleteInteractionBehavior.suggestString(builder: StringChoiceBuilder.() -> Unit) {
    contract {
        callsInPlace(builder, InvocationKind.EXACTLY_ONCE)
    }

    kord.rest.interaction.createStringAutoCompleteInteractionResponse(id, token, builder)
}

/**
 * Responds to the interaction with [choices] to this auto-complete request.
 *
 * The provided choices are only suggestions and the user can provide any other input as well.
 */
public suspend inline fun <reified T> AutoCompleteInteractionBehavior.suggest(choices: List<Choice<T>>) {
    kord.rest.interaction.createAutoCompleteInteractionResponse(
        id,
        token,
        DiscordAutoComplete(choices)
    )
}
