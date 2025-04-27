package com.snowdango.sumire.usecase.db

import com.snowdango.sumire.data.entity.db.Tasks
import com.snowdango.sumire.repository.SongsDatabase
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class TasksUseCase : KoinComponent {

    private val songsDatabase: SongsDatabase by inject()

    suspend fun saveTasks(mediaId: String, songId: Long) {
        val task = Tasks(
            mediaId = mediaId,
            songId = songId,
        )
        songsDatabase.tasksDao.insert(task)
    }
}
