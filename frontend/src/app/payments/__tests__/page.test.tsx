import { render, screen, waitFor } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import PaymentsPage from "@/app/payments/page";
import { api } from "@/lib/api";

jest.mock("@/lib/api", () => ({
  api: {
    receivables: jest.fn(),
    payments: jest.fn(),
    createPayment: jest.fn(),
  },
}));

describe("PaymentsPage", () => {
  beforeEach(() => {
    jest.clearAllMocks();
    (api.receivables as jest.Mock).mockResolvedValue([
      {
        id: 1,
        invoiceNo: "INV-OPEN",
        customerId: 1,
        customerCode: "C001",
        customerName: "テスト",
        invoiceDate: "2026-07-01",
        dueDate: "2026-07-31",
        amount: 100000,
        balance: 100000,
        currency: "JPY",
        status: "OPEN",
      },
      {
        id: 2,
        invoiceNo: "INV-CLOSED",
        customerId: 1,
        customerCode: "C001",
        customerName: "テスト",
        invoiceDate: "2026-06-01",
        dueDate: "2026-06-30",
        amount: 10000,
        balance: 0,
        currency: "JPY",
        status: "CLOSED",
      },
    ]);
    (api.payments as jest.Mock).mockResolvedValue([]);
  });

  it("renders form and empty payment history", async () => {
    render(<PaymentsPage />);

    expect(screen.getByRole("heading", { name: "入金消込" })).toBeInTheDocument();

    await waitFor(() => {
      expect(screen.getByText("入金履歴はまだありません")).toBeInTheDocument();
      expect(screen.getByRole("option", { name: /INV-OPEN/ })).toBeInTheDocument();
      expect(screen.queryByRole("option", { name: /INV-CLOSED/ })).not.toBeInTheDocument();
    });
  });

  it("submits payment form", async () => {
    const user = userEvent.setup();
    (api.createPayment as jest.Mock).mockResolvedValue({
      id: 1,
      receivableId: 1,
      invoiceNo: "INV-OPEN",
      paymentDate: "2026-07-24",
      amount: 30000,
      method: "BANK_TRANSFER",
    });
    (api.payments as jest.Mock)
      .mockResolvedValueOnce([])
      .mockResolvedValueOnce([
        {
          id: 1,
          receivableId: 1,
          invoiceNo: "INV-OPEN",
          paymentDate: "2026-07-24",
          amount: 30000,
          method: "BANK_TRANSFER",
          referenceNo: "REF-1",
        },
      ]);

    render(<PaymentsPage />);
    await waitFor(() => expect(api.receivables).toHaveBeenCalled());

    await user.clear(screen.getByLabelText("入金額"));
    await user.type(screen.getByLabelText("入金額"), "30000");
    await user.type(screen.getByLabelText("照合番号"), "REF-1");
    await user.click(screen.getByRole("button", { name: "入金登録" }));

    await waitFor(() => {
      expect(api.createPayment).toHaveBeenCalledWith(
        expect.objectContaining({
          receivableId: 1,
          amount: 30000,
          method: "BANK_TRANSFER",
          referenceNo: "REF-1",
        }),
      );
    });
  });
});
