"use client";

import { useEffect, useState } from "react";
import { api, DashboardSummary, Receivable } from "@/lib/api";

function yen(value: number) {
  return new Intl.NumberFormat("ja-JP", {
    style: "currency",
    currency: "JPY",
    maximumFractionDigits: 0,
  }).format(value || 0);
}

export default function HomePage() {
  const [summary, setSummary] = useState<DashboardSummary | null>(null);
  const [receivables, setReceivables] = useState<Receivable[]>([]);
  const [error, setError] = useState("");

  useEffect(() => {
    Promise.all([api.summary(), api.receivables()])
      .then(([s, r]) => {
        setSummary(s);
        setReceivables(r);
      })
      .catch((e: Error) => setError(e.message || "取得に失敗しました"));
  }, []);

  return (
    <>
      <section className="hero">
        <h1>債権の残高と回収状況を一目で把握</h1>
        <p>得意先・請求・入金を連携し、未収残高と延滞を継続的に管理します。</p>
      </section>

      {error && <p className="error">{error}</p>}

      <section className="grid">
        <div className="stat">
          <div className="label">得意先数</div>
          <div className="value">{summary?.customerCount ?? "-"}</div>
        </div>
        <div className="stat">
          <div className="label">未収債権件数</div>
          <div className="value">{summary?.openReceivableCount ?? "-"}</div>
        </div>
        <div className="stat">
          <div className="label">延滞件数</div>
          <div className="value">{summary?.overdueCount ?? "-"}</div>
        </div>
        <div className="stat">
          <div className="label">未収残高合計</div>
          <div className="value">{summary ? yen(Number(summary.totalOpenBalance)) : "-"}</div>
        </div>
      </section>

      <section className="panel">
        <h2>債権一覧</h2>
        <div className="table-wrap">
          <table>
            <thead>
              <tr>
                <th>請求番号</th>
                <th>得意先</th>
                <th>期日</th>
                <th>請求額</th>
                <th>残高</th>
                <th>状態</th>
              </tr>
            </thead>
            <tbody>
              {receivables.map((r) => (
                <tr key={r.id}>
                  <td>{r.invoiceNo}</td>
                  <td>{r.customerName}</td>
                  <td>{r.dueDate}</td>
                  <td>{yen(Number(r.amount))}</td>
                  <td>{yen(Number(r.balance))}</td>
                  <td>
                    <span className={`badge ${r.status.toLowerCase()}`}>{r.status}</span>
                  </td>
                </tr>
              ))}
              {receivables.length === 0 && (
                <tr>
                  <td colSpan={6} className="muted">
                    データがありません（API / PostgreSQL の起動を確認してください）
                  </td>
                </tr>
              )}
            </tbody>
          </table>
        </div>
      </section>
    </>
  );
}
