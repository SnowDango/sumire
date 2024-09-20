package com.snowdango.sumire.logging

import android.media.MediaMetadata
import android.media.session.PlaybackState
import android.util.Log

object Logging {

    fun loggingMetaData(metadata: MediaMetadata) {
        Log.d(
            "CurrentMetadata",
            """
                title = ${metadata.getString(MediaMetadata.METADATA_KEY_TITLE)}
                artist = ${metadata.getString(MediaMetadata.METADATA_KEY_ARTIST)}
                album = ${metadata.getString(MediaMetadata.METADATA_KEY_ALBUM)}
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

    fun loggingPlaybackAction(action: Long) {
        Log.d(
            "CurrentPlaybackAction",
            when (action) {
                PlaybackState.ACTION_PLAY -> "Play"
                PlaybackState.ACTION_STOP -> "Stop"
                PlaybackState.ACTION_PAUSE -> "Pause"
                PlaybackState.ACTION_SKIP_TO_NEXT -> "SkipNext"
                PlaybackState.ACTION_SKIP_TO_PREVIOUS -> "SkipPrevious"
                PlaybackState.ACTION_SKIP_TO_QUEUE_ITEM -> "SkipQueue"
                PlaybackState.ACTION_PLAY_PAUSE -> "PlayPause"
                PlaybackState.ACTION_FAST_FORWARD -> "FastForward"
                PlaybackState.ACTION_PREPARE -> "Prepare"
                PlaybackState.ACTION_REWIND -> "Rewind"
                else -> "UnknownAction: $action"
            },
        )
    }
}
