---
name: version-up
description: リリース用にアプリのバージョンを上げて develop ブランチに PR を作成する。gradle/libs.versions.toml の versionName と versionCode を bump し、feature/version-up/<version> ブランチで commit & push、base=develop で gh pr create する。引数なしなら patch を自動 +1、引数で指定すれば任意の versionName を使う。
user-invocable: true
allowed-tools:
  - Read
  - Edit
  - Bash(git *)
  - Bash(gh *)
  - Bash(grep *)
  - Bash(sed *)
---

# /version-up — リリース用バージョンアップ PR 作成

## 目的

リリースに向けて `gradle/libs.versions.toml` の `versionName` と `versionCode` を bump し、`feature/version-up/<version>` ブランチを切って `develop` 宛の PR を作る。

引数: `$ARGUMENTS`

- 引数なし → 現在の `versionName` の patch を +1 (例: `0.0.3` → `0.0.4`)
- 引数あり → その値を新しい `versionName` として使う (例: `/version-up 0.1.0`)

`versionCode` は常に +1 する。

---

## 手順

### 1. 事前チェック

1. `git status --porcelain` を実行。出力が空でなければ未コミットの変更があるので、ユーザーに確認してから中断する (勝手に stash しない)。
2. `git fetch origin` で最新を取得。
3. 現在のブランチ名を控える (失敗時に戻すため)。

### 2. 現バージョンの取得

`gradle/libs.versions.toml` の先頭付近にある以下の行を Read で読む:



- `versionCode` の現在値 (整数文字列) を控える
- `versionName` の現在値 (`X.Y.Z` 形式) を控える

### 3. 新バージョンの決定

- **引数あり (`$ARGUMENTS` が空でない)**: trim した値を新 `versionName` とする。`X.Y.Z` (各セグメント数値) のフォーマットを満たしているか正規表現で検証し、満たさなければエラーで停止。
- **引数なし**: `versionName` を `.` で split し、末尾セグメントを整数として +1 する。例: `0.0.3` → `0.0.4`、`1.2.9` → `1.2.10`。

新 `versionCode` = 旧 `versionCode` を整数化して +1。

### 4. ブランチ作成

```bash
git checkout develop
git pull --ff-only origin develop
git checkout -b feature/version-up/<新versionName>
```

すでに同名ローカルブランチがある場合は、ユーザーに確認してから削除 or 別名で進める (勝手に上書きしない)。

### 5. ファイル編集

`gradle/libs.versions.toml` を Edit で書き換える:

- `versionCode = "<旧>"` → `versionCode = "<新>"`
- `versionName = "<旧>"` → `versionName = "<新>"`

行は `[versions]` ブロック直下にあり、ファイル中で一意。`replace_all` は使わず、片方ずつ Edit する。

### 6. コミット

```bash
git add gradle/libs.versions.toml
git commit -m "version <新versionName>"
```

過去の PR #186 ("version 0.0.3" 等) と同じ慣習で、コミットメッセージは小文字 `version <X.Y.Z>` のみ。Co-Authored-By 行は付けない (過去のリリースコミットに付いていないため、ログの一貫性を優先)。

### 7. プッシュ & PR

```bash
git push -u origin feature/version-up/<新versionName>
```

`gh pr create` で `develop` 宛 PR を作成:

```bash
gh pr create --base develop --head feature/version-up/<新versionName> \
  --title "Version <新versionName>" \
  --body "$(cat <<'EOF'
## 概要

- Version <新versionName>

## 変更点

- version up
EOF
)"
```

### 8. 完了報告

作成された PR の URL をユーザーに伝える。

---

## 注意事項

- **base ブランチは必ず `develop`** (master ではない)。このリポジトリでは develop → master の流れで release する。
- `versionCode` を bump し忘れると Play Store にアップロードできないので必ず両方更新する。
- 既に同 `versionName` の PR/ブランチがリモートにある場合は、`gh pr list --head feature/version-up/<version>` で確認してからユーザーに判断を仰ぐ。
- 失敗してコミット前で詰まったら、最初に控えたブランチに `git checkout` で戻し、作業ブランチを `git branch -D` で消して状態を元に戻す。コミット後の失敗 (push 失敗等) は自動で戻さず、ユーザーに状況を報告する。
