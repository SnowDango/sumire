package com.snowdango.sumire.data.entity.songlink

data class SongLinkResponse(
    val status: Status,
    val songData: SongLinkData,
) {
    enum class Status {
        Error,
        NOT_FOUND,
        OK,
    }
}
