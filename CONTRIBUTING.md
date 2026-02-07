# How to Contribute

VoiceTermへの貢献を歓迎します！

## Getting Started

1. GitHubアカウントを作成
2. リポジトリをフォーク
3. ローカルにクローン:
   ```bash
   git clone git@github.com:your-username/VoiceTerm.git
   ```
4. ビルド確認:
   ```bash
   ./gradlew assemble
   ```
5. テスト実行:
   ```bash
   ./gradlew test
   ```

## Making Changes

1. トピックブランチを作成:
   ```bash
   git checkout -b feature/my-feature main
   ```

2. 変更をコミット:
   ```
   Short summary (50 chars or less)

   Detailed explanation if necessary.
   ```

3. テストとlintを確認:
   ```bash
   ./gradlew check test
   ```

## Pull Request

1. フォークにプッシュ
2. [Pull Request](https://github.com/kenimo49/VoiceTerm/compare/)を作成

## Code Style

- 新規コードはKotlinで書く
- 既存のConnectBotコードスタイルを尊重
- Android lintの警告を解消する

## Issues

バグ報告や機能リクエストは[Issues](https://github.com/kenimo49/VoiceTerm/issues)へ。

## License

貢献いただいたコードはApache License 2.0のもとで公開されます。
