# 開発ガイド

既存コードの書き方に合わせるための規約、よくある変更の手順、実装上の注意点と既知の課題をまとめる。

## コーディング規約

既存コードから読み取れる約束事。新しいコードもこれに合わせる。

### 全般

- コメントは日本語で、「何をしているか」より「なぜそうしているか」を書く。
- 末尾カンマ (trailing comma) を付ける。1 行は 120 文字まで (detekt の `MaxLineLength`)。
- コルーチン内で `catch (e: Exception)` するときは、先に `catch (e: CancellationException) { throw e }` を書いてキャンセルを握りつぶさない。
- ログのタグは `companion object` の `private const val LOG_TAG` に置く。

### レイヤーと DI

- 依存の向きは Presenter → Model → UseCase → Repository → Data ([architecture.md](architecture.md#レイヤーと責務))。ViewModel から UseCase や DAO を直接呼ばない。
- 依存は `KoinComponent` + `by inject()` で取るのが基本。状態を持たないクラスは `factory`、アプリ全体で 1 つの状態を持つものは `sharedModule` に `single` で登録する。
- ViewModel は `private val _xxx = MutableStateFlow(...)` + `val xxx: StateFlow<...> = _xxx.asStateFlow()` の形で状態を公開する。DB 由来の Flow は `stateIn` / `shareIn` (`WhileSubscribed(5000)`) で変換する。

### Room

- エンティティは `companion object` に `TABLE_NAME` と `COLUMN_*` の定数を持ち、`@Entity` / `@ColumnInfo` と DAO の `@Query` 文字列の両方でその定数を使う。
- `MusicApp` などの enum は **定数名がそのまま DB に保存される**。既存の enum 定数の名前を変えたり消したりすると、保存済みの行が読めなくなる。
- LIKE 検索に渡す文字列は `escapeLike()` してからワイルドカードを付け、クエリ側は `escape '\\'` を書く。

### Compose

- Composable は `modifier: Modifier = Modifier` を受け取り、ルート要素に渡す (Compose 用 detekt ルールの前提)。
- `@Preview` には必ず `group` (各モジュールの `*_GROUP` 定数) と `name` を付ける。Preview は VRT の撮影対象になる ([build-and-ci.md](build-and-ci.md#vrt-の仕組み))。
- Preview 用のダミーデータは各 presenter の `mock/MockData.kt` に置く。
- 画面は `WindowSizeClass` と画面の向きで Compact / Split2 のレイアウトを切り替える ([screens.md](screens.md#画面サイズへの対応))。

## よくある変更の手順

### 再生検知に対応する音楽アプリを増やす

1. [`MusicApp`](../data/src/main/java/com/snowdango/sumire/data/entity/MusicApp.kt) の該当する enum の `packageName` にアプリのパッケージ名を入れる。enum 自体が無いサービスなら、`apiProvider` と `platform` を song.link API の値に合わせて追加する。
2. enum を追加した場合:
   - `ui/src/main/res/drawable` にアイコンを置き、[`MusicAppImage`](../ui/src/main/java/com/snowdango/sumire/ui/component/MusicAppImage.kt) と [`MusicAppText`](../ui/src/main/java/com/snowdango/sumire/ui/component/MusicAppText.kt) の `when` に分岐を足す (`when` は網羅チェックされるので、足し忘れるとコンパイルエラーになる)。
   - [`UrlPriorityPlatform`](../data/src/main/java/com/snowdango/sumire/data/entity/preference/UrlPriorityPlatform.kt) にも同じ `platform` 文字列で追加する。
3. debug ビルドで実機再生し、Logcat の `CurrentMetadata` で mediaId やアートワークが取れているかを確認する。mediaId が song.link の `id` として通らないと `NOT_FOUND` になり、共有 URL が取れない。
4. [README.md](../README.md) / [README.ja.md](../README.ja.md) の対応サービス一覧を更新する。

### タブ (画面) を追加する

1. `presenter/<name>` モジュールを作り、[`settings.gradle.kts`](../settings.gradle.kts) に `include(":presenter:<name>")`、`:app` の `dependencies` に `implementation(project(":presenter:<name>"))` を足す。`build.gradle.kts` は既存の presenter (例: [`presenter/history/build.gradle.kts`](../presenter/history/build.gradle.kts)) をコピーして namespace を変える。
2. `XxxScreen` / `XxxViewModel` と、`viewModel { XxxViewModel() }` を持つ Koin モジュールを作り、[`SumireApp`](../app/src/main/java/com/snowdango/sumire/SumireApp.kt) の `modules(...)` に追加する。
3. [`MainScreen`](../app/src/main/java/com/snowdango/sumire/MainScreen.kt) の `ROUTE` enum に項目 (アイコン 2 種とラベル) を足し、`NavHost` に `composable(route = ROUTE.XXX.name) { ... }` を足す。
4. Preview を VRT に載せるため、`*_GROUP` 定数と `src/debug` の `@ShowkaseRoot` クラスを用意し、`kspDebug(libs.showkase.prosessor)` などの依存を入れる (既存 presenter と同じ構成)。

### DB スキーマを変更する

1. `data/entity/db` のエンティティ、`repository/dao` の DAO を変更 (追加) する。テーブルを増やす場合は [`SongsDatabase`](../repository/src/main/java/com/snowdango/sumire/repository/SongsDatabase.kt) の `entities` と DAO の `abstract val` にも足す。
2. `SongsDatabase` の `version` を上げる。
3. **Migration を書いて `Room.databaseBuilder(...)` に `addMigrations(...)` で登録する。** 現在は Migration の仕組みも `fallbackToDestructiveMigration` も無いので、version だけ上げると既存ユーザーの端末では DB を開く時点で例外になる。
4. ビルドで `repository/schemas/` にスキーマ JSON が出力される。Migration の検証や AutoMigration に必要なので、変更前後のスキーマをコミットしておく。
5. UseCase → Model の順に読み書きのメソッドを足す。

### 設定項目を追加する

1. [`SettingsUseCase`](../usecase/src/main/java/com/snowdango/sumire/usecase/setting/SettingsUseCase.kt) に Preferences のキーと get / edit を足し、`settingsFlow()` でも読み出す。
2. [`SettingsPreferences`](../data/src/main/java/com/snowdango/sumire/data/entity/SettingsPreferences.kt) にフィールドを足す。
3. [`SettingsModel`](../model/src/main/java/com/snowdango/sumire/model/SettingsModel.kt) → [`SettingsViewModel`](../presenter/settings/src/main/java/com/snowdango/sumire/settings/SettingsViewModel.kt) → [`SettingsScreen`](../presenter/settings/src/main/java/com/snowdango/sumire/settings/SettingsScreen.kt) の順に通す。選択ダイアログは `settings/component/` に置き、文言は `presenter/settings/src/main/res/values/strings.xml` に足す。

### ウィジェットに表示する項目を増やす

1. [`SmallArtworkWidget`](../presenter/widget/src/main/java/com/snowdango/sumire/widget/SmallArtworkWidget.kt) の companion object に Preferences のキーを足し、`provideGlance` で読む。
2. 状態を書き込む処理が **[`WidgetViewModel.update`](../presenter/widget/src/main/java/com/snowdango/sumire/widget/WidgetViewModel.kt) と [`PlayingSongWorker.update`](../presenter/widget/src/main/java/com/snowdango/sumire/widget/worker/PlayingSongWorker.kt) の 2 か所に重複している** ので、両方を直す。

### Analytics のイベントを増やす

[`LogEvent`](../infla/src/main/java/com/snowdango/sumire/infla/LogEvent.kt) の companion object にイベント名の定数、`Event` / `Param` enum に項目を足し、`logEvent.sendEvent(Event.XXX, mapOf(Param.YYY to "..."))` で送る。`LogEvent` は Koin の `factory` なので `by inject()` で取れる。

### バージョンを上げる

Claude Code で `/version-up` (patch を +1) または `/version-up 0.1.0` を実行する。詳細は [build-and-ci.md](build-and-ci.md#バージョン)。

## 実装上の注意点と既知の課題

コードを読んで確認できた、挙動に影響する点。修正するときはこの一覧も更新する。

### 動作に影響するもの

| 内容 | 場所 |
| --- | --- |
| 再生中画面の Recent の相対時刻 (`"3m ago"` など) は、基準時刻を Flow を作った時点 (ViewModel 生成時) で 1 回だけ計算している。そのため時間が経っても表示が更新されず、ViewModel 生成後に再生した曲は差分が負になって常に `now` と表示される | `GetHistoriesModel.getRecentHistoriesSongFlow` |
| 履歴画面のリスト構築で `for (index in 0 until histories.itemCount)` と全件に `histories[index]` でアクセスしている。`LazyPagingItems` は `get` されたインデックスを見て次のページを読み込むため、表示範囲に関係なく全ページを順に読み込む挙動になり、Paging の遅延読み込みが実質効いていない | `HistoryCompatScreen` / `HistorySplit2Screen` |
| アートワークが届かないまま 10 秒以内にスキップされた曲は履歴に保存されない | `PlayingSongSharedFlow` ([詳細](playback-detection.md#保存タイミング-handlestatechange)) |
| 再生中の曲はメモリ上にしか無く、プロセスが終了すると失われる。ウィジェットは次の Worker 実行で「情報なし」表示に戻る | `PlayingSongSharedFlow` |
| ウィジェットをタップしても、曲がまだ DB に保存されていない間 (保存待ちの最大 10 秒) や URL が無い曲では共有に失敗する | `ShareSongAction` / `ShareSongModel` |
| `saveData` は複数テーブルへの insert をトランザクション無しで行うので、途中で失敗すると中途半端な行が残る | `SaveModel.saveData` |
| アーティストは名前だけで同一判定するため、同名の別アーティストは 1 行にまとまる | `SaveModel.saveArtist` |
| 設定の選択ダイアログは、開いたとき現在の設定値が選択されていない | `UrlPriorityPlatformDialog` / `WidgetActionTypeDialog` |

### 将来の変更で踏みやすいもの

| 内容 | 場所 |
| --- | --- |
| Room の Migration が 1 つも無い。スキーマ JSON もコミットされていない ([手順](#db-スキーマを変更する)) | `SongsDatabase` |
| `PlayingSongSharedFlow.listener` は 1 つしか登録できず、現在はウィジェットが使っている。他から設定するとウィジェットが更新されなくなる | `PlayingSongSharedFlow` / `WidgetViewModel` |
| `MusicApp` の定数名は DB に保存されるため、名前の変更・削除は既存データを壊す | `MusicApp` / `Histories` / `AppSongKey` |
| `MusicApp.platform` と `UrlPriorityPlatform.platform` は同じ文字列を二重に定義している | `MusicApp` / `UrlPriorityPlatform` |
| ウィジェットの状態を書き込む処理が 2 か所に重複している | `WidgetViewModel.update` / `PlayingSongWorker.update` |
| namespace と Kotlin パッケージ名が一致しないモジュールがある ([一覧](architecture.md#モジュール構成)) | `:presenter:history` / `:presenter:settings` / `:presenter:widget` |

### 未使用・実態と合っていないもの

| 内容 | 場所 |
| --- | --- |
| `tasks` テーブルは API エラー時に書き込むだけで、読み出して再取得する処理は無い | `TasksUseCase` / `TasksDao` |
| Analytics の `save_song_data` は DB 保存時ではなく `ChangeCurrentSong` イベントのたびに送られる。一時停止・再開やアートワークの到着でも送られる | `SumireApp.initEventListener` |
| 初回の通知権限ダイアログは「ウィジェットのエラー表示用」と説明しているが、ウィジェットのエラー表示は通知を使っていない。アプリが出す通知はフォアグラウンドサービスの常駐通知だけ | `NotificationPostDialog` / `ShareSongFailureWorker` |
| `LogEvent.Event.FAILED_EVENT` と `SongListenerService.loggingMediaNotification()` はどこからも使われていない | `LogEvent` / `SongListenerService` |
| version catalog の `jetbrains-kotlin-android` プラグインはどのモジュールにも適用されていない | `gradle/libs.versions.toml` |
| UI 文言は文字列リソースと Kotlin への直書き (日本語) が混在している。`WidgetActionType.description` のように `:data` に表示文言を持つものもある | 各ダイアログ / `WidgetActionType` |

### 品質保証まわり

| 内容 | 場所 |
| --- | --- |
| detekt は `ignoreFailures = true` なので、指摘があっても CI は落ちない | ルート `build.gradle.kts` |
| ロジックのユニットテストはほぼ無く、回帰検知は Preview の VRT に頼っている | 各モジュールの `src/test` |
