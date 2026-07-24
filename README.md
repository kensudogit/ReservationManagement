# Receivables Management（債権管理システム）

Java + Spring Boot + Maven + Oracle + Next.js + React + Docker で構成した債権管理システムの開発環境です。

## 構成

| 層 | 技術 | ポート |
|---|---|---|
| Frontend | Next.js 14 / React 18 / TypeScript | 3000 |
| Backend | Spring Boot 3.3 / Java 21 / Maven | 8080 |
| Database | Oracle XE 21 (`gvenzl/oracle-xe`) | 1521 |

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
docker compose up -d --build
```

初回の Oracle 起動には数分かかることがあります。

| URL | 説明 |
|---|---|
| http://localhost:3000 | フロントエンド |
| http://localhost:8080/api/health | API ヘルスチェック |
| localhost:1521 / XEPDB1 | Oracle |

### Oracle 接続情報

- User: `receivables`
- Password: `receivables`
- SYS password: `OraclePass123`
- Service: `XEPDB1`

停止:

```powershell
docker compose down
```

データごと初期化:

```powershell
docker compose down -v
```

## ローカル開発（API / UI を個別起動）

### 1. Oracle のみ Docker 起動

```powershell
docker compose up -d oracle
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
├── database/init/           # 参考スキーマ SQL
├── docker-compose.yml
└── README.md
```

## 補足

- テーブルは Spring Data JPA（`ddl-auto=update`）が自動作成します。
- `database/init/01_schema.sql` は手動確認用の参考 DDL です。
- 初回起動時にサンプル得意先・債権を自動投入します。
"# ReservationManagement" 
