package dev.jombi.kordsb.voice.exception

public class VoiceConnectionInitializationException : Exception {
    public constructor(message: String) : super(message)
    public constructor(cause: Throwable) : super(cause)
    public constructor(message: String, cause: Throwable) : super(message, cause)
}
