package dev.jombi.kordsb.rest.service

import dev.jombi.kordsb.rest.request.RequestBuilder
import dev.jombi.kordsb.rest.request.RequestHandler
import dev.jombi.kordsb.rest.route.Route
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

public abstract class RestService(@PublishedApi internal val requestHandler: RequestHandler) {

    @PublishedApi
    internal suspend inline fun <T> call(route: Route<T>, builder: RequestBuilder<T>.() -> Unit = {}): T {
        contract {
            callsInPlace(builder, InvocationKind.EXACTLY_ONCE)
        }

        val interceptedBuilder = RequestBuilder(route).apply(builder)
        requestHandler.intercept(interceptedBuilder)

        val request = interceptedBuilder.build()
        return requestHandler.handle(request)
    }
}
