---
title: WSL2 開発環境セットアップ
category: knowledge
tags: [wsl2, android-sdk, jdk, setup, environment]
related:
  - ../guide/build.md
  - ../guide/device-setup.md
---

# WSL2 開発環境セットアップ

WSL2 (Windows Subsystem for Linux 2) 上でVoiceTermをビルドするための環境構築手順。

## 前提条件

- Windows 10/11 + WSL2
- Ubuntu (推奨: 22.04 LTS以降)

## 1. JDK 17 のインストール

Android開発にはJDK 17が必要です。

```bash
sudo apt update
sudo apt install -y openjdk-17-jdk
```

インストール確認:

```bash
java -version
# openjdk version "17.0.x" と表示されればOK
```

## 2. Android SDK のインストール

### 2.1 Command Line Tools のダウンロード

```bash
mkdir -p ~/android-sdk
cd ~/android-sdk

# Command Line Tools をダウンロード
wget https://dl.google.com/android/repository/commandlinetools-linux-11076708_latest.zip -O cmdline-tools.zip
unzip cmdline-tools.zip

# ディレクトリ構造を修正
mkdir -p cmdline-tools/latest
mv cmdline-tools/bin cmdline-tools/lib cmdline-tools/NOTICE.txt cmdline-tools/source.properties cmdline-tools/latest/
```

### 2.2 ライセンスの承認

```bash
cd ~/android-sdk
echo "y" | bash cmdline-tools/latest/bin/sdkmanager --licenses
```

### 2.3 SDK コンポーネントのインストール

```bash
cd ~/android-sdk
bash cmdline-tools/latest/bin/sdkmanager \
    "platform-tools" \
    "platforms;android-35" \
    "build-tools;35.0.0"
```

必要に応じて追加コンポーネントをインストール:

```bash
# NDK (ネイティブコード用)
bash cmdline-tools/latest/bin/sdkmanager "ndk;28.2.13676358"

# CMake (ネイティブビルド用)
bash cmdline-tools/latest/bin/sdkmanager "cmake;3.22.1"
```

## 3. プロジェクト設定

### 3.1 local.properties の作成

プロジェクトルートに `local.properties` を作成:

```bash
cd ~/workspace/VoiceTerm
echo "sdk.dir=/home/$(whoami)/android-sdk" > local.properties
```

### 3.2 環境変数の設定 (オプション)

`~/.bashrc` に追加しておくと便利:

```bash
export ANDROID_HOME=~/android-sdk
export PATH=$PATH:$ANDROID_HOME/platform-tools
export PATH=$PATH:$ANDROID_HOME/cmdline-tools/latest/bin
```

反映:

```bash
source ~/.bashrc
```

## 4. ビルド確認

```bash
cd ~/workspace/VoiceTerm
./gradlew assembleDebug
```

初回ビルドは依存関係のダウンロードで時間がかかります（5〜10分程度）。

## ディレクトリ構成

```
~/
├── android-sdk/
│   ├── build-tools/
│   ├── cmdline-tools/
│   ├── cmake/
│   ├── licenses/
│   ├── ndk/
│   ├── platform-tools/
│   └── platforms/
└── workspace/
    └── VoiceTerm/
        ├── local.properties  (gitignore対象)
        └── ...
```

## トラブルシューティング

### JAVA_HOME が見つからない

```bash
export JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64
```

### SDK location not found

`local.properties` が正しく設定されているか確認:

```bash
cat local.properties
# sdk.dir=/home/your-username/android-sdk
```

### ライセンスエラー

```bash
cd ~/android-sdk
yes | bash cmdline-tools/latest/bin/sdkmanager --licenses
```

## 参考リンク

- [Android Command Line Tools](https://developer.android.com/studio/command-line)
- [sdkmanager](https://developer.android.com/studio/command-line/sdkmanager)
