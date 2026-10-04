package com.snowdango.sumire.infla

import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ListeningSessionTest {

    private var now = 0L
    private val session = ListeningSession(clock = { now })

    @Test
    fun pause_returnsActiveInterval() {
        session.resume()
        now = 1_000L

        assertEquals(1_000L, session.pause())
    }

    @Test
    fun pausedTimeIsNotCounted() {
        session.resume()
        now = 1_000L
        session.pause()
        now = 5_000L
        session.resume()
        now = 7_000L

        assertEquals(2_000L, session.finish())
    }

    @Test
    fun resumeWhileActive_keepsStartOfInterval() {
        session.resume()
        now = 500L
        session.resume()
        now = 1_000L

        assertEquals(1_000L, session.pause())
    }

    @Test
    fun pauseWhilePaused_returnsZero() {
        assertEquals(0L, session.pause())

        session.resume()
        now = 1_000L
        session.pause()
        now = 2_000L

        assertEquals(0L, session.pause())
    }

    @Test
    fun clockGoingBack_doesNotReturnNegative() {
        now = 1_000L
        session.resume()
        now = 0L

        assertEquals(0L, session.pause())
    }

    @Test
    fun finish_withoutSave_completesHistoryIdWithNull() {
        session.resume()
        now = 1_000L

        assertEquals(1_000L, session.finish())
        assertTrue(session.historyId.isCompleted)
        assertNull(runBlocking { session.historyId.await() })
    }

    @Test
    fun finish_afterSaveRequested_waitsForSaveResult() {
        session.markSaveRequested()
        session.finish()

        assertFalse(session.historyId.isCompleted)

        session.historyId.complete(42L)

        assertEquals(42L, runBlocking { session.historyId.await() })
    }

    @Test
    fun resumeAfterFinish_isIgnored() {
        session.finish()
        session.resume()
        now = 1_000L

        assertEquals(0L, session.pause())
    }
}
