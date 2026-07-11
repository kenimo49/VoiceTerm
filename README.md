<p align="center">
  <img src="icons/icon-web.png" alt="VoiceTerm" width="128" height="128">
</p>

# VoiceTerm

[![License: Apache 2.0](https://img.shields.io/badge/License-Apache_2.0-blue.svg)](./LICENSE)
[![Platform: Android](https://img.shields.io/badge/Platform-Android-3ddc84.svg)](https://developer.android.com/)
[![Status: Alpha](https://img.shields.io/badge/Status-Alpha%20(pre--release)-orange.svg)](#roadmap)

日本語入力と音声認識に対応した Android向けSSHクライアント。カフェからスマホで自宅PCのClaude Codeを操作する、モバイルAIペアプロを実現するために ConnectBot をフォークしてリブランドしたプロジェクト。

> **EN**: Android SSH client with Japanese input & voice recognition — built to pair with Claude Code from a smartphone. Forked from ConnectBot (Apache 2.0).

## Features

- **SSH / Telnet / Mosh 接続** — ConnectBot ベースの安定した接続
- **日本語入力** — 非モーダル入力バーで IME 連携問題を解消 (`FloatingTextInputDialog.kt`)
- **音声認識** — Android `SpeechRecognizer` API、自動送信タイムアウト付き
- **Claude Code フレンドリー** — スマホからAIペアプログラミング
- **独自ブランディング** — ティール背景 + マイク + 音声波形アイコン

## ステータス

現在は **Phase 1 MVP 完了** の段階で、Google Play への公開は準備中。動かすには source からビルドする必要あり。

- ✅ Phase 1 (MVP): 日本語入力・音声認識・ブランディング
- ⬜ Phase 2: Google Play 公開
- ⬜ Phase 3: 広告・課金

詳細は [ROADMAP.md](./ROADMAP.md) / [CHANGELOG.md](./CHANGELOG.md) を参照。

## ビルドして試す

```bash
git clone https://github.com/kenimo49/VoiceTerm.git
cd VoiceTerm

# Debug ビルド (APK を実機に転送)
./gradlew assembleDebug

# Release ビルド
./gradlew assembleRelease

# ユニットテスト
./gradlew test
```

### 開発要件

- Android Studio (最新 stable)
- Android SDK 21+
- Kotlin 1.9+

## 開発者向け

- 内部構造・設計思想は [CLAUDE.md](./CLAUDE.md)
- 4層構成のドキュメント (guide / knowledge / design / references / flows) は [docs/README.md](./docs/README.md)

## License

Apache License 2.0

This project is forked from [ConnectBot](https://github.com/connectbot/connectbot).

```
Copyright 2007-2024 ConnectBot Contributors
Copyright 2024 VoiceTerm Contributors

Licensed under the Apache License, Version 2.0
```

## Acknowledgments

- [ConnectBot](https://github.com/connectbot/connectbot) — Original SSH client for Android
- ConnectBot Contributors — For building an excellent open source SSH client
