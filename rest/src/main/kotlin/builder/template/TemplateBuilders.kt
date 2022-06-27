package dev.jombi.kordsb.rest.builder.template

import dev.jombi.kordsb.common.entity.optional.Optional
import dev.jombi.kordsb.common.entity.optional.delegate.delegate
import dev.jombi.kordsb.common.entity.optional.map
import dev.jombi.kordsb.rest.Image
import dev.jombi.kordsb.rest.builder.RequestBuilder
import dev.jombi.kordsb.rest.json.request.GuildFromTemplateCreateRequest
import dev.jombi.kordsb.rest.json.request.GuildTemplateCreateRequest
import dev.jombi.kordsb.rest.json.request.GuildTemplateModifyRequest

public class GuildFromTemplateCreateBuilder(public var name: String) : RequestBuilder<GuildFromTemplateCreateRequest> {

    private var _image: Optional<Image> = Optional.Missing()
    public var image: Image? by ::_image.delegate()


    override fun toRequest(): GuildFromTemplateCreateRequest = GuildFromTemplateCreateRequest(
        name, _image.map { it.dataUri }
    )
}

public class GuildTemplateCreateBuilder(public var name: String) : RequestBuilder<GuildTemplateCreateRequest> {
    private var _description: Optional<String> = Optional.Missing()
    public var description: String? by ::_description.delegate()

    override fun toRequest(): GuildTemplateCreateRequest = GuildTemplateCreateRequest(name, _description)
}


public class GuildTemplateModifyBuilder : RequestBuilder<GuildTemplateModifyRequest> {

    private var _name: Optional<String> = Optional.Missing()
    public var name: String? by ::_name.delegate()

    private var _description: Optional<String> = Optional.Missing()
    public var description: String? by ::_description.delegate()

    override fun toRequest(): GuildTemplateModifyRequest = GuildTemplateModifyRequest(_name, _description)
}
