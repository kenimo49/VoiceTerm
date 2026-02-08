---
title: ドキュメント作成ガイド
category: guide
tags: [documentation, llm-optimization, frontmatter]
related:
  - ../README.md
  - ../_templates/flow.md
  - ../_templates/knowledge.md
---

# ドキュメント作成ガイド

VoiceTermのドキュメントを作成・更新する際のガイドラインです。
LLM（Claude Code等）が効率的にコードベースを探索できる構造を目指しています。

## なぜこのガイドが必要か

- LLMはコンテキストウィンドウに制約がある
- フラットなドキュメント構成では、必要な情報にたどり着くまでに多くのファイルを読む必要がある
- 4層分類 + Frontmatter + 相互リンクにより、最小限のファイル読み込みで目的の情報に到達できる

## ディレクトリ構造と役割

| ディレクトリ | 役割 | 分類の問いかけ |
|-------------|------|---------------|
| `guide/` | 開発ガイド・手順書 | 「どう進めるか？」 |
| `knowledge/` | 実践的知識 | 「HOW - どうやるか？」 |
| `design/` | 設計思想 | 「WHY - なぜそうしたか？」 |
| `references/` | 外部仕様 | 「WHAT - 何が定義されているか？」 |
| `flows/` | 処理フロー | 「WHEN/WHERE - いつ・どこで動くか？」 |

迷った場合の判断基準:
- 手順がメインなら → `guide/`
- 環境やツールのノウハウなら → `knowledge/`
- 「なぜAではなくBを選んだか」を記録するなら → `design/`
- 外部APIの仕様をまとめるなら → `references/`
- データや処理の流れを図示するなら → `flows/`

## ドキュメント種類別ガイドライン

### Knowledge ドキュメント

実践的なノウハウを記録する。

```markdown
---
title: タイトル
category: knowledge
tags: [関連タグ]
related:
  - 関連ファイルへの相対パス
---

# タイトル

## 概要
（何についてのナレッジか、1-2文で）

## 手順 / 内容
（具体的なコマンドやコード例を含める）

## トラブルシューティング
（よくある問題と解決策）
```

### Design ドキュメント

設計判断とその理由を記録する。

```markdown
---
title: タイトル
category: design
tags: [関連タグ]
related:
  - 関連ファイルへの相対パス
---

# タイトル

## 背景
（どんな課題があったか）

## 検討した選択肢
（比較表を含めると効果的）

## 決定
（何を選び、なぜ選んだか）

## 影響範囲
（この決定が影響するファイル・機能）
```

### References ドキュメント

外部仕様やAPIインターフェースをまとめる。

```markdown
---
title: タイトル
category: references
tags: [関連タグ]
source: 公式ドキュメントのURL
related:
  - 関連ファイルへの相対パス
---

# タイトル

## 概要

## API / インターフェース
（表形式で整理）

## VoiceTermでの使用箇所
（コード内の該当ファイルへのリンク）
```

### Flow ドキュメント

処理の流れを図示する。テンプレート: `_templates/flow.md`

```markdown
---
title: タイトル
category: flow
tags: [関連タグ]
related:
  - 関連ファイルへの相対パス
---

# タイトル

## 概要

## フロー図
（Mermaid記法を使用）

## ステップ詳細
（各ステップの説明）
```

## Frontmatter の設定方法

全てのドキュメントにYAML Frontmatterを付与する。

```yaml
---
title: ドキュメントのタイトル        # 必須
category: guide|knowledge|design|references|flow  # 必須
tags: [tag1, tag2]                  # 必須（検索用）
related:                            # 推奨（相互リンク）
  - ../path/to/related-doc.md
source: https://...                 # 任意（外部参照元）
---
```

### タグの付け方

- 技術要素: `android`, `kotlin`, `gradle`, `wsl2`, `ssh`
- 機能: `voice-recognition`, `japanese-input`, `branding`
- 作業種類: `setup`, `build`, `debug`, `release`
- ドキュメント: `documentation`, `frontmatter`, `llm-optimization`

## 相互リンク戦略

- 関連ドキュメントはFrontmatterの `related` で宣言する
- 本文中でも文脈に応じてリンクを設置する
- リンクは常に相対パスを使用する（`../knowledge/wsl-setup.md`）
- 各READMEがカテゴリ内のインデックスとして機能する

## コンテキストサイズ最適化

LLMが効率的に読めるよう、以下を守る:

- **1ファイル300行以内**: 超える場合はファイルを分割する
- **冗長な説明を避ける**: コマンド例と簡潔な説明を優先
- **構造化する**: 見出し、表、リストを活用する
- **Frontmatterでメタデータを外出しする**: 本文は内容に集中

## チェックリスト

新しいドキュメントを作成したら、以下を確認:

- [ ] Frontmatter（title, category, tags, related）が設定されている
- [ ] 適切なディレクトリに配置されている
- [ ] カテゴリのREADME.mdに追記されている
- [ ] 関連ドキュメントから相互リンクされている
- [ ] 300行以内に収まっている
- [ ] 具体的なコマンド例やコードが含まれている
