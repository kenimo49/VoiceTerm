---
title: 実機デバッグ セットアップガイド
category: guide
tags: [debug, adb, usb, wsl2, device]
related:
  - build.md
  - ../knowledge/wsl-setup.md
---

# 実機デバッグ セットアップガイド (WSL2 + USB)

WSL2環境からAndroid実機にUSB接続してデバッグする方法を説明します。

## 前提条件

- WSL2 (Ubuntu 24.04)
- Android SDK (Windows側: `%LOCALAPPDATA%\Android\Sdk`)
- Android端末 (USB接続)

## 動作確認済み環境

| 項目 | 内容 |
|------|------|
| ホストOS | Windows (WSL2) |
| WSL | Ubuntu 24.04 |
| 端末 | Pixel 3a |
| Android | 12 (API 32) |

## 接続方式について

WSL2からAndroid実機に接続する方式は複数ありますが、
**Windows側の `adb.exe` をWSL2から呼び出す方式**が最も安定しています。

| 方式 | 状態 | 備考 |
|------|------|------|
| Windows `adb.exe` 経由 | **推奨** | 安定動作 |
| usbipd-win (USBフォワード) | 非推奨 | WSL2カーネルとの相性問題あり |
| ワイヤレスデバッグ | 非推奨 | プロトコルエラーが発生 |

## 1. Android端末の準備

### 開発者オプションを有効化

1. **設定 → デバイス情報 → ビルド番号** を7回タップ
2. 「開発者になりました」と表示される

### USBデバッグを有効化

1. **設定 → システム → 開発者オプション**
2. **USBデバッグ** を ON

## 2. Windows側: ADBの確認

Android StudioをインストールしていればADBは含まれています。

```powershell
# ADBの場所を確認
where adb
# 通常: %LOCALAPPDATA%\Android\Sdk\platform-tools\adb.exe
```

## 3. WSL2側: adbエイリアスの設定

Windows側の `adb.exe` をWSL2から簡単に呼び出せるようにします。

`~/.bashrc` に追加:

```bash
# Android SDK (Windows側のadbを使用)
alias adb='"/mnt/c/Users/<ユーザー名>/AppData/Local/Android/Sdk/platform-tools/adb.exe"'
```

反映:

```bash
source ~/.bashrc
adb version  # 動作確認
```

> **Note**: WSL2側にもAndroid SDKがある場合（ビルド用）、PATH設定も追加:
> ```bash
> export ANDROID_HOME="$HOME/android-sdk"
> export PATH="${PATH}:${ANDROID_HOME}/platform-tools:${ANDROID_HOME}/tools/bin"
> ```
> ただし、実機接続にはWindows側の `adb.exe` を使用してください。

## 4. USBデバイスの接続

### 4-1. USB接続 & デバッグ許可

1. Android端末をUSBケーブルでPCに接続
2. 端末の画面に「USBデバッグを許可しますか？」ダイアログが表示される
3. **「このコンピュータを常に許可する」** にチェックを入れて **許可** をタップ

### 4-2. 接続確認

WSL2で実行:

```bash
adb devices
```

正常な出力:

```
List of devices attached
02VAYV1QJC	device
```

### 4-3. WSL側のadbサーバーとの競合に注意

WSL側の `adb` (Linux版) のサーバーが起動していると、Windows側の `adb.exe` が
ポート5037を使えずに失敗します。

```
could not read ok from ADB Server
* failed to start daemon
```

この場合、WSL側のadbサーバーを停止してください:

```bash
# WSL側のadbサーバーを停止
~/android-sdk/platform-tools/adb kill-server

# Windows側のadbで再試行
adb devices
```

## 5. アプリのビルド・インストール・起動

```bash
# ビルド (WSL2のGradleで実行)
cd ~/workspace/VoiceTerm
./gradlew assembleGoogleDebug

# インストール (Windows adb.exe 経由)
adb install app/build/outputs/apk/google/debug/app-google-debug.apk

# 起動
adb shell am start -n org.connectbot.debug/org.connectbot.ui.MainActivity

# スクリーンショット取得
adb exec-out screencap -p > screenshot.png
```

> **Tip**: WSL2のパスをWindows形式に変換してadbに渡す場合:
> ```bash
> adb install "$(wslpath -w app/build/outputs/apk/google/debug/app-google-debug.apk)"
> ```

## トラブルシューティング

### `unauthorized` エラー

```
02VAYV1QJC	unauthorized
```

→ Android端末の画面でUSBデバッグの許可ダイアログを承認してください。

### `no devices/emulators found`

→ USBケーブルを抜き差しして再接続してください。
→ 端末のUSBモードが「充電のみ」になっていないか確認してください。

### adbデーモンの起動に失敗する

```
could not read ok from ADB Server
* failed to start daemon
```

→ WSL側とWindows側のadbサーバーが競合しています。上記「4-3」を参照。

### APKインストール時のパスエラー

WSL2のLinuxパスはWindows側の `adb.exe` から直接読めません。
`wslpath -w` でWindows形式に変換してください:

```bash
adb install "$(wslpath -w /home/ken/workspace/VoiceTerm/app/build/outputs/apk/google/debug/app-google-debug.apk)"
```
