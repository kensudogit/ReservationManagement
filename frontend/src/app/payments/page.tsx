"use client";

import { FormEvent, useEffect, useState } from "react";
import { api, Payment, Receivable } from "@/lib/api";

export default function PaymentsPage() {
  const [receivables, setReceivables] = useState<Receivable[]>([]);
  const [payments, setPayments] = useState<Payment[]>([]);
  const [error, setError] = useState("");
  const [form, setForm] = useState({
    receivableId: "",
    paymentDate: new Date().toISOString().slice(0, 10),
    amount: "",
    method: "BANK_TRANSFER",
    referenceNo: "",
    note: "",
  });

  const load = async () => {
    const [r, p] = await Promise.all([api.receivables(), api.payments()]);
    setReceivables(r.filter((x) => x.status !== "CLOSED"));
    setPayments(p);
    if (!form.receivableId && r.length > 0) {
      const open = r.find((x) => x.status !== "CLOSED") || r[0];
      setForm((prev) => ({ ...prev, receivableId: String(open.id) }));
    }
  };

  useEffect(() => {
    load().catch((e: Error) => setError(e.message));
  }, []);

  const onSubmit = async (e: FormEvent) => {
    e.preventDefault();
    setError("");
    try {
      await api.createPayment({
        receivableId: Number(form.receivableId),
        paymentDate: form.paymentDate,
        amount: Number(form.amount),
        method: form.method,
        referenceNo: form.referenceNo,
        note: form.note,
      });
      setForm((prev) => ({ ...prev, amount: "", referenceNo: "", note: "" }));
      await load();
    } catch (err) {
      setError(err instanceof Error ? err.message : "登録に失敗しました");
    }
  };

  return (
    <>
      <section className="hero">
        <h1>入金消込</h1>
        <p>入金を登録すると対象債権の残高・状態が自動更新されます。</p>
      </section>

      <section className="panel">
        <h2>入金登録</h2>
        <form className="form" onSubmit={onSubmit}>
          <div className="form-row">
            <label>
              対象債権
              <select
                required
                value={form.receivableId}
                onChange={(e) => setForm({ ...form, receivableId: e.target.value })}
              >
                {receivables.map((r) => (
                  <option key={r.id} value={r.id}>
                    {r.invoiceNo} / 残高 {Number(r.balance).toLocaleString("ja-JP")}
                  </option>
                ))}
              </select>
            </label>
            <label>
              入金日
              <input
                type="date"
                required
                value={form.paymentDate}
                onChange={(e) => setForm({ ...form, paymentDate: e.target.value })}
              />
            </label>
          </div>
          <div className="form-row">
            <label>
              入金額
              <input
                type="number"
                min={1}
                required
                value={form.amount}
                onChange={(e) => setForm({ ...form, amount: e.target.value })}
              />
            </label>
            <label>
              入金方法
              <select
                value={form.method}
                onChange={(e) => setForm({ ...form, method: e.target.value })}
              >
                <option value="BANK_TRANSFER">振込</option>
                <option value="CHECK">小切手</option>
                <option value="CASH">現金</option>
                <option value="OTHER">その他</option>
              </select>
            </label>
          </div>
          <div className="form-row">
            <label>
              照合番号
              <input
                value={form.referenceNo}
                onChange={(e) => setForm({ ...form, referenceNo: e.target.value })}
              />
            </label>
            <label>
              備考
              <input
                value={form.note}
                onChange={(e) => setForm({ ...form, note: e.target.value })}
              />
            </label>
          </div>
          <button type="submit">入金登録</button>
          {error && <p className="error">{error}</p>}
        </form>
      </section>

      <section className="panel">
        <h2>入金履歴</h2>
        <div className="table-wrap">
          <table>
            <thead>
              <tr>
                <th>請求番号</th>
                <th>入金日</th>
                <th>金額</th>
                <th>方法</th>
                <th>照合番号</th>
              </tr>
            </thead>
            <tbody>
              {payments.map((p) => (
                <tr key={p.id}>
                  <td>{p.invoiceNo}</td>
                  <td>{p.paymentDate}</td>
                  <td>{Number(p.amount).toLocaleString("ja-JP")}</td>
                  <td>{p.method}</td>
                  <td>{p.referenceNo || "-"}</td>
                </tr>
              ))}
              {payments.length === 0 && (
                <tr>
                  <td colSpan={5} className="muted">
                    入金履歴はまだありません
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
