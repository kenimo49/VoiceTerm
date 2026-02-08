# Change Log

All notable changes to VoiceTerm will be documented in this file.
This project adheres to [Semantic Versioning](https://semver.org/).

## [Unreleased]

### Added
- 日本語入力バー - 非モーダル入力UI (`FloatingTextInputDialog.kt`)
  - EditText + 送信ボタン
  - IME確定後にSSHへ送信
  - ターミナル直接入力との切り替え
- 音声認識 - Android SpeechRecognizer API統合
  - 音声入力ボタン（マイクアイコン）
  - 認識結果を入力欄に反映
  - 自動送信タイムアウト選択機能
- ブランディング変更
  - アイコン: ティール背景 + マイク中央 + 音声波形 + ターミナルプロンプト
  - Adaptive Icon + モノクロ版 + レガシーPNG対応
- ドキュメント体系構築
  - CLAUDE.md - 開発指針（LLM最適化・決定木ナビゲーション）
  - docs/ - 4層情報分類（guide/knowledge/design/references/flows）
  - Frontmatter + 相互リンク + テンプレート
  - ROADMAP.md - プロジェクトロードマップ

### Changed
- パッケージ名変更: `org.connectbot` → `com.voiceterm.app`

---

## Base Project

VoiceTerm is forked from [ConnectBot](https://github.com/connectbot/connectbot) v1.9.13.

For ConnectBot's change history, see:
https://github.com/connectbot/connectbot/blob/main/CHANGELOG.md
