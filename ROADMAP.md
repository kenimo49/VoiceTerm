# VoiceTerm Roadmap

## Vision

カフェからスマホで自宅PCのClaude Codeを操作。
日本語入力と音声認識で、モバイルAI開発体験を実現する。

---

## Phase 1: MVP (Minimum Viable Product)

### 1.1 環境構築
- [ ] Android Studioでビルド確認
- [ ] 実機でConnectBot動作確認
- [ ] パッケージ名変更 (`org.connectbot` → `com.voiceterm.app`)
- [ ] アプリ名・アイコン変更

### 1.2 日本語入力対応
- [ ] 入力用UIの設計
- [ ] 画面下部に専用入力欄を追加
- [ ] EditText + 送信ボタンの実装
- [ ] IME確定後にターミナルへ送信
- [ ] 従来の直接入力との切り替え

### 1.3 音声認識
- [ ] Speech-to-Text API 実装
- [ ] 音声入力ボタン追加
- [ ] 認識結果を入力欄に反映
- [ ] 回数制限ロジック (SharedPreferences)
- [ ] 無料回数: 1日10回を想定

---

## Phase 2: リリース準備

### 2.1 Google Play Console 設定
- [ ] デベロッパーアカウント登録 ($25)
- [ ] アプリ作成
- [ ] ストア掲載情報入力
- [ ] スクリーンショット作成
- [ ] プライバシーポリシー作成・公開

### 2.2 Fastlane 導入
- [ ] Ruby/Bundler インストール
- [ ] Fastlane 初期化 (`fastlane init`)
- [ ] 署名鍵 (keystore) 作成
- [ ] Google Play API 設定 (サービスアカウント)
- [ ] supply設定 (メタデータ自動アップロード)
- [ ] Fastfile 作成

```ruby
# 想定するFastfile
default_platform(:android)

platform :android do
  desc "内部テストにデプロイ"
  lane :internal do
    gradle(task: "bundleRelease")
    upload_to_play_store(
      track: "internal",
      aab: "app/build/outputs/bundle/release/app-release.aab"
    )
  end

  desc "本番リリース"
  lane :release do
    gradle(task: "bundleRelease")
    upload_to_play_store(
      track: "production",
      aab: "app/build/outputs/bundle/release/app-release.aab"
    )
  end
end
```

### 2.3 初回リリース
- [ ] 内部テスト配布
- [ ] バグ修正
- [ ] 本番リリース (無料、広告なし)

---

## Phase 3: 収益化

### 3.1 広告導入
- [ ] AdMobアカウント作成
- [ ] 広告ユニットID取得
- [ ] バナー広告実装 (画面下部)
- [ ] アップデートリリース

### 3.2 課金実装
- [ ] Google Play Billing Library 導入
- [ ] 商品設定 (Play Console)
  - 広告非表示: ¥500 (買い切り)
  - 音声無制限: ¥300 (買い切り)
  - またはセット: ¥700
- [ ] 購入フロー実装
- [ ] 購入状態の永続化
- [ ] リストア機能

---

## Phase 4: 機能拡充 (将来)

### 4.1 追加機能候補
- [ ] オフライン音声認識 (Vosk/Whisper)
- [ ] スニペット/マクロ機能
- [ ] 接続先クラウド同期
- [ ] テーマ/カスタマイズ
- [ ] Mosh対応
- [ ] TTS (出力読み上げ)

### 4.2 マーケティング
- [ ] ブログ記事作成
- [ ] Qiita/Zenn投稿
- [ ] Twitter/X での告知
- [ ] Claude Code連携の訴求

---

## Milestone Summary

| Phase | 目標 | 想定期間 |
|-------|------|----------|
| Phase 1 | MVP完成・動作確認 | 2-3週間 |
| Phase 2 | Google Play公開 | 1週間 |
| Phase 3 | 収益化開始 | 2週間 |
| Phase 4 | 継続的改善 | ongoing |

---

## Notes

- 各Phaseは順番に進める
- Phase 1完了時点で一度公開し、フィードバックを得る
- 収益化は急がず、ユーザー獲得を優先
- ConnectBotのライセンス表記を必ず維持
