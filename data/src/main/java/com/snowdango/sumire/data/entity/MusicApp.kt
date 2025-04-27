package com.snowdango.sumire.data.entity

enum class MusicApp(val apiProvider: String, val packageName: String, val platform: String) {
    APPLE_MUSIC("itunes", "com.apple.android.music", "appleMusic"),
    SPOTIFY("spotify", "", "spotify"),
    YOUTUBE("youtube", "", "youtubeMusic"),
    GOOGLE("google", "", "google"),
    PANDORA("pandora", "", "pandora"),
    DEEZER("deezer", "", "deezer"),
    AMAZON("amazon", "", "amazonMusic"),
    TIDAL("tidal", "", "tidal"),
    NAPSTER("napster", "", "napster"),
    YANDEX("yandex", "", "yandex"),
}
