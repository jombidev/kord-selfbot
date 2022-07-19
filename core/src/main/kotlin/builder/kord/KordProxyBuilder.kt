package dev.jombi.kordsb.core.builder.kord

import dev.jombi.kordsb.common.annotation.KordExperimental
import dev.jombi.kordsb.common.entity.Snowflake
import dev.jombi.kordsb.core.Kord

/**
 * The proxy Kord builder. You probably want to invoke the [DSL builder][Kord.proxy] instead.
 */
@KordExperimental
public class KordProxyBuilder(override var applicationId: Snowflake) : RestOnlyBuilder() {

    override val token: String get() = ""
}