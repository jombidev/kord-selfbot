package dev.jombi.kordsb.rest.json.response

import kotlinx.serialization.Serializable

@Serializable
public data class GetPruneResponse(val pruned: Int)

@Serializable
public data class PruneResponse(val pruned: Int?)
