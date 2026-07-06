"use client";

import Link from "next/link";
import { usePathname } from "next/navigation";
import type { ReactNode } from "react";

const nav = [
  { href: "/", label: "首页" },
  { href: "/files", label: "文件" },
  { href: "/ai", label: "AI" },
  { href: "/trades", label: "二手" },
  { href: "/orders", label: "订单" },
  { href: "/notices", label: "通知" },
  { href: "/admin", label: "后台" }
];

export function Shell({ children }: { children: ReactNode }) {
  const pathname = usePathname();
  return (
    <div className="mx-auto flex min-h-screen w-full max-w-md flex-col bg-cloud">
      <header className="sticky top-0 z-10 border-b border-slate-200 bg-white/95 px-4 py-3 backdrop-blur">
        <div className="flex items-center justify-between">
          <Link href="/" className="text-lg font-bold text-ink">CampusHub</Link>
          <Link href="/login" className="rounded-md bg-ink px-3 py-2 text-sm font-medium text-white">登录</Link>
        </div>
      </header>
      <main className="flex-1 px-4 py-4">{children}</main>
      <nav className="sticky bottom-0 grid grid-cols-7 border-t border-slate-200 bg-white">
        {nav.map((item) => {
          const active = pathname === item.href;
          return (
            <Link
              key={item.href}
              href={item.href}
              className={`px-1 py-3 text-center text-xs ${active ? "font-semibold text-mint" : "text-slate-500"}`}
            >
              {item.label}
            </Link>
          );
        })}
      </nav>
    </div>
  );
}
