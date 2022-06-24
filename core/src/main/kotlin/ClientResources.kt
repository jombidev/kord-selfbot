package dev.kord.core

import dev.kord.common.entity.Snowflake
import dev.kord.core.supplier.EntitySupplyStrategy
import dev.kord.gateway.Intents
import io.ktor.client.*

public class ClientResources( // removed shard, applicationId because it's useless on self-bot
    public val token: String,
    public val httpClient: HttpClient,
    public val defaultStrategy: EntitySupplyStrategy<*>,
) {
    override fun toString(): String {
        return "ClientResources(httpClient=$httpClient, defaultStrategy=$defaultStrategy)"
    }
}
