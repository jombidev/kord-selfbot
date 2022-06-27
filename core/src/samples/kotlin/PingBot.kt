import dev.jombi.kordsb.core.Kord
import dev.jombi.kordsb.core.event.message.MessageCreateEvent
import dev.jombi.kordsb.core.on
import dev.jombi.kordsb.gateway.Intent
import dev.jombi.kordsb.gateway.PrivilegedIntent

suspend fun main(args: Array<String>) {
    val kord = Kord(args.firstOrNull() ?: error("token required"))

    kord.on<MessageCreateEvent> {
        if (message.author?.isBot == true) return@on
        if (message.content == "!ping") message.channel.createMessage("pong")
    }

    kord.login {
        presence { playing("!ping to pong") }

        @OptIn(PrivilegedIntent::class)
        intents += Intent.MessageContent
    }
}
