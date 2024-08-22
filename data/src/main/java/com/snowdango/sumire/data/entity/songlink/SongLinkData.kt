package com.snowdango.sumire.data.entity.songlink

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SongLinkData(
    @SerialName("entityUniqueId")
    val entityUniqueId: String,
    @SerialName("userCountry")
    val userCountry: String,
    @SerialName("pageUrl")
    val pageUrl: String,
    @SerialName("entitiesByUniqueId")
    val entities: Map<String, EntityByUniqueId>,
    @SerialName("linksByPlatform")
    val links: Map<String, LinkByPlatform>,
)
