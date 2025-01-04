package com.snowdango.presenter.history

import android.annotation.SuppressLint
import android.content.res.Configuration
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.snowdango.presenter.history.mock.MockData
import com.snowdango.sumire.ui.component.CircleLoading
import com.snowdango.sumire.ui.component.ListSongCard
import com.snowdango.sumire.ui.component.SearchText
import com.snowdango.sumire.ui.theme.SumireTheme
import com.snowdango.sumire.ui.viewdata.SongCardViewData
import org.koin.androidx.compose.koinViewModel

@Composable
fun HistoryScreen(
    windowSize: WindowSizeClass
) {
    val viewModel: HistoryViewModel = koinViewModel()
    val historiesPaging = viewModel.getHistories.collectAsLazyPagingItems()
    var currentSearchText: String by remember { mutableStateOf("") }
    val searchHistoriesPaging = viewModel.searchHistories.collectAsLazyPagingItems()
    val searchSuggestTitleList = viewModel.suggestSearchTitleListFlow.collectAsStateWithLifecycle()

    val isLandScape =
        LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE

    val onSearch: (String) -> Unit = {
        viewModel.setSearchText(it)
        searchHistoriesPaging.refresh()
        currentSearchText = it
    }

    val onSearchTextChange: (searchText: String) -> Unit = {
        viewModel.getSuggestSearchTitle(it)
    }

    val histories = if (currentSearchText.isBlank()) {
        historiesPaging
    } else {
        searchHistoriesPaging
    }

    if (!isLandScape) {
        when (windowSize.widthSizeClass) {
            WindowWidthSizeClass.Compact -> HistoryCompatScreen(
                histories,
                searchSuggestTitleList.value,
                onSearchTextChange,
                onSearch
            )

            WindowWidthSizeClass.Medium -> HistorySplit2Screen(
                histories,
                searchSuggestTitleList.value,
                onSearchTextChange,
                onSearch
            )

            WindowWidthSizeClass.Expanded -> HistorySplit2Screen(
                histories,
                searchSuggestTitleList.value,
                onSearchTextChange,
                onSearch
            )
        }
    } else {
        HistorySplit2Screen(histories, searchSuggestTitleList.value, onSearchTextChange, onSearch)
    }
}


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HistoryCompatScreen(
    histories: LazyPagingItems<SongCardViewData>,
    suggestSearchTitle: List<String>,
    onSearchTextChange: (searchText: String) -> Unit,
    onSearch: (searchText: String) -> Unit,
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 16.dp, start = 24.dp, end = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            SearchText(
                modifier = Modifier.fillMaxWidth(),
                searchSuggestList = suggestSearchTitle,
                onSearchTextChange = onSearchTextChange,
                onSearch = onSearch,
            )
        }
        if (histories.loadState.refresh == LoadState.Loading) {
            item {
                CircleLoading()
            }
        } else {
            var headerDay = ""
            for (index in 0 until histories.itemCount) {
                val viewData = histories.peek(index)
                viewData?.let {
                    if (it.headerDay != headerDay) {
                        headerDay = it.headerDay
                        stickyHeader {
                            DateHeader(it.headerDay)
                        }
                    }
                    item {
                        ListSongCard(
                            songCardViewData = it,
                            modifier = Modifier
                                .padding(start = 8.dp, end = 8.dp, bottom = 4.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun HistorySplit2Screen(
    histories: LazyPagingItems<SongCardViewData>,
    suggestSearchTitle: List<String>,
    onSearchTextChange: (searchText: String) -> Unit,
    onSearch: (searchText: String) -> Unit,
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 16.dp, start = 24.dp, end = 24.dp)
    ) {
        item(span = { GridItemSpan(2) }) {
            SearchText(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(),
                searchSuggestList = suggestSearchTitle,
                onSearchTextChange = onSearchTextChange,
                onSearch = onSearch,
            )
        }
        if (histories.loadState.refresh == LoadState.Loading) {
            item(span = { GridItemSpan(2) }) {
                CircleLoading()
            }
        } else {
            var headerDay = ""
            for (index in 0 until histories.itemCount) {
                val viewData = histories.peek(index)
                viewData?.let {
                    if (it.headerDay != headerDay) {
                        headerDay = it.headerDay
                        item(span = { GridItemSpan(2) }) {
                            DateHeader(it.headerDay)
                        }
                    }
                    item {
                        ListSongCard(
                            songCardViewData = it,
                            modifier = Modifier
                                .padding(start = 8.dp, end = 8.dp, bottom = 4.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun DateHeader(date: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(54.dp)
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = date,
            color = MaterialTheme.colorScheme.secondary,
            modifier = Modifier
                .wrapContentSize()
        )
    }
}


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Preview(group = HISTORY_GROUP, name = "DateHeader")
@Composable
fun Preview_DateHeader() {
    SumireTheme {
        Box(modifier = Modifier.background(MaterialTheme.colorScheme.background)) {
            DateHeader(date = MockData.mockDate)
        }
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Preview(group = HISTORY_GROUP, name = "HistoryCompatScreen")
@Composable
fun Preview_HistoryCompatScreen() {
    SumireTheme {
        Scaffold {
            HistoryCompatScreen(
                histories = MockData.mockPagingFlow.collectAsLazyPagingItems(),
                suggestSearchTitle = listOf(),
                onSearchTextChange = {},
                onSearch = {}
            )
        }
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Preview(
    group = HISTORY_GROUP,
    name = "HistorySplit2Screen",
    device = "spec:width=1280dp,height=800dp,dpi=240"
)
@Composable
fun Preview_HistorySplit2Screen() {
    SumireTheme {
        Scaffold {
            HistorySplit2Screen(
                histories = MockData.mockPagingFlow.collectAsLazyPagingItems(),
                suggestSearchTitle = listOf(),
                onSearchTextChange = {},
                onSearch = {}
            )
        }
    }
}