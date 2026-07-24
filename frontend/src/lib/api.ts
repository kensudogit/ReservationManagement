const API_BASE = process.env.NEXT_PUBLIC_API_BASE_URL || "http://localhost:8080";

async function request<T>(path: string, init?: RequestInit): Promise<T> {
  const res = await fetch(`${API_BASE}${path}`, {
    ...init,
    headers: {
      "Content-Type": "application/json",
      ...(init?.headers || {}),
    },
    cache: "no-store",
  });
  if (!res.ok) {
    const text = await res.text();
    throw new Error(text || `API error: ${res.status}`);
  }
  if (res.status === 204) {
    return undefined as T;
  }
  return res.json();
}

export type DashboardSummary = {
  customerCount: number;
  openReceivableCount: number;
  overdueCount: number;
  totalOpenBalance: number;
};

export type Customer = {
  id: number;
  customerCode: string;
  name: string;
  contactName?: string;
  email?: string;
  phone?: string;
  creditLimit?: number;
  status: string;
};

export type Receivable = {
  id: number;
  invoiceNo: string;
  customerId: number;
  customerCode: string;
  customerName: string;
  invoiceDate: string;
  dueDate: string;
  amount: number;
  balance: number;
  currency: string;
  status: string;
  description?: string;
};

export type Payment = {
  id: number;
  receivableId: number;
  invoiceNo: string;
  paymentDate: string;
  amount: number;
  method: string;
  referenceNo?: string;
  note?: string;
};

export const api = {
  health: () => request<{ status: string }>("/api/health"),
  summary: () => request<DashboardSummary>("/api/dashboard/summary"),
  customers: () => request<Customer[]>("/api/customers"),
  createCustomer: (body: Omit<Customer, "id">) =>
    request<Customer>("/api/customers", { method: "POST", body: JSON.stringify(body) }),
  receivables: () => request<Receivable[]>("/api/receivables"),
  createReceivable: (body: {
    invoiceNo: string;
    customerId: number;
    invoiceDate: string;
    dueDate: string;
    amount: number;
    currency?: string;
    description?: string;
  }) => request<Receivable>("/api/receivables", { method: "POST", body: JSON.stringify(body) }),
  payments: () => request<Payment[]>("/api/payments"),
  createPayment: (body: {
    receivableId: number;
    paymentDate: string;
    amount: number;
    method?: string;
    referenceNo?: string;
    note?: string;
  }) => request<Payment>("/api/payments", { method: "POST", body: JSON.stringify(body) }),
};
