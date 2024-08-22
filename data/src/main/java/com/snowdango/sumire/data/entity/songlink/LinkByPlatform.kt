package com.snowdango.sumire.data.entity.songlink

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LinkByPlatform(
    @SerialName("url")
    val url: String,
    @SerialName("country")
    val country: String,
    @SerialName("nativeAppUriMobile")
    val nativeAppUriMobile: String? = null,
    @SerialName("nativeAppUriDesktop")
    val nativeAppUriDesktop: String? = null,
    @SerialName("entityUniqueId")
    val entityUniqueId: String,
)
