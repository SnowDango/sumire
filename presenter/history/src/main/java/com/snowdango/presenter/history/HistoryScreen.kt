package com.snowdango.presenter.history

import android.content.res.Configuration
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.snowdango.sumire.ui.component.ListSongCard
import com.snowdango.sumire.ui.viewdata.SongCardViewData
import org.koin.androidx.compose.koinViewModel

@Composable
fun HistoryScreen(
    windowSize: WindowSizeClass
) {
    val viewModel: HistoryViewModel = koinViewModel()
    val histories = viewModel.getHistories.collectAsLazyPagingItems()

    val isLandScape =
        LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE

    if (!isLandScape) {
        when (windowSize.widthSizeClass) {
            WindowWidthSizeClass.Compact -> HistoryCompatScreen(histories)
            WindowWidthSizeClass.Medium -> HistorySplit2Screen(histories)
            WindowWidthSizeClass.Expanded -> HistorySplit2Screen(histories)
        }
    } else {
        HistorySplit2Screen(histories)
    }
}


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HistoryCompatScreen(
    histories: LazyPagingItems<SongCardViewData>
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 16.dp, start = 24.dp, end = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        var headerDay = ""
        for (index in 0 until histories.itemCount) {
            val nextViewData = histories.peek(index)
            nextViewData?.let {
                if (it.headerDay != headerDay) {
                    headerDay = it.headerDay
                    stickyHeader {
                        DateHeader(it.headerDay)
                    }
                }
            }
            val viewData = histories[index]
            viewData?.let {
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

@Composable
fun HistorySplit2Screen(
    histories: LazyPagingItems<SongCardViewData>
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 16.dp, start = 24.dp, end = 24.dp)
    ) {
        var headerDay = ""
        for (index in 0 until histories.itemCount) {
            val nextViewData = histories.peek(index)
            nextViewData?.let {
                if (it.headerDay != headerDay) {
                    headerDay = it.headerDay
                    item(span = { GridItemSpan(2) }) {
                        DateHeader(it.headerDay)
                    }
                }
            }
            val viewData = histories[index]
            viewData?.let {
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

@Preview(group = HISTORY_GROUP, name = "DateHeader")
@Composable
fun DateHeader(date: String = "2024/10/13") {
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
