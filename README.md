# Receivables Management（債権管理システム）

Java + Spring Boot + Maven + PostgreSQL + Next.js + React + Docker で構成した債権管理システムの開発環境です。

## 構成

| 層 | 技術 | ポート |
|---|---|---|
| Frontend | Next.js 14 / React 18 / TypeScript | 3000 |
| Backend | Spring Boot 3.3 / Java 21 / Maven | 8080 |
| Database | PostgreSQL 16 | 5432 |

### 機能

- 得意先マスタ（与信枠）
- 債権（請求）登録・残高管理
- 入金消込（残高・状態の自動更新）
- ダッシュボード（未収件数 / 延滞 / 残高合計）

## 前提ツール

- Docker Desktop
- JDK 21+（ローカル起動時）
- Maven 3.9+
- Node.js 20+

## いちばん簡単な起動（Docker）

```powershell
cd C:\devlop\ReceivablesManagement
.\scripts\up.ps1
```

または手動で:

```powershell
docker buildx use desktop-linux
docker build -t receivablesmanagement-backend ./backend
docker build -t receivablesmanagement-frontend ./frontend
docker compose up -d
```

| URL | 説明 |
|---|---|
| http://localhost:3000 | フロントエンド |
| http://localhost:8080/api/health | API ヘルスチェック |
| localhost:5432 / receivables | PostgreSQL |

### PostgreSQL 接続情報

- Database: `receivables`
- User: `receivables`
- Password: `receivables`
- Port: `5432`

停止:

```powershell
docker compose down
```

データごと初期化:

```powershell
docker compose down -v
```

## ローカル開発（API / UI を個別起動）

### 1. PostgreSQL のみ Docker 起動

```powershell
docker compose up -d postgres
```

### 2. Backend

```powershell
cd C:\devlop\ReceivablesManagement\backend
mvn spring-boot:run
```

### 3. Frontend

```powershell
cd C:\devlop\ReceivablesManagement\frontend
npm install
npm run dev
```

## テスト

全 Java / TypeScript ソースに対応するテストを用意しています。

```powershell
cd C:\devlop\ReceivablesManagement
.\scripts\test.ps1
```

個別実行:

```powershell
# Backend (JUnit 5 / MockMvc / Mockito / DataJpaTest + JaCoCo)
cd backend
mvn test

# Frontend (Jest / React Testing Library + coverage)
cd frontend
npm install
npm test
```

### 結果の確認

| 成果物 | パス |
|---|---|
| Backend テスト結果 (Surefire) | `backend/target/surefire-reports/` |
| Backend カバレッジ (HTML) | `backend/target/site/jacoco/index.html` |
| Frontend カバレッジ (HTML) | `frontend/coverage/lcov-report/index.html` |
| Frontend JUnit XML | `frontend/test-results/junit.xml` |

ブラウザで HTML レポートを開く例:

```powershell
start backend\target\site\jacoco\index.html
start frontend\coverage\lcov-report\index.html
```

## API 一覧

| Method | Path | 説明 |
|---|---|---|
| GET | `/api/health` | ヘルスチェック |
| GET | `/api/dashboard/summary` | サマリ |
| GET/POST | `/api/customers` | 得意先 |
| PUT | `/api/customers/{id}` | 得意先更新 |
| GET/POST | `/api/receivables` | 債権 |
| GET/POST | `/api/payments` | 入金 |

## ディレクトリ

```text
ReceivablesManagement/
├── backend/                 # Spring Boot (Maven)
├── frontend/                # Next.js + React
├── database/init/           # PostgreSQL 初期スキーマ
├── docker-compose.yml
└── README.md
```

## 補足

- テーブルは Spring Data JPA（`ddl-auto=update`）が自動作成・更新します。
- `database/init/01_schema.sql` は PostgreSQL 初回起動時にも適用されます。
- 初回起動時にサンプル得意先・債権を自動投入します。
- Railway デプロイ時は PostgreSQL プラグインを追加し、`SPRING_DATASOURCE_URL` 等を設定してください。
