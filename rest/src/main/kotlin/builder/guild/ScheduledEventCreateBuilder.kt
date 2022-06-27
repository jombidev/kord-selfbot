package dev.jombi.kordsb.rest.builder.guild

import dev.jombi.kordsb.common.entity.GuildScheduledEventEntityMetadata
import dev.jombi.kordsb.common.entity.GuildScheduledEventPrivacyLevel
import dev.jombi.kordsb.common.entity.ScheduledEntityType
import dev.jombi.kordsb.common.entity.Snowflake
import dev.jombi.kordsb.common.entity.optional.Optional
import dev.jombi.kordsb.common.entity.optional.OptionalSnowflake
import dev.jombi.kordsb.common.entity.optional.delegate.delegate
import dev.jombi.kordsb.common.entity.optional.map
import dev.jombi.kordsb.rest.Image
import dev.jombi.kordsb.rest.builder.AuditRequestBuilder
import dev.jombi.kordsb.rest.json.request.GuildScheduledEventCreateRequest
import kotlinx.datetime.Instant

public class ScheduledEventCreateBuilder(
    /** The name of the scheduled event. */
    public var name: String,
    /** The [privacy level][GuildScheduledEventPrivacyLevel] of the scheduled event. */
    public var privacyLevel: GuildScheduledEventPrivacyLevel,
    /** The [Instant] to schedule the scheduled event. */
    public var scheduledStartTime: Instant,
    /** The [entity type][ScheduledEntityType] of the scheduled event. */
    public var entityType: ScheduledEntityType,
) : AuditRequestBuilder<GuildScheduledEventCreateRequest> {
    override var reason: String? = null

    private var _channelId: OptionalSnowflake = OptionalSnowflake.Missing

    /** The channel id of the scheduled event. */
    public var channelId: Snowflake? by ::_channelId.delegate()

    private var _description: Optional<String> = Optional.Missing()

    /** The description of the scheduled event. */
    public var description: String? by ::_description.delegate()

    private var _entityMetadata: Optional<GuildScheduledEventEntityMetadata> = Optional.Missing()

    /** The [entity metadata][GuildScheduledEventEntityMetadata] of the scheduled event. */
    public var entityMetadata: GuildScheduledEventEntityMetadata? by ::_entityMetadata.delegate()

    private var _scheduledEndTime: Optional<Instant> = Optional.Missing()

    /** The [Instant] when the scheduled event is scheduled to end. */
    public var scheduledEndTime: Instant? by ::_scheduledEndTime.delegate()

    private var _image: Optional<Image> = Optional.Missing()

    /** The cover image of the scheduled event. */
    public var image: Image? by ::_image.delegate()

    override fun toRequest(): GuildScheduledEventCreateRequest = GuildScheduledEventCreateRequest(
        channelId = _channelId,
        entityMetadata = _entityMetadata,
        name = name,
        privacyLevel = privacyLevel,
        scheduledStartTime = scheduledStartTime,
        scheduledEndTime = _scheduledEndTime,
        description = _description,
        entityType = entityType,
        image = _image.map { it.dataUri },
    )
}
