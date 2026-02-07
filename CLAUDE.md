# VoiceTerm - Development Instructions

## Project Overview

VoiceTermは、日本語入力と音声認識に対応したAndroid向けSSHクライアントです。
ConnectBotをベースにフォークし、独自の入力機能を追加しています。

## Tech Stack

- Language: Kotlin (primary), Java (legacy)
- Min SDK: 21 (Android 5.0)
- Build: Gradle (Kotlin DSL)
- Base: ConnectBot (Apache 2.0)

## Project Structure

```
app/
├── src/main/
│   ├── java/org/connectbot/    # メインソースコード
│   ├── res/                     # リソース (layouts, strings, etc.)
│   └── AndroidManifest.xml
├── build.gradle.kts
fastlane/                        # リリース自動化 (後で追加)
```

## Key Files

- `app/src/main/java/org/connectbot/ConsoleActivity.java` - ターミナル画面
- `app/src/main/java/org/connectbot/TerminalView.java` - ターミナル描画
- `app/src/main/res/layout/` - UIレイアウト

## Development Rules

### Code Style
- Kotlinを優先、新規コードはKotlinで書く
- 既存のJavaコードは必要に応じてKotlinに移行
- ConnectBotのコードスタイルを尊重

### Git Workflow
- mainブランチに直接push OK (個人開発)
- 機能追加時はfeatureブランチ推奨
- コミットメッセージは日本語OK

### Testing
- 実機テスト必須 (エミュレータでSSHは動作確認しにくい)
- 日本語入力は複数のIMEでテスト (Gboard, Google日本語入力等)

## Features to Implement

### 1. Japanese Input (日本語入力対応)
- 専用の入力欄UIを追加
- IMEで確定後にSSHへ送信
- ターミナル直接入力との切り替え

### 2. Voice Recognition (音声認識)
- AndroidのSpeech-to-Text API使用
- またはVosk/Whisperでオフライン対応
- 一定回数まで無料、以降は課金

### 3. Monetization (収益化)
- AdMobバナー広告
- 広告非表示 (課金)
- 音声入力無制限 (課金)

## Build Commands

```bash
# Debug build
./gradlew assembleDebug

# Release build
./gradlew assembleRelease

# Install to device
./gradlew installDebug
```

## Environment Setup

1. Android Studio (latest stable)
2. Android SDK 21+
3. 実機またはエミュレータ
4. Fastlane (リリース時)

## Notes

- ConnectBotのライセンス表記を維持すること (Apache 2.0)
- 課金実装時はGoogle Play Billing Library使用
- リリースはFastlane経由で自動化
