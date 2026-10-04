package com.snowdango.sumire.infla

import kotlinx.coroutines.CompletableDeferred

/**
 * 1 曲 (キューアイテム 1 つ) 分の再生時間を測る。
 * 一時停止中は聴いていないので、再生中 (isActive) だった区間の長さだけを数える。
 * 区間の長さは再生位置ではなく [clock] の差で測るので、シークしても実際に流れた時間になる。
 * 本番では端末のスリープ中も進み、時刻の変更でずれない elapsedRealtime を渡す
 */
internal class ListeningSession(
    private val clock: () -> Long,
) {

    /**
     * この曲の履歴の ID。保存 (アートワーク待ちと song.link API) が終わるまで分からないので、
     * 再生時間の書き込みはこれを待つ。保存されないまま終わった曲や、保存に失敗した曲は null で完了する
     */
    val historyId = CompletableDeferred<Long?>()

    private var activeSince: Long? = null
    private var isSaveRequested = false
    private var isFinished = false

    /**
     * 再生が始まった・再開した。すでに再生中なら区間を測り直さない
     */
    @Synchronized
    fun resume() {
        if (!isFinished && activeSince == null) {
            activeSince = clock()
        }
    }

    /**
     * 一時停止した。
     * @return 直前まで再生していた区間の長さ (ミリ秒)。再生中でなかったら 0
     */
    @Synchronized
    fun pause(): Long {
        val since = activeSince ?: return 0L
        activeSince = null
        return (clock() - since).coerceAtLeast(0L)
    }

    /**
     * 曲の保存を始めた。これ以降に曲が終わっても、historyId は保存の結果で完了させる
     */
    @Synchronized
    fun markSaveRequested() {
        isSaveRequested = true
    }

    /**
     * 曲が終わった (別の曲に切り替わった・再生中の曲が無くなった)。
     * @return 最後の区間の長さ (ミリ秒)。一時停止中に終わったら 0
     */
    @Synchronized
    fun finish(): Long {
        val listenedMs = pause()
        isFinished = true
        // 保存するのは再生中の曲だけなので、ここまで保存が始まっていない曲はこの先も保存されない。
        // 待たせたままにすると書き込み待ちのコルーチンが残り続けるので、ここで null にする
        if (!isSaveRequested) {
            historyId.complete(null)
        }
        return listenedMs
    }
}
