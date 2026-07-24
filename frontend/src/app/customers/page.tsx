"use client";

import { FormEvent, useEffect, useState } from "react";
import { api, Customer } from "@/lib/api";

export default function CustomersPage() {
  const [customers, setCustomers] = useState<Customer[]>([]);
  const [error, setError] = useState("");
  const [form, setForm] = useState({
    customerCode: "",
    name: "",
    contactName: "",
    email: "",
    phone: "",
    creditLimit: "1000000",
    status: "ACTIVE",
  });

  const load = () =>
    api
      .customers()
      .then(setCustomers)
      .catch((e: Error) => setError(e.message));

  useEffect(() => {
    load();
  }, []);

  const onSubmit = async (e: FormEvent) => {
    e.preventDefault();
    setError("");
    try {
      await api.createCustomer({
        customerCode: form.customerCode,
        name: form.name,
        contactName: form.contactName,
        email: form.email,
        phone: form.phone,
        creditLimit: Number(form.creditLimit),
        status: form.status,
      });
      setForm({
        customerCode: "",
        name: "",
        contactName: "",
        email: "",
        phone: "",
        creditLimit: "1000000",
        status: "ACTIVE",
      });
      await load();
    } catch (err) {
      setError(err instanceof Error ? err.message : "登録に失敗しました");
    }
  };

  return (
    <>
      <section className="hero">
        <h1>得意先マスタ</h1>
        <p>与信枠と連絡先を管理し、債権登録の土台を整えます。</p>
      </section>

      <section className="panel">
        <h2>得意先登録</h2>
        <form className="form" onSubmit={onSubmit}>
          <div className="form-row">
            <label>
              得意先コード
              <input
                required
                value={form.customerCode}
                onChange={(e) => setForm({ ...form, customerCode: e.target.value })}
              />
            </label>
            <label>
              得意先名
              <input
                required
                value={form.name}
                onChange={(e) => setForm({ ...form, name: e.target.value })}
              />
            </label>
          </div>
          <div className="form-row">
            <label>
              担当者
              <input
                value={form.contactName}
                onChange={(e) => setForm({ ...form, contactName: e.target.value })}
              />
            </label>
            <label>
              与信枠
              <input
                type="number"
                min={0}
                value={form.creditLimit}
                onChange={(e) => setForm({ ...form, creditLimit: e.target.value })}
              />
            </label>
          </div>
          <div className="form-row">
            <label>
              メール
              <input
                value={form.email}
                onChange={(e) => setForm({ ...form, email: e.target.value })}
              />
            </label>
            <label>
              電話
              <input
                value={form.phone}
                onChange={(e) => setForm({ ...form, phone: e.target.value })}
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
                <th>コード</th>
                <th>名称</th>
                <th>担当</th>
                <th>与信枠</th>
                <th>状態</th>
              </tr>
            </thead>
            <tbody>
              {customers.map((c) => (
                <tr key={c.id}>
                  <td>{c.customerCode}</td>
                  <td>{c.name}</td>
                  <td>{c.contactName || "-"}</td>
                  <td>{Number(c.creditLimit || 0).toLocaleString("ja-JP")}</td>
                  <td>{c.status}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </section>
    </>
  );
}
