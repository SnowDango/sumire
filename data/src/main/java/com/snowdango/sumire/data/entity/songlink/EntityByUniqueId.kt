package com.snowdango.sumire.data.entity.songlink

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class EntityByUniqueId(
    @SerialName("id")
    val id: String,
    @SerialName("type")
    val type: String,
    @SerialName("title")
    val title: String,
    @SerialName("artistName")
    val artistName: String,
    @SerialName("thumbnailUrl")
    val thumbnailUrl: String,
    @SerialName("thumbnailWidth")
    val thumbnailWidth: Int,
    @SerialName("thumbnailHeight")
    val thumbnailHeight: Int,
    @SerialName("apiProvider")
    val provider: String,
    @SerialName("platforms")
    val platforms: List<String>
)
