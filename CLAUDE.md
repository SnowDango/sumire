# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## プロジェクト概要

Sumire は、音楽アプリ (Apple Music / Spotify) の再生を通知から検知して履歴として保存する Android アプリ (Android 13 / API 33 以上)。画面は再生中・履歴・レポート (当月の集計)・設定の 4 タブ。再生中の曲を表示するホーム画面ウィジェットがあり、タップで曲の URL のコピーや X への共有ができる。Kotlin + Jetpack Compose のマルチモジュール構成。

実装の詳細は [`docs/`](docs/README.md) にまとめてある。実装を変えたら docs も同じ PR で更新する ([docs の更新](#docs-の更新))。

## コマンド

JDK 21 が必要。ビルドには gitignore 済みの `app/src/debug/google-services.json` (release は `app/src/release/google-services.json` と `release.jks` / `siging.properties`) が要る。CI では Secrets から生成している ([docs/build-and-ci.md](docs/build-and-ci.md))。

```bash
./gradlew assembleDebug                 # debug ビルド (applicationId は com.snowdango.sumire.debug)
./gradlew testDebugUnitTest             # 全モジュールの JVM テスト
./gradlew :model:testDebugUnitTest --tests "com.snowdango.sumire.model.GetReportModelTest"  # 単体のテストクラス
./gradlew detekt                        # 静的解析。autoCorrect = true なのでソースが自動整形される点に注意
./gradlew lint                          # Android Lint (ライブラリモジュールは abortOnError = true)
./gradlew updateDebugScreenshotTest     # @PreviewTest の Preview を撮影し各モジュールの src/screenshotTestDebug/reference/ に保存 (gitignore 済み)
./gradlew validateDebugScreenshotTest   # 手元の参照画像との比較
```

- detekt は `ignoreFailures = true` なので、指摘があってもタスクも CI も失敗しない。出力を読んで判断する。
- ロジックのユニットテストは `model` の `GetReportModelTest` と `infla` の `ListeningSessionTest` だけ (他の `ExampleUnitTest` はテンプレートのまま)。画面の回帰検知は Compose Preview Screenshot Testing による VRT が担い、CI が PR 先ブランチの画像と比較して PR にコメントする。

## アーキテクチャ

### データの流れ

1. `SongListenerService` (`:app`, `NotificationListenerService`) が `MusicApp.packageName` に一致するアプリの通知と、そのアプリの MediaSession の `MediaController.Callback` を受け、`MediaSessionManager` からメタデータを読んで `PlayingSongSharedFlow.changeSong(queueId, data)` を呼ぶ。
2. `PlayingSongSharedFlow` (`:infla`, Koin の single) が再生中の曲を **メモリ上だけ** に保持する。変化があれば `listener` (ウィジェット用、1 つしか持てない) と `EventSharedFlow` の `ChangeCurrentSong` (画面・Analytics 用) に通知する。
3. アートワーク付きのメタデータが揃ったら即、揃わなければ 10 秒待って `SaveModel.saveSong()` を呼ぶ。`SaveModel` は `AppSongKey` に `(mediaId, app)` があれば履歴だけ追加し、無ければ song.link API で各サービスの URL を取ってから Room に保存する。戻り値は追加した履歴の ID。
4. 再生時間は `PlayingSongSharedFlow` が曲ごとの `ListeningSession` で `isActive` だった区間を測り、一時停止・曲の切り替え・流し続けて 5 分ごとに `SaveModel.addListeningTime()` で `histories.listening_ms` に足し込む (履歴の ID が決まるまでアプリスコープで待つ)。新しい履歴は 0 で作り、`null` は再生時間を記録し始める前の履歴だけ。
5. 画面は Room を Paging / Flow で読む。レポートタブは `HistoriesDao` の集計クエリ (期間内の再生回数・再生時間の合計・曲 / アーティストのランキング) を `GetReportModel` で当月分にまとめる。ウィジェット (Glance) はタップ時に `ShareSongModel` で `AppSongKey` から URL を解決する。

詳細: [docs/playback-detection.md](docs/playback-detection.md), [docs/persistence.md](docs/persistence.md), [docs/widget.md](docs/widget.md)

### レイヤーとモジュール

依存の向きは Presenter (`:presenter:*`, `:app`) → `:model` → `:usecase` → `:repository` (Room / Ktor) → `:data`。ViewModel から UseCase や DAO を直接呼ばない。`:infla` は横断的な位置付けで、`:model` の `SaveModel` を呼ぶ。直感に反する依存として `:model` → `:ui` (`SongCardViewData` への変換のため) がある。依存はすべて `implementation` なので推移的には見えない。

- DI は Koin。各モジュールの `KoinModule.kt` を [`SumireApp`](app/src/main/java/com/snowdango/sumire/SumireApp.kt) の `startKoin { modules(...) }` に登録しないと解決できない。アプリ全体の共有物 (DB, API, DataStore, アプリスコープ, `PlayingSongSharedFlow` など) は `SumireApp` 内の `sharedModule` にある。
- 依存はコンストラクタではなく `KoinComponent` + `by inject()` で取るのが基本。Model / UseCase は `factory` なので状態を持たせない。
- `:presenter:history` / `:presenter:settings` / `:presenter:widget` は Android namespace と Kotlin パッケージ名が一致しない (例: settings はパッケージ `com.snowdango.sumire.settings`、`R` / `BuildConfig` は `com.snowdango.sumire.presenter.settings`)。新規ファイルは既存ファイルのパッケージに合わせる。

詳細: [docs/architecture.md](docs/architecture.md), [docs/screens.md](docs/screens.md)

## 変更時の注意

- `MusicApp` などの enum は Room に **定数名の文字列** で保存される。既存定数の名前変更・削除は保存済みデータを壊す。`MusicApp.platform` / `apiProvider` は song.link API の値と一致させる必要があり、`UrlPriorityPlatform.platform` とも揃える。
- Room (`SongsDatabase`, version 4) は `fallbackToDestructiveMigration` を使っていない。スキーマを変えるときは version を上げ、`repository/.../migration/Migrations.kt` に Migration を書いて `addMigrations` に登録する。スキーマ JSON は `repository/schemas/` に出力される (まだコミットされていない)。
- ウィジェットの状態を書き込む処理は `WidgetViewModel.update` と `PlayingSongWorker.update` の 2 か所に重複しているので、両方直す。
- `PlayingSongSharedFlow.listener` はウィジェットが使っている。他から代入するとウィジェットが更新されなくなるので、画面側の通知には `EventSharedFlow` を使う。
- `@Preview` には各モジュールの `PreviewGroup.kt` の `*_GROUP` 定数で `group` を付ける。VRT が撮影するのは `src/screenshotTest/kotlin/` にある `@PreviewTest` のラッパーだけなので、Preview を足したらラッパーも足す (`@Preview` のパラメータは main 側と揃える)。
- モジュールを追加したら、`settings.gradle.kts` と `SumireApp` の Koin 登録に加えて、`.circleci/config.yml` の `build` ジョブの `persist_to_workspace` にもその `build` ディレクトリを足す。
- コルーチン内で `Exception` を catch するときは先に `CancellationException` を再スローする (既存コードの書き方)。コメントは日本語で「なぜ」を書く。
- 既知の課題の一覧は [docs/development-guide.md](docs/development-guide.md#実装上の注意点と既知の課題) にある。

## docs の更新

実装の変更が `docs/` に書かれている内容 (挙動・クラス名・ファイルパス・設定値・手順・図など) に影響する場合は、**同じ PR の中で該当する docs も更新する**。作業の最後に、変更したファイルが下の表のどれに当たるかを確認し、記述が実装と食い違っていないか読み直すこと。docs の内容に影響しない変更 (内部のリファクタや文言の微修正など) では更新しなくてよい。

| 変更した内容 | 更新するドキュメント |
| --- | --- |
| モジュールの追加・削除、モジュール間の依存、Koin の登録、コルーチンのスコープ、技術スタックとそのバージョン | [docs/architecture.md](docs/architecture.md) |
| 通知リスナー (`SongListenerService`)、`PlayingSongSharedFlow` / `EventSharedFlow`、再生検知の対象アプリ (`MusicApp.packageName`) | [docs/playback-detection.md](docs/playback-detection.md) |
| `SaveModel` の保存フロー、song.link API、Room のエンティティ / DAO / DB version、読み出し・集計の Model、DataStore の設定キー | [docs/persistence.md](docs/persistence.md) |
| 画面・タブ・ダイアログ、`:ui` の共通コンポーネントやテーマ、Preview とスクリーンショットテストの構成 | [docs/screens.md](docs/screens.md) |
| ウィジェット、Worker、タップ時の共有処理 (`ShareSongAction`) | [docs/widget.md](docs/widget.md) |
| Gradle の設定、署名、静的解析、テスト、CircleCI / GitHub Actions、VRT の流れ、Secrets、リリース手順 | [docs/build-and-ci.md](docs/build-and-ci.md) |
| コーディング規約、よくある変更の手順、既知の課題 (直したら表から消す、新しく見つけたら足す) | [docs/development-guide.md](docs/development-guide.md) |
| ドキュメントの追加・削除、全体の流れ、用語 | [docs/README.md](docs/README.md) |
| コマンド、データの流れの要約、変更時の注意、この表 | この `CLAUDE.md` |

- docs と実装が食い違っていたら実装が正しい。気づいた時点で docs を実装に合わせる。
- 見出しを変えるとアンカーリンク (`docs/xxx.md#見出し`) が切れるので、参照元も直す。mermaid の図も実装に合わせて更新する。

## ブランチと PR

- 作業ブランチは `develop` から切り、PR も `develop` 宛てにする (`master` へはリリース時に develop からマージ)。PR 本文は `.github/PULL_REQUEST_TEMPLATE.md` の「概要」「変更点」の形式。
- バージョンは `gradle/libs.versions.toml` の `versionName` / `versionCode`。上げるときは `/version-up` スキル (`.claude/skills/version-up/SKILL.md`) を使う。
