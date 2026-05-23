# Sumire

<div align="center">
  <img src="https://raw.githubusercontent.com/SnowDango/sumire/refs/heads/master/app/src/main/res/mipmap-xxxhdpi/ic_launcher_round.webp">
</div>

<div align="center">

[English](README.md) | [日本語](README.ja.md)

</div>

## このアプリについて

このアプリは、音楽サービスで再生した曲を取得し履歴として保存します。
現在再生している曲や過去に再生した曲を履歴として見ることができます。
また、ウィジェット機能も使用でき、現在再生している曲の表示、URL のコピー、X へのシェアを簡単にすることができます。

## 動作環境

- **Android 13 (API 33) 以上**

## 対応音楽サービス

### 再生検知

通知から再生中の楽曲を検知できる音楽アプリ:

- Apple Music
- Spotify

### URL の取得・シェア

検知した楽曲について、以下のプラットフォームでの URL 取得・シェアに対応しています:

- Apple Music
- Spotify
- YouTube Music
- Google
- Amazon Music
- Deezer
- Tidal
- Pandora
- Napster
- Yandex

## 使用方法

[Releases](https://github.com/SnowDango/sumire/releases) から最新の apk を取得してインストールしてください。
インストール後にアプリを起動したら、権限の要求がされるので表示に従って権限を付与してください。

### 必要権限

- **通知の取得** — 現在再生している曲の取得に使用しています。この権限がない場合、アプリを使用できません。
- **通知の表示** — 必須ではありません。ただし、権限がない場合ウィジェットでのエラー表示が表示されなくなります。
