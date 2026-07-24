import type { Metadata } from "next";
import Link from "next/link";
import "./globals.css";

export const metadata: Metadata = {
  title: "Receivables Management | 債権管理システム",
  description: "Java / Spring Boot / Next.js / PostgreSQL 債権管理システム",
};

export default function RootLayout({ children }: { children: React.ReactNode }) {
  return (
    <html lang="ja">
      <body>
        <main>
          <header className="site-header">
            <div className="brand">Receivables Management</div>
            <nav className="nav">
              <Link href="/">ダッシュボード</Link>
              <Link href="/customers">得意先</Link>
              <Link href="/receivables">債権</Link>
              <Link href="/payments">入金</Link>
            </nav>
          </header>
          {children}
        </main>
      </body>
    </html>
  );
}
