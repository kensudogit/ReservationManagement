import { api } from "@/lib/api";

describe("api", () => {
  const originalFetch = global.fetch;

  beforeEach(() => {
    global.fetch = jest.fn();
  });

  afterEach(() => {
    global.fetch = originalFetch;
    jest.resetAllMocks();
  });

  it("health calls /api/health", async () => {
    (global.fetch as jest.Mock).mockResolvedValue({
      ok: true,
      status: 200,
      json: async () => ({ status: "UP" }),
    });

    await expect(api.health()).resolves.toEqual({ status: "UP" });
    expect(global.fetch).toHaveBeenCalledWith(
      "http://localhost:8080/api/health",
      expect.objectContaining({
        headers: expect.objectContaining({ "Content-Type": "application/json" }),
        cache: "no-store",
      }),
    );
  });

  it("summary returns dashboard data", async () => {
    const summary = {
      customerCount: 2,
      openReceivableCount: 3,
      overdueCount: 1,
      totalOpenBalance: 1000,
    };
    (global.fetch as jest.Mock).mockResolvedValue({
      ok: true,
      status: 200,
      json: async () => summary,
    });

    await expect(api.summary()).resolves.toEqual(summary);
  });

  it("createCustomer posts JSON body", async () => {
    const body = {
      customerCode: "C001",
      name: "テスト",
      status: "ACTIVE",
      creditLimit: 1000,
    };
    (global.fetch as jest.Mock).mockResolvedValue({
      ok: true,
      status: 201,
      json: async () => ({ id: 1, ...body }),
    });

    await expect(api.createCustomer(body)).resolves.toMatchObject({ id: 1, customerCode: "C001" });
    expect(global.fetch).toHaveBeenCalledWith(
      "http://localhost:8080/api/customers",
      expect.objectContaining({
        method: "POST",
        body: JSON.stringify(body),
      }),
    );
  });

  it("throws Error when response is not ok", async () => {
    (global.fetch as jest.Mock).mockResolvedValue({
      ok: false,
      status: 409,
      text: async () => "得意先コードが既に存在します",
    });

    await expect(api.customers()).rejects.toThrow("得意先コードが既に存在します");
  });

  it("returns undefined for 204", async () => {
    (global.fetch as jest.Mock).mockResolvedValue({
      ok: true,
      status: 204,
      json: async () => ({}),
    });

    await expect(api.health()).resolves.toBeUndefined();
  });
});
