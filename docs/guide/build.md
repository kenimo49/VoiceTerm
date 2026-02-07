# ビルドガイド

VoiceTermのビルド方法について説明します。

## 前提条件

- JDK 17
- Android SDK
- `local.properties` が設定済み

環境構築については [WSL2 開発環境セットアップ](../knowledge/wsl-setup.md) を参照。

## ビルドバリアント

VoiceTermには2つのビルドバリアントがあります:

| バリアント | 説明 | サイズ |
|-----------|------|--------|
| `google` | Google Play Services版 | 小さい |
| `oss` | オープンソース版（暗号化ライブラリ内蔵） | 大きい |

## デバッグビルド

### 全バリアントをビルド

```bash
./gradlew assembleDebug
```

### 特定バリアントのみ

```bash
# Google版のみ
./gradlew assembleGoogleDebug

# OSS版のみ
./gradlew assembleOssDebug
```

### 出力先

```
app/build/outputs/apk/
├── google/debug/
│   └── app-google-debug.apk
└── oss/debug/
    └── app-oss-debug.apk
```

## リリースビルド

### 署名鍵の準備

リリースビルドには署名鍵が必要です。

#### 1. キーストアの作成（初回のみ）

```bash
keytool -genkey -v -keystore voiceterm-release.keystore \
    -alias voiceterm \
    -keyalg RSA \
    -keysize 2048 \
    -validity 10000
```

#### 2. キーストア情報の設定

`keystore.properties` をプロジェクトルートに作成（gitignore対象）:

```properties
storeFile=/path/to/voiceterm-release.keystore
storePassword=your-store-password
keyAlias=voiceterm
keyPassword=your-key-password
```

#### 3. build.gradle.kts の設定

```kotlin
// app/build.gradle.kts
android {
    signingConfigs {
        create("release") {
            val keystorePropertiesFile = rootProject.file("keystore.properties")
            if (keystorePropertiesFile.exists()) {
                val keystoreProperties = Properties()
                keystoreProperties.load(FileInputStream(keystorePropertiesFile))
                storeFile = file(keystoreProperties["storeFile"] as String)
                storePassword = keystoreProperties["storePassword"] as String
                keyAlias = keystoreProperties["keyAlias"] as String
                keyPassword = keystoreProperties["keyPassword"] as String
            }
        }
    }
    buildTypes {
        release {
            signingConfig = signingConfigs.getByName("release")
        }
    }
}
```

### リリースビルドの実行

```bash
# APK形式
./gradlew assembleRelease

# AAB形式（Google Play用）
./gradlew bundleRelease
```

### 出力先

```
app/build/outputs/
├── apk/
│   └── google/release/
│       └── app-google-release.apk
└── bundle/
    └── googleRelease/
        └── app-google-release.aab
```

## 実機へのインストール

### USB接続の場合

```bash
# デバイス接続確認
adb devices

# インストール
./gradlew installGoogleDebug
# または
adb install app/build/outputs/apk/google/debug/app-google-debug.apk
```

### WSL2からWindows経由でインストール

WSL2では直接USBデバイスにアクセスできないため、以下の方法を使用:

#### 方法1: APKをWindows側にコピー

```bash
cp app/build/outputs/apk/google/debug/app-google-debug.apk \
   "/mnt/c/Users/your-username/Downloads/"
```

その後、Windows側からスマホに転送してインストール。

#### 方法2: Windows側のadbを使用

```bash
# Windows側のadb.exeのパスを指定
/mnt/c/Users/your-username/AppData/Local/Android/Sdk/platform-tools/adb.exe install \
    app/build/outputs/apk/google/debug/app-google-debug.apk
```

## クリーンビルド

キャッシュをクリアして再ビルド:

```bash
./gradlew clean assembleDebug
```

## よく使うコマンド一覧

| コマンド | 説明 |
|---------|------|
| `./gradlew assembleDebug` | デバッグAPKをビルド |
| `./gradlew assembleRelease` | リリースAPKをビルド |
| `./gradlew bundleRelease` | リリースAABをビルド |
| `./gradlew installGoogleDebug` | Google版をインストール |
| `./gradlew clean` | ビルドキャッシュをクリア |
| `./gradlew tasks` | 利用可能なタスク一覧 |
| `./gradlew lint` | コード品質チェック |
| `./gradlew test` | ユニットテスト実行 |

## Fastlane（自動化）

リリース作業の自動化については [ROADMAP.md](../../ROADMAP.md) のPhase 2を参照。

```bash
# 内部テストにデプロイ（設定後）
fastlane internal

# 本番リリース（設定後）
fastlane release
```

## トラブルシューティング

### ビルドが遅い

Gradleデーモンのメモリを増やす（`gradle.properties`）:

```properties
org.gradle.jvmargs=-Xmx4g -XX:+HeapDumpOnOutOfMemoryError
org.gradle.parallel=true
org.gradle.caching=true
```

### 依存関係の問題

```bash
./gradlew clean
./gradlew --refresh-dependencies assembleDebug
```

### 署名エラー（リリースビルド）

- `keystore.properties` のパスが正しいか確認
- キーストアファイルが存在するか確認
- パスワードが正しいか確認
