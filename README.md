# VoiceTerm

SSH client with Japanese input & voice recognition for Android.

カフェからスマホで自宅PCを操作。日本語入力と音声認識で、モバイル開発体験を実現します。

## Features

- **SSH接続** - ConnectBotベースの安定したSSH/Telnet/Mosh接続
- **日本語入力対応** - 非モーダル入力バーでIMEとの連携問題を解決
- **音声認識** - SpeechRecognizerによる音声コマンド入力（自動送信タイムアウト付き）
- **Claude Code連携** - スマホからAIペアプログラミング
- **独自アイコン** - ティール背景 + マイク + 音声波形でConnectBotと差別化

## Screenshots

(Coming soon)

## Install

### Google Play

(Coming soon)

### Build from source

```bash
git clone https://github.com/kenimo49/VoiceTerm.git
cd VoiceTerm
./gradlew assembleDebug
```

## Development

### Requirements

- Android Studio (latest stable)
- Android SDK 21+
- Kotlin 1.9+

### Build

```bash
# Debug build
./gradlew assembleDebug

# Release build
./gradlew assembleRelease

# Run tests
./gradlew test
```

詳細は [CLAUDE.md](CLAUDE.md) を参照してください。

## Roadmap

- [x] リポジトリ作成
- [x] ブランディング（アプリ名・パッケージ名・アイコン）
- [x] 日本語入力UI実装（非モーダル入力バー）
- [x] 音声認識実装（SpeechRecognizer + 自動送信）
- [ ] Google Play公開
- [ ] 広告・課金実装

詳細は [ROADMAP.md](ROADMAP.md) を参照してください。

## License

Apache License 2.0

This project is forked from [ConnectBot](https://github.com/connectbot/connectbot).

```
Copyright 2007-2024 ConnectBot Contributors
Copyright 2024 VoiceTerm Contributors

Licensed under the Apache License, Version 2.0
```

## Acknowledgments

- [ConnectBot](https://github.com/connectbot/connectbot) - Original SSH client for Android
- ConnectBot Contributors - For building an excellent open source SSH client
