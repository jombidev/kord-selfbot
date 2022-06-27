package dev.jombi.kordsb.rest

import dev.jombi.kordsb.rest.request.Request
import dev.jombi.kordsb.rest.request.RequestBuilder
import dev.jombi.kordsb.rest.route.Route


internal fun <T> RequestBuilder(route: Route<T>, keySize: Int = 2, request: RequestBuilder<T>.() -> Unit): Request<*,T> {
    val builder = RequestBuilder(route, keySize)
    builder.request()
    return builder.build()
}