package com.snowdango.sumire.logging

import android.media.MediaMetadata
import android.media.session.PlaybackState
import android.util.Log

object Logging {

    fun loggingMetaData(metadata: MediaMetadata) {
        val title = metadata.getString(MediaMetadata.METADATA_KEY_TITLE)
        val artist = metadata.getString(MediaMetadata.METADATA_KEY_ARTIST)
        val album = metadata.getString(MediaMetadata.METADATA_KEY_ALBUM)
        Log.d(
            "CurrentMetadata",
            """
                title = $title
                artist = $artist
                album = $album
                artwork = ${
                metadata.getBitmap(MediaMetadata.METADATA_KEY_ART) ?: metadata.getBitmap(
                    MediaMetadata.METADATA_KEY_ALBUM_ART,
                )
            }
                artworkUri = ${metadata.getString(MediaMetadata.METADATA_KEY_ART_URI)}
                composer = ${metadata.getString(MediaMetadata.METADATA_KEY_COMPOSER)}
                completion = ${metadata.getString(MediaMetadata.METADATA_KEY_COMPILATION)}
                displayIcon = ${metadata.getBitmap(MediaMetadata.METADATA_KEY_DISPLAY_ICON)}
                mediaId = ${metadata.getString(MediaMetadata.METADATA_KEY_MEDIA_ID)}
            """.trimIndent(),
        )
    }

    fun loggingPlaybackState(state: Int) {
        Log.d(
            "CurrentPlaybackState",
            when (state) {
                PlaybackState.STATE_PLAYING -> "Playing"
                PlaybackState.STATE_PAUSED -> "Pause"
                PlaybackState.STATE_STOPPED -> "Stop"
                PlaybackState.STATE_SKIPPING_TO_NEXT -> "SkipNext"
                PlaybackState.STATE_SKIPPING_TO_PREVIOUS -> "SkipPrevious"
                else -> "UnknownState"
            },
        )
    }

    /**
     * actions はビットマスクなので、立っているフラグをすべて列挙する
     */
    fun loggingPlaybackAction(actions: Long) {
        val names = ACTION_NAMES
            .filter { (flag, _) -> (actions and flag) != 0L }
            .map { (_, name) -> name }
        Log.d(
            "CurrentPlaybackAction",
            if (names.isEmpty()) "UnknownAction: $actions" else names.joinToString(separator = ","),
        )
    }

    private val ACTION_NAMES: List<Pair<Long, String>> = listOf(
        PlaybackState.ACTION_PLAY to "Play",
        PlaybackState.ACTION_STOP to "Stop",
        PlaybackState.ACTION_PAUSE to "Pause",
        PlaybackState.ACTION_SKIP_TO_NEXT to "SkipNext",
        PlaybackState.ACTION_SKIP_TO_PREVIOUS to "SkipPrevious",
        PlaybackState.ACTION_SKIP_TO_QUEUE_ITEM to "SkipQueue",
        PlaybackState.ACTION_PLAY_PAUSE to "PlayPause",
        PlaybackState.ACTION_FAST_FORWARD to "FastForward",
        PlaybackState.ACTION_PREPARE to "Prepare",
        PlaybackState.ACTION_REWIND to "Rewind",
    )
}
