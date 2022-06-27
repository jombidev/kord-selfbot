package dev.jombi.kordsb.rest.builder.guild

import dev.jombi.kordsb.common.entity.Snowflake
import dev.jombi.kordsb.common.entity.optional.Optional
import dev.jombi.kordsb.common.entity.optional.OptionalBoolean
import dev.jombi.kordsb.common.entity.optional.delegate.delegate
import dev.jombi.kordsb.rest.builder.RequestBuilder
import dev.jombi.kordsb.rest.json.request.CurrentVoiceStateModifyRequest
import dev.jombi.kordsb.rest.json.request.VoiceStateModifyRequest
import kotlinx.datetime.Instant

public class CurrentVoiceStateModifyBuilder(public val channelId: Snowflake) :
    RequestBuilder<CurrentVoiceStateModifyRequest> {

    private var _requestToSpeakTimestamp: Optional<Instant?> = Optional.Missing()

    private var _suppress: OptionalBoolean = OptionalBoolean.Missing

    /**
     * Sets the user's request to speak.
     * The timestamp is used to sort how users appear on the moderators' request list.
     *
     * e.g: A client who requested to speak at 18:00,
     * will appear above a client who requested to speak at 20:00 in the same timezone.
     *
     * * A date in the past is treated as "now" by Discord.
     * * A null value removes the request to speak.
     */
    public var requestToSpeakTimestamp: Instant? by ::_requestToSpeakTimestamp.delegate()

    /**
     *  whether this user is muted by the current user.
     */
    public var suppress: Boolean? by ::_suppress.delegate()


    override fun toRequest(): CurrentVoiceStateModifyRequest {
        return CurrentVoiceStateModifyRequest(channelId, _suppress, _requestToSpeakTimestamp)
    }
}


public class VoiceStateModifyBuilder(public val channelId: Snowflake) : RequestBuilder<VoiceStateModifyRequest> {

    private var _suppress: OptionalBoolean = OptionalBoolean.Missing

    /**
     *  whether this user is muted by the current user.
     */
    public var suppress: Boolean? by ::_suppress.delegate()

    override fun toRequest(): VoiceStateModifyRequest {
        return VoiceStateModifyRequest(channelId, _suppress)
    }
}
