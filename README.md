### <strong>I don't take any responsibility for blocked Discord accounts that used this module.</strong>
### <strong>Using this on a user account is prohibited by the [Discord TOS](https://discord.com/terms) and can lead to the account block.</strong>

# Note

This repository always can be removed by the author of [kord](https://github.com/kordlib).
I didn't test many times, so I can't doubt everything working.

# Kord


__Kord is still in an experimental stage, as such we can't guarantee API stability between releases. While we'd love for
you to try out our library, we don't recommend you use this in production just yet.__

If you have any feedback, we'd love to hear it, hit us up on discord or write up an issue if you have any suggestions!

## What is Kord

Kord is a [coroutine-based](https://kotlinlang.org/docs/reference/coroutines-overview.html), modularized implementation
of the Discord API, written 100% in [Kotlin](https://kotlinlang.org/).

## Why use Kord

Kord was created as an answer to the frustrations of writing Discord bots with other JVM libraries, which either use
thread-blocking code or verbose and scope restrictive reactive systems. We believe an API written from the ground up in
Kotlin with coroutines can give you the best of both worlds: The conciseness of imperative code with the concurrency of
reactive code.

Aside from coroutines, we also wanted to give the user full access to lower level APIs. Sometimes you have to do some
unconventional things, and we want to allow you to do those in a safe and supported way.

## Documentation

* [Dokka docs](https://kordlib.github.io/kord/)
* [Wiki](https://github.com/kordlib/kord/wiki) // original docs

## Installation

will added soon :)

## Modules

### Core

A higher level API, combining `rest` and `gateway`, with additional (optional) caching. Unless you're writing your own
abstractions, we'd recommend using this.

```kotlin
suspend fun main() {
    val kord = Kord("your account token")
    val prefix = "."

    kord.on<MessageCreateEvent> {
        val content = message.content
        val channel = message.channel
        if (!prefixCommand.startsWith(prefix)) return@on
        if (this.message.author?.id != kord.selfId) return@on

        val rawArguments = content.split(" ")
        val prefixCommand = rawArguments[0]
        message.delete()
        when (prefixCommand.substring(prefix.length).lowercase()) {
            "ping" -> {
                val calc = System.currentTimeMillis()
                val message = channel.createMessage("Calculating ping...")
                message.edit { this.content = ":ping_pong: Pong!\nCalculated ping : ${System.currentTimeMillis() - calc}ms, Gateway Ping : ${kord.gateway.averagePing}" }
            }
        }
    }

    kord.login()
}
```

> will added more ~~maybe~~

## FAQ

## Will you support kotlin multi-platform

We will, there's an [issue](https://github.com/kordlib/kord/issues/69) open to track the features we want/need to make a
transition to MPP smooth.

## When will you document your code

Yes.
