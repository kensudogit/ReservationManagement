import { render, screen, waitFor } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import CustomersPage from "@/app/customers/page";
import { api } from "@/lib/api";

jest.mock("@/lib/api", () => ({
  api: {
    customers: jest.fn(),
    createCustomer: jest.fn(),
  },
}));

describe("CustomersPage", () => {
  beforeEach(() => {
    jest.clearAllMocks();
    (api.customers as jest.Mock).mockResolvedValue([
      {
        id: 1,
        customerCode: "C001",
        name: "株式会社サンプル商事",
        contactName: "佐藤",
        creditLimit: 10000000,
        status: "ACTIVE",
      },
    ]);
  });

  it("renders customer list", async () => {
    render(<CustomersPage />);

    expect(screen.getByRole("heading", { name: "得意先マスタ" })).toBeInTheDocument();

    await waitFor(() => {
      expect(screen.getByText("C001")).toBeInTheDocument();
      expect(screen.getByText("株式会社サンプル商事")).toBeInTheDocument();
    });
  });

  it("submits create customer form", async () => {
    const user = userEvent.setup();
    (api.createCustomer as jest.Mock).mockResolvedValue({
      id: 2,
      customerCode: "C010",
      name: "新規商事",
      status: "ACTIVE",
      creditLimit: 1000000,
    });

    render(<CustomersPage />);

    await waitFor(() => expect(api.customers).toHaveBeenCalled());

    await user.type(screen.getByLabelText("得意先コード"), "C010");
    await user.type(screen.getByLabelText("得意先名"), "新規商事");
    await user.click(screen.getByRole("button", { name: "登録" }));

    await waitFor(() => {
      expect(api.createCustomer).toHaveBeenCalledWith(
        expect.objectContaining({
          customerCode: "C010",
          name: "新規商事",
          creditLimit: 1000000,
          status: "ACTIVE",
        }),
      );
      expect(api.customers).toHaveBeenCalledTimes(2);
    });
  });

  it("shows registration error", async () => {
    const user = userEvent.setup();
    (api.createCustomer as jest.Mock).mockRejectedValue(new Error("登録に失敗しました"));

    render(<CustomersPage />);
    await waitFor(() => expect(api.customers).toHaveBeenCalled());

    await user.type(screen.getByLabelText("得意先コード"), "C010");
    await user.type(screen.getByLabelText("得意先名"), "失敗ケース");
    await user.click(screen.getByRole("button", { name: "登録" }));

    await waitFor(() => {
      expect(screen.getByText("登録に失敗しました")).toBeInTheDocument();
    });
  });
});
