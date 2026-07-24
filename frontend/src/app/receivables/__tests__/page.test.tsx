import { render, screen, waitFor } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import ReceivablesPage from "@/app/receivables/page";
import { api } from "@/lib/api";

jest.mock("@/lib/api", () => ({
  api: {
    customers: jest.fn(),
    receivables: jest.fn(),
    createReceivable: jest.fn(),
  },
}));

describe("ReceivablesPage", () => {
  beforeEach(() => {
    jest.clearAllMocks();
    (api.customers as jest.Mock).mockResolvedValue([
      { id: 1, customerCode: "C001", name: "株式会社サンプル商事", status: "ACTIVE" },
    ]);
    (api.receivables as jest.Mock).mockResolvedValue([
      {
        id: 1,
        invoiceNo: "INV-2026-001",
        customerId: 1,
        customerCode: "C001",
        customerName: "株式会社サンプル商事",
        invoiceDate: "2026-07-01",
        dueDate: "2026-07-31",
        amount: 1250000,
        balance: 1250000,
        currency: "JPY",
        status: "OPEN",
      },
    ]);
  });

  it("renders receivables list and form", async () => {
    render(<ReceivablesPage />);

    expect(screen.getByRole("heading", { name: "債権（請求）管理" })).toBeInTheDocument();

    await waitFor(() => {
      expect(screen.getByText("INV-2026-001")).toBeInTheDocument();
      expect(screen.getByRole("option", { name: "C001 - 株式会社サンプル商事" })).toBeInTheDocument();
    });
  });

  it("submits create receivable form", async () => {
    const user = userEvent.setup();
    (api.createReceivable as jest.Mock).mockResolvedValue({
      id: 2,
      invoiceNo: "INV-NEW",
      customerId: 1,
      customerCode: "C001",
      customerName: "株式会社サンプル商事",
      invoiceDate: "2026-07-24",
      dueDate: "2026-08-24",
      amount: 50000,
      balance: 50000,
      currency: "JPY",
      status: "OPEN",
    });

    render(<ReceivablesPage />);
    await waitFor(() => expect(api.customers).toHaveBeenCalled());

    await user.type(screen.getByLabelText("請求番号"), "INV-NEW");
    await user.type(screen.getByLabelText("支払期日"), "2026-08-24");
    await user.type(screen.getByLabelText("請求額"), "50000");
    await user.click(screen.getByRole("button", { name: "登録" }));

    await waitFor(() => {
      expect(api.createReceivable).toHaveBeenCalledWith(
        expect.objectContaining({
          invoiceNo: "INV-NEW",
          customerId: 1,
          dueDate: "2026-08-24",
          amount: 50000,
          currency: "JPY",
        }),
      );
    });
  });
});
