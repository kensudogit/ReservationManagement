import { render, screen, waitFor } from "@testing-library/react";
import HomePage from "@/app/page";
import { api } from "@/lib/api";

jest.mock("@/lib/api", () => ({
  api: {
    summary: jest.fn(),
    receivables: jest.fn(),
  },
}));

describe("HomePage", () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  it("renders dashboard summary and receivables", async () => {
    (api.summary as jest.Mock).mockResolvedValue({
      customerCount: 2,
      openReceivableCount: 3,
      overdueCount: 1,
      totalOpenBalance: 1250000,
    });
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

    render(<HomePage />);

    expect(screen.getByRole("heading", { name: "債権の残高と回収状況を一目で把握" })).toBeInTheDocument();

    await waitFor(() => {
      expect(screen.getByText("得意先数")).toBeInTheDocument();
      expect(screen.getByText("2")).toBeInTheDocument();
      expect(screen.getByText("INV-2026-001")).toBeInTheDocument();
      expect(screen.getByText("株式会社サンプル商事")).toBeInTheDocument();
      expect(screen.getByText("OPEN")).toBeInTheDocument();
    });
  });

  it("shows empty message when no receivables", async () => {
    (api.summary as jest.Mock).mockResolvedValue({
      customerCount: 0,
      openReceivableCount: 0,
      overdueCount: 0,
      totalOpenBalance: 0,
    });
    (api.receivables as jest.Mock).mockResolvedValue([]);

    render(<HomePage />);

    await waitFor(() => {
      expect(
        screen.getByText("データがありません（API / PostgreSQL の起動を確認してください）"),
      ).toBeInTheDocument();
    });
  });

  it("shows error message when API fails", async () => {
    (api.summary as jest.Mock).mockRejectedValue(new Error("接続失敗"));
    (api.receivables as jest.Mock).mockResolvedValue([]);

    render(<HomePage />);

    await waitFor(() => {
      expect(screen.getByText("接続失敗")).toBeInTheDocument();
    });
  });
});
