package com.snowdango.sumire.model

import com.snowdango.sumire.usecase.db.SongsUseCase
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class GetSongsModel : KoinComponent {

    private val songsUseCase: SongsUseCase by inject()

    suspend fun getSearchTitleList(searchText: String): List<String> {
        return songsUseCase.getSearchSongsList("$searchText%").map {
            it.title
        }
    }

}