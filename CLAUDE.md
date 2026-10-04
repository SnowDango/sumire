# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## プロジェクト概要

Sumire は、音楽アプリ (Apple Music / Spotify) の再生を通知から検知して履歴として保存する Android アプリ (Android 13 / API 33 以上)。再生中の曲を表示するホーム画面ウィジェットがあり、タップで曲の URL のコピーや X への共有ができる。Kotlin + Jetpack Compose のマルチモジュール構成。

実装の詳細は [`docs/`](docs/README.md) にまとめてある。コードを変更したら、関係する docs も合わせて更新すること。

## コマンド

JDK 21 が必要。ビルドには gitignore 済みの `app/src/debug/google-services.json` (release は `app/src/release/google-services.json` と `release.jks` / `siging.properties`) が要る。CI では Secrets から生成している ([docs/build-and-ci.md](docs/build-and-ci.md))。

```bash
./gradlew assembleDebug                 # debug ビルド (applicationId は com.snowdango.sumire.debug)
./gradlew testDebugUnitTest             # 全モジュールの JVM テスト
./gradlew :model:testDebugUnitTest --tests "com.snowdango.sumire.model.ExampleUnitTest"  # 単体のテストクラス
./gradlew detekt                        # 静的解析。autoCorrect = true なのでソースが自動整形される点に注意
./gradlew lint                          # Android Lint (ライブラリモジュールは abortOnError = true)
./gradlew recordRoborazziDebug          # 全 Preview のスクリーンショットを app/build/outputs/roborazzi に保存
./gradlew compareRoborazziDebug         # 保存済みスクリーンショットとの比較
```

- detekt は `ignoreFailures = true` なので、指摘があってもタスクも CI も失敗しない。出力を読んで判断する。
- ロジックのユニットテストはほぼ無い (各モジュールの `ExampleUnitTest` はテンプレートのまま)。回帰検知は `app/src/test/.../PreviewTest.kt` の VRT (Showkase で集めた全 `@Preview` を Roborazzi で撮影) が担っている。

## アーキテクチャ

### データの流れ

1. `SongListenerService` (`:app`, `NotificationListenerService`) が `MusicApp.packageName` に一致するアプリの通知を受け、`MediaSessionManager` からメタデータを読んで `PlayingSongSharedFlow.changeSong(queueId, data)` を呼ぶ。
2. `PlayingSongSharedFlow` (`:infla`, Koin の single) が再生中の曲を **メモリ上だけ** に保持する。変化があれば `listener` (ウィジェット用、1 つしか持てない) と `EventSharedFlow` の `ChangeCurrentSong` (画面・Analytics 用) に通知する。
3. アートワーク付きのメタデータが揃ったら即、揃わなければ 10 秒待って `SaveModel.saveSong()` を呼ぶ。`SaveModel` は `AppSongKey` に `(mediaId, app)` があれば履歴だけ追加し、無ければ song.link API で各サービスの URL を取ってから Room に保存する。
4. 画面は Room を Paging / Flow で読む。ウィジェット (Glance) はタップ時に `ShareSongModel` で `AppSongKey` から URL を解決する。

詳細: [docs/playback-detection.md](docs/playback-detection.md), [docs/persistence.md](docs/persistence.md), [docs/widget.md](docs/widget.md)

### レイヤーとモジュール

依存の向きは Presenter (`:presenter:*`, `:app`) → `:model` → `:usecase` → `:repository` (Room / Ktor) → `:data`。ViewModel から UseCase や DAO を直接呼ばない。`:infla` は横断的な位置付けで、`:model` の `SaveModel` を呼ぶ。直感に反する依存として `:model` → `:ui` (`SongCardViewData` への変換のため) がある。依存はすべて `implementation` なので推移的には見えない。

- DI は Koin。各モジュールの `KoinModule.kt` を [`SumireApp`](app/src/main/java/com/snowdango/sumire/SumireApp.kt) の `startKoin { modules(...) }` に登録しないと解決できない。アプリ全体の共有物 (DB, API, DataStore, アプリスコープ, `PlayingSongSharedFlow` など) は `SumireApp` 内の `sharedModule` にある。
- 依存はコンストラクタではなく `KoinComponent` + `by inject()` で取るのが基本。Model / UseCase は `factory` なので状態を持たせない。
- `:presenter:history` / `:presenter:settings` / `:presenter:widget` は Android namespace と Kotlin パッケージ名が一致しない (例: settings はパッケージ `com.snowdango.sumire.settings`、`R` / `BuildConfig` は `com.snowdango.sumire.presenter.settings`)。新規ファイルは既存ファイルのパッケージに合わせる。

詳細: [docs/architecture.md](docs/architecture.md), [docs/screens.md](docs/screens.md)

## 変更時の注意

- `MusicApp` などの enum は Room に **定数名の文字列** で保存される。既存定数の名前変更・削除は保存済みデータを壊す。`MusicApp.platform` / `apiProvider` は song.link API の値と一致させる必要があり、`UrlPriorityPlatform.platform` とも揃える。
- Room (`SongsDatabase`, version 3) には Migration も `fallbackToDestructiveMigration` も無い。スキーマを変えるときは version を上げ、Migration を書いて `addMigrations` に登録する。スキーマ JSON は `repository/schemas/` に出力される。
- ウィジェットの状態を書き込む処理は `WidgetViewModel.update` と `PlayingSongWorker.update` の 2 か所に重複しているので、両方直す。
- `PlayingSongSharedFlow.listener` はウィジェットが使っている。他から代入するとウィジェットが更新されなくなるので、画面側の通知には `EventSharedFlow` を使う。
- `@Preview` には各モジュールの `*_GROUP` 定数で `group` を付ける。Preview は VRT の撮影対象で、変更すると PR に差分画像がコメントされる。
- コルーチン内で `Exception` を catch するときは先に `CancellationException` を再スローする (既存コードの書き方)。コメントは日本語で「なぜ」を書く。
- 既知の課題の一覧は [docs/development-guide.md](docs/development-guide.md#実装上の注意点と既知の課題) にある。

## ブランチと PR

- 作業ブランチは `develop` から切り、PR も `develop` 宛てにする (`master` へはリリース時に develop からマージ)。PR 本文は `.github/PULL_REQUEST_TEMPLATE.md` の「概要」「変更点」の形式。
- バージョンは `gradle/libs.versions.toml` の `versionName` / `versionCode`。上げるときは `/version-up` スキル (`.claude/skills/version-up/SKILL.md`) を使う。
