package com.snowdango.sumire.data.entity.songlink

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SongLinkData(
    @SerialName("statusCode")
    val statusCode: Int? = 0,
    @SerialName("code")
    val code: String? = "",
    @SerialName("entityUniqueId")
    val entityUniqueId: String = "",
    @SerialName("userCountry")
    val userCountry: String = "",
    @SerialName("pageUrl")
    val pageUrl: String = "",
    @SerialName("entitiesByUniqueId")
    val entities: Map<String, EntityByUniqueId> = mapOf(),
    @SerialName("linksByPlatform")
    val links: Map<String, LinkByPlatform> = mapOf(),
)
