package dev.jombi.kordsb.rest.service

import dev.jombi.kordsb.common.entity.DiscordMessageSticker
import dev.jombi.kordsb.common.entity.DiscordStickerPack
import dev.jombi.kordsb.common.entity.Snowflake
import dev.jombi.kordsb.rest.builder.guild.StickerModifyBuilder
import dev.jombi.kordsb.rest.json.request.GuildStickerCreateRequest
import dev.jombi.kordsb.rest.json.request.GuildStickerModifyRequest
import dev.jombi.kordsb.rest.json.request.MultipartGuildStickerCreateRequest
import dev.jombi.kordsb.rest.request.RequestHandler
import dev.jombi.kordsb.rest.route.Route
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

public class StickerService(requestHandler: RequestHandler) : RestService(requestHandler) {

    public suspend fun getNitroStickerPacks(): List<DiscordStickerPack> = call(Route.NitroStickerPacks)

    public suspend fun getGuildStickers(guildId: Snowflake): List<DiscordMessageSticker> =
        call(Route.GuildStickersGet) {
            keys[Route.GuildId] = guildId
        }

    public suspend fun getSticker(id: Snowflake): DiscordMessageSticker = call(Route.StickerGet) {
        keys[Route.StickerId] = id
    }

    public suspend fun getGuildSticker(guildId: Snowflake, id: Snowflake): DiscordMessageSticker =
        call(Route.GuildStickerGet) {
            keys[Route.GuildId] = guildId
            keys[Route.StickerId] = id
        }

    public suspend fun createGuildSticker(
        guildId: Snowflake,
        multipartRequest: MultipartGuildStickerCreateRequest,
    ): DiscordMessageSticker = call(Route.GuildStickerPost) {
        keys[Route.GuildId] = guildId
        body(GuildStickerCreateRequest.serializer(), multipartRequest.request)
        file(multipartRequest.file)
    }

    public suspend fun modifyGuildSticker(
        guildId: Snowflake,
        id: Snowflake,
        request: GuildStickerModifyRequest,
    ): DiscordMessageSticker = call(Route.GuildStickerPatch) {
        keys[Route.GuildId] = guildId
        keys[Route.StickerId] = id
        body(GuildStickerModifyRequest.serializer(), request)
    }

    public suspend inline fun modifyGuildSticker(
        guildId: Snowflake,
        id: Snowflake,
        builder: StickerModifyBuilder.() -> Unit,
    ): DiscordMessageSticker {
        contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
        val request = StickerModifyBuilder().apply(builder).toRequest()
        return modifyGuildSticker(guildId, id, request)
    }

    public suspend fun deleteSticker(guildId: Snowflake, id: Snowflake): Unit = call(Route.GuildStickerDelete) {
        keys[Route.GuildId] = guildId
        keys[Route.StickerId] = id
    }
}
