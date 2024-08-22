package com.snowdango.sumire.data.entity.songlink

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LinkByPlatform(
    @SerialName("url")
    val url: String,
    @SerialName("nativeAppUriMobile")
    val nativeAppUriMobile: String?,
    @SerialName("nativeAppUriDesktop")
    val nativeAppUriDesktop: String?,
    @SerialName("entityUniqueId")
    val entityUniqueId: String,
)
