package dev.jombi.kordsb.core.supplier

import dev.jombi.kordsb.common.annotation.KordExperimental
import dev.jombi.kordsb.common.annotation.KordUnsafe
import dev.jombi.kordsb.common.entity.Snowflake
import dev.jombi.kordsb.core.ClientResources
import dev.jombi.kordsb.core.Kord
import dev.jombi.kordsb.core.cache.KordCacheBuilder
import dev.jombi.kordsb.core.gateway.DefaultMasterGateway
import dev.jombi.kordsb.gateway.Gateway
import dev.jombi.kordsb.gateway.PrivilegedIntent
import dev.jombi.kordsb.rest.request.KtorRequestHandler
import dev.jombi.kordsb.rest.service.RestClient
import io.ktor.client.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test

internal class CacheEntitySupplierTest {

    @Test
    @OptIn(PrivilegedIntent::class, KordUnsafe::class, KordExperimental::class)
    fun `cache does not throw when accessing unregistered entities`(): Unit = runBlocking {
        val kord = Kord(
                ClientResources("", HttpClient(), EntitySupplyStrategy.cache, ),
                KordCacheBuilder().build(),
                DefaultMasterGateway(Gateway.none()),
                RestClient(KtorRequestHandler("")),
                Snowflake(0u),
                MutableSharedFlow(),
                Dispatchers.Default
        )

        kord.unsafe.guild(Snowflake(0u)).regions.toList()
    }

}
