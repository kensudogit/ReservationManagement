"use client";

import { FormEvent, useEffect, useState } from "react";
import { api, Customer, Receivable } from "@/lib/api";

export default function ReceivablesPage() {
  const [customers, setCustomers] = useState<Customer[]>([]);
  const [receivables, setReceivables] = useState<Receivable[]>([]);
  const [error, setError] = useState("");
  const [form, setForm] = useState({
    invoiceNo: "",
    customerId: "",
    invoiceDate: new Date().toISOString().slice(0, 10),
    dueDate: "",
    amount: "",
    description: "",
  });

  const load = async () => {
    const [c, r] = await Promise.all([api.customers(), api.receivables()]);
    setCustomers(c);
    setReceivables(r);
    if (!form.customerId && c.length > 0) {
      setForm((prev) => ({ ...prev, customerId: String(c[0].id) }));
    }
  };

  useEffect(() => {
    load().catch((e: Error) => setError(e.message));
  }, []);

  const onSubmit = async (e: FormEvent) => {
    e.preventDefault();
    setError("");
    try {
      await api.createReceivable({
        invoiceNo: form.invoiceNo,
        customerId: Number(form.customerId),
        invoiceDate: form.invoiceDate,
        dueDate: form.dueDate,
        amount: Number(form.amount),
        currency: "JPY",
        description: form.description,
      });
      setForm((prev) => ({
        ...prev,
        invoiceNo: "",
        amount: "",
        description: "",
      }));
      await load();
    } catch (err) {
      setError(err instanceof Error ? err.message : "登録に失敗しました");
    }
  };

  return (
    <>
      <section className="hero">
        <h1>債権（請求）管理</h1>
        <p>請求番号・期日・金額を登録し、未収残高を追跡します。</p>
      </section>

      <section className="panel">
        <h2>債権登録</h2>
        <form className="form" onSubmit={onSubmit}>
          <div className="form-row">
            <label>
              請求番号
              <input
                required
                value={form.invoiceNo}
                onChange={(e) => setForm({ ...form, invoiceNo: e.target.value })}
              />
            </label>
            <label>
              得意先
              <select
                required
                value={form.customerId}
                onChange={(e) => setForm({ ...form, customerId: e.target.value })}
              >
                {customers.map((c) => (
                  <option key={c.id} value={c.id}>
                    {c.customerCode} - {c.name}
                  </option>
                ))}
              </select>
            </label>
          </div>
          <div className="form-row">
            <label>
              請求日
              <input
                type="date"
                required
                value={form.invoiceDate}
                onChange={(e) => setForm({ ...form, invoiceDate: e.target.value })}
              />
            </label>
            <label>
              支払期日
              <input
                type="date"
                required
                value={form.dueDate}
                onChange={(e) => setForm({ ...form, dueDate: e.target.value })}
              />
            </label>
          </div>
          <div className="form-row">
            <label>
              請求額
              <input
                type="number"
                min={1}
                required
                value={form.amount}
                onChange={(e) => setForm({ ...form, amount: e.target.value })}
              />
            </label>
            <label>
              摘要
              <input
                value={form.description}
                onChange={(e) => setForm({ ...form, description: e.target.value })}
              />
            </label>
          </div>
          <button type="submit">登録</button>
          {error && <p className="error">{error}</p>}
        </form>
      </section>

      <section className="panel">
        <h2>一覧</h2>
        <div className="table-wrap">
          <table>
            <thead>
              <tr>
                <th>請求番号</th>
                <th>得意先</th>
                <th>期日</th>
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
                  <td>{Number(r.balance).toLocaleString("ja-JP")}</td>
                  <td>
                    <span className={`badge ${r.status.toLowerCase()}`}>{r.status}</span>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </section>
    </>
  );
}
