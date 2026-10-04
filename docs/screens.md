# アプリ画面と共通 UI

アプリ本体の UI はすべて Jetpack Compose (Material 3) で書かれている。ウィジェットは [widget.md](widget.md) を参照。

## 起動シーケンス

1. [`SumireApp.onCreate()`](../app/src/main/java/com/snowdango/sumire/SumireApp.kt)
   - Koin を起動する ([architecture.md](architecture.md#di-koin))。
   - `EventSharedFlow` をアプリスコープで購読し、`ChangeCurrentSong` のたびに Analytics の `save_song_data` を送る。
2. [`MainActivity.onCreate()`](../app/src/main/java/com/snowdango/sumire/MainActivity.kt)
   - `installSplashScreen()` (テーマ `Theme.Sumire.SplashScreen`、背景色 `@color/primary`) → `enableEdgeToEdge()` → `setContent { SumireTheme { ... } }`。
   - 権限ダイアログ 2 種と `MainScreen` を表示する。
3. `MainActivity.onStart()`
   - 通知リスナーが有効か確認し、無効なら通知リスナー用ダイアログを出す。有効なら `SongListenerService` を `startForegroundService()` で起動する。

## 権限ダイアログ

状態は [`MainViewModel`](../app/src/main/java/com/snowdango/sumire/MainViewModel.kt) が `StateFlow<Boolean>` で持つ。

| ダイアログ | 表示条件 | ボタン | 備考 |
| --- | --- | --- | --- |
| [`NotificationPostDialog`](../app/src/main/java/com/snowdango/sumire/dialog/NotificationPostDialog.kt) (通知の表示) | 初回起動 (`is_first_time` が true) | OK → `POST_NOTIFICATIONS` の実行時権限をリクエスト / Cancel | 閉じた時点で `is_first_time = false` を保存。権限が無くてもアプリは動くが、ウィジェットのエラー表示ができない |
| [`NotificationListenerDialog`](../app/src/main/java/com/snowdango/sumire/dialog/NotificationListenerDialog.kt) (通知へのアクセス) | 通知リスナーが無効 かつ 通知権限ダイアログを表示していない | 設定へ移動 → `Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS` / 閉じる | ダイアログ外タップでは閉じない。アプリの動作に必須の権限 |

ダイアログ文言は Kotlin に日本語で直書きされている (文字列リソースではない)。

## ナビゲーション

[`MainScreen`](../app/src/main/java/com/snowdango/sumire/MainScreen.kt) は `Scaffold` + `NavigationBar` + `NavHost` の 4 タブ構成。

| `ROUTE` | ルート文字列 | 画面 | アイコン (選択時 / 非選択時) | ラベル |
| --- | --- | --- | --- | --- |
| `PLAYING` (開始画面) | `"PLAYING"` | `PlayingScreen(windowSize)` | `Filled.PlayArrow` / `Outlined.PlayArrow` | `playing` |
| `HISTORY` | `"HISTORY"` | `HistoryScreen(windowSize)` | `Filled.MusicNote` / `Outlined.MusicNote` | `history` |
| `REPORT` | `"REPORT"` | `ReportScreen(windowSize)` | `Filled.BarChart` / `Outlined.BarChart` | `report` |
| `SETTINGS` | `"SETTINGS"` | `SettingsScreen()` | `Filled.Settings` / `Outlined.Settings` | `settings` |

- タブの並び順は `ROUTE` enum の定義順。ルート文字列は enum の `name` をそのまま使う。
- タブ切り替えは `popUpTo(startDestination) { saveState = true }` + `launchSingleTop` + `restoreState` で、バックスタックを積まずに各タブの状態を保存・復元する。
- 画面遷移アニメーションは全部 `None`。
- 遷移のたびに Analytics の `view_screen` (パラメータ `screen` = ルート文字列) を送る。リスナーは `LifecycleStartEffect` で start / stop に合わせて登録・解除する。

## 画面サイズへの対応

`MainActivity` で `calculateWindowSizeClass(activity)` を計算して各画面に渡す。再生中画面・履歴画面・レポート画面は同じ規則でレイアウトを切り替える。

| 条件 | レイアウト |
| --- | --- |
| 縦向き かつ `WindowWidthSizeClass.Compact` | 1 カラム (`*CompactScreen` / `HistoryCompatScreen`) |
| 縦向き かつ `Medium` / `Expanded` | 2 分割 (`*Split2Screen`) |
| 横向き (サイズを問わない) | 2 分割 (`*Split2Screen`) |

## 再生中画面 (`:presenter:playing`)

ファイル: [`PlayingScreen.kt`](../presenter/playing/src/main/java/com/snowdango/sumire/presenter/playing/PlayingScreen.kt) / [`PlayingViewModel.kt`](../presenter/playing/src/main/java/com/snowdango/sumire/presenter/playing/PlayingViewModel.kt)

ViewModel の状態:

| プロパティ | 型 | 取得方法 |
| --- | --- | --- |
| `currentPlayingSong` | `StateFlow<PlayingSongData?>` | 初期化時と `ChangeCurrentSong` 受信時に `PlayingSongSharedFlow.getCurrentPlayingSong()` を読む |
| `recentHistories` | `StateFlow<List<SongCardViewData>>` | `GetHistoriesModel.getRecentHistoriesSongFlow(size = 10)` を `stateIn(WhileSubscribed(5000))` |

表示:

- `currentPlayingSong?.isActive` が true のときだけ再生中の曲を表示する。一時停止中や曲なしのときは `NothingPlayingSongComponent` (「Music is not playing in all apps.」) を出す。
- `PlayingSongComponent`: 円形アートワーク (`CircleSongArtwork`)、タイトル、アルバム、アーティスト、「from <アイコン> <サービス名>」。
- Compact: `LazyColumn` に再生中の曲 → 見出し「Recent」→ 直近 10 件の `ListSongCard`。
- Split2: 左半分に再生中の曲、右半分に Recent のリスト。
- Recent の時刻表示は `"3m ago"` 形式の相対時刻。

## 履歴画面 (`:presenter:history`)

ファイル: [`HistoryScreen.kt`](../presenter/history/src/main/java/com/snowdango/presenter/history/HistoryScreen.kt) / [`HistoryViewModel.kt`](../presenter/history/src/main/java/com/snowdango/presenter/history/HistoryViewModel.kt)

ViewModel の状態:

| プロパティ | 内容 |
| --- | --- |
| `getHistories` | 全履歴の `Pager` (`pageSize = 20`, `prefetchDistance = 50`)。`PagingData.map` で `SongCardViewData` (時刻は `HH:mm:ss`) に変換し `cachedIn(viewModelScope)` |
| `searchHistories` | 検索用の `Pager`。PagingSource を作るたびに `getSearchText()` を読むので、`setSearchText()` → `refresh()` で新しい検索条件のクエリになる |
| `suggestSearchTitleListFlow` | 検索候補のタイトル一覧 (`StateFlow<List<String>>`) |

検索の流れ:

1. 入力のたびに `getSuggestSearchTitle(text)` → `songs.title` の前方一致で最大 6 件を候補に出す。
2. 確定 (IME の検索 or 候補タップ) で `setSearchText(text)` → `searchHistoriesPaging.refresh()` → 画面側の `currentSearchText` を更新。
3. `currentSearchText` が空なら `getHistories`、空でなければ `searchHistories` を表示する。検索条件はタイトルの部分一致のみ。
4. 入力欄の × で文字を消すと、候補リストが閉じていれば空文字で検索し直す (= 全履歴に戻る)。

表示:

- 読み込み中 (`loadState.refresh == LoadState.Loading`) は `CircleLoading` だけを出す。
- 日付 (`headerDay`、`yyyy/MM/dd`) が変わるところに `DateHeader` を挟む。Compact は `LazyColumn` の `stickyHeader`、Split2 は 2 カラムの `LazyVerticalGrid` で全幅アイテムとして挟む (sticky ではない)。

## レポート画面 (`:presenter:report`)

ファイル: [`ReportScreen.kt`](../presenter/report/src/main/java/com/snowdango/sumire/presenter/report/ReportScreen.kt) / [`ReportViewModel.kt`](../presenter/report/src/main/java/com/snowdango/sumire/presenter/report/ReportViewModel.kt)

当月の再生状況をまとめて表示する。

- ViewModel は `GetReportModel.getCurrentMonthReportFlow()` を `stateIn(WhileSubscribed(5000), initialValue = null)` で `StateFlow<MonthlyReportViewData?>` にする。`null` の間は読み込み中。
- 集計は Room の Flow なので、画面を開いている間に再生が保存されると表示も更新される。集計の中身は [persistence.md](persistence.md#集計クエリ-レポート用) を参照。

| 状態 | 表示 |
| --- | --- |
| 読み込み中 (`null`) | `CircleLoading` |
| 当月の再生が 0 回 | `NoPlayReportComponent` (「No songs have been played in <年月> yet.」) |
| 縦向き かつ Compact | `ReportCompactScreen`: 1 カラムで「<年月> Report」+ サマリー → Top Songs → Top Artists |
| それ以外 (横向き / Medium / Expanded) | `ReportSplit2Screen`: 左に見出し・サマリー・Top Artists、右に Top Songs |

コンポーネント (`component/`):

| Composable | 内容 |
| --- | --- |
| `PlaySummary` | 上段に全幅の再生時間 (Listening Time、`12h 34m` / 1 時間未満は `34m`。1 分未満は切り捨て)、下段に再生回数 (Plays)・曲の種類数 (Songs)・アーティストの種類数 (Artists) の 3 つのタイル |
| `RankedSongCard` | 順位、サムネイル (`SongThumbnail`)、タイトル / アルバム / アーティスト、再生回数 (上位 5 曲) |
| `RankedArtistCard` | 順位、アーティスト名、再生回数 (上位 3 組) |
| `RankText` / `PlayCountText` | 順位と「N play(s)」の表示 (`plurals` リソース) |

文言は `presenter/report/src/main/res/values/strings.xml` の文字列リソース (英語) にある。

## 設定画面 (`:presenter:settings`)

ファイル: [`SettingsScreen.kt`](../presenter/settings/src/main/java/com/snowdango/sumire/settings/SettingsScreen.kt) / [`SettingsViewModel.kt`](../presenter/settings/src/main/java/com/snowdango/sumire/settings/SettingsViewModel.kt)

- 設定値は `SettingsModel.getSettingsFlow()` を `shareIn(WhileSubscribed(5000))` したものを購読する。書き込みは `viewModelScope.launch(Dispatchers.IO)`。
- UI は compose-settings の `SettingsGroup` / `SettingsMenuLink` / `SettingsRadioButton` を使う。

| セクション | 項目 | 動作 |
| --- | --- | --- |
| Settings | ウィジェットのタップ時の動作 | `WidgetActionTypeDialog` で `COPY` (URL をコピー) / `TWITTER` (X に共有) を選ぶ。サブタイトルは `WidgetActionType.description` |
| | URL取得時に優先されるサービス | `UrlPriorityPlatformDialog` で `UrlPriorityPlatform` を選ぶ。サブタイトルは platform 文字列そのまま |
| | Version | `BuildConfig.VERSION_NAME` (`:presenter:settings` の BuildConfig) を表示するだけ |
| Dev (`BuildConfig.DEBUG` のみ) | Crashlytics | `RuntimeException("意図的なCrash")` を投げる |

選択ダイアログは選択前は「Select」ボタンが無効で、ダイアログを開くたびに未選択状態から始まる (現在値は初期選択されない)。ラジオボタンの配色は Material 3 の `ListItemDefaults.colors(...)` で渡す (compose-settings 3.x の API)。

## 共通 UI (`:ui`)

| ファイル | 内容 |
| --- | --- |
| [`component/ListSongCard.kt`](../ui/src/main/java/com/snowdango/sumire/ui/component/ListSongCard.kt) | 履歴 1 件のカード。サムネイル (`SongThumbnail`)、タイトル / アルバム / アーティスト、サービスアイコン、時刻 |
| [`component/SongThumbnail.kt`](../ui/src/main/java/com/snowdango/sumire/ui/component/SongThumbnail.kt) | 角丸のサムネイル。`isThumbUrl` なら Coil の `AsyncImage`、そうでなければ Base64 をデコード (結果は `remember` でキャッシュ)、どちらも無ければ `noimage`。履歴カードとレポートのランキングで共用 |
| [`component/CircleSongArtwork.kt`](../ui/src/main/java/com/snowdango/sumire/ui/component/CircleSongArtwork.kt) | 円形のアートワーク。`null` なら `noimage` |
| [`component/MusicAppImage.kt`](../ui/src/main/java/com/snowdango/sumire/ui/component/MusicAppImage.kt) | `MusicApp` → アイコン drawable (`when` で網羅) |
| [`component/MusicAppText.kt`](../ui/src/main/java/com/snowdango/sumire/ui/component/MusicAppText.kt) | `MusicApp` → 表示名 (`when` で網羅)。Preview 用の `MusicAppPramProvider` もここ |
| [`component/SearchText.kt`](../ui/src/main/java/com/snowdango/sumire/ui/component/SearchText.kt) | `DockedSearchBar`。フォーカス中だけ候補リストを展開する |
| [`component/CircleLoading.kt`](../ui/src/main/java/com/snowdango/sumire/ui/component/CircleLoading.kt) | 中央に `CircularProgressIndicator` |
| [`viewdata/SongCardViewData.kt`](../ui/src/main/java/com/snowdango/sumire/ui/viewdata/SongCardViewData.kt) | カード表示用データ (`title`, `artistName`, `albumName`, `thumbnail`, `isThumbUrl`, `playTimeText`, `headerDay`, `app`) |
| [`viewdata/MonthlyReportViewData.kt`](../ui/src/main/java/com/snowdango/sumire/ui/viewdata/MonthlyReportViewData.kt) | レポート表示用データ (`MonthlyReportViewData` と、ランキング 1 件分の `RankedSongViewData` / `RankedArtistViewData`) |
| [`theme/SumireTheme.kt`](../ui/src/main/java/com/snowdango/sumire/ui/theme/SumireTheme.kt) | material-kolor の `rememberDynamicColorScheme` でシード色 `@color/seed` (`#b0c4de`) から配色を作る。ダークモードは端末設定に従う。Android 12+ の壁紙ダイナミックカラーは使っていない |
| [`theme/Typography.kt`](../ui/src/main/java/com/snowdango/sumire/ui/theme/Typography.kt) | Material 3 の Typography 定義 (`FontFamily.Default`) |
| [`theme/glance/`](../ui/src/main/java/com/snowdango/sumire/ui/theme/glance/) | ウィジェット用の `SumireGlanceTheme` と固定配色 `glanceColors` (ライト / ダーク) |

リソース: 各音楽サービスのアイコン (`apple_music.png` など)、`noimage.png`、ウィジェットから参照するランチャーアイコン (`mipmap/ic_launcher*`)。

## Preview とスクリーンショットテスト

- 画面・コンポーネントの `@Preview` には `group` と `name` を付ける。グループ名は各モジュールの `PreviewGroup.kt` の定数 (`PLAYING_GROUP = "playing"`, `HISTORY_GROUP = "history"`, `REPORT_GROUP = "report"`, `SETTING_GROUP = "settings"`, `UTIL_GROUP = "util/ui"`)。
- Preview 用のダミーデータは各 presenter の `mock/MockData.kt` にある。
- VRT の撮影対象は、各モジュールの `src/screenshotTest/kotlin/` にある `@PreviewTest` 付きの Composable だけ。中身は main の Preview 関数を呼ぶだけのラッパーで、`@Preview` のパラメータ (`group`, `name`, `device`) は main 側と揃える。ラッパーから呼べるように、VRT に載せる main の Preview は private にしない。
- 撮影対象のモジュールは `:ui`, `:presenter:playing`, `:presenter:history`, `:presenter:report`, `:presenter:settings` (`:presenter:widget` と `:app` には無い)。
- 撮影の仕組みと CI の流れは [build-and-ci.md](build-and-ci.md#vrt-の仕組み) を参照。対象の Preview を変更すると、VRT の差分として PR にコメントされる。
