package dev.jombi.kordsb.rest

import dev.jombi.kordsb.rest.request.KtorRequestHandler
import dev.jombi.kordsb.rest.service.RestClient

suspend fun main(args: Array<String>) {
    val token = args.firstOrNull() ?: error("token required")

    val rest = RestClient(KtorRequestHandler(token))

    val username = rest.user.getCurrentUser().username
    println("using $username's token")
}
