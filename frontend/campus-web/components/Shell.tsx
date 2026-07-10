"use client";

import { AUTH_CHANGE_EVENT, AUTH_TOKEN_KEY, apiFetch, clearToken, getToken } from "@/lib/api";
import Link from "next/link";
import { usePathname } from "next/navigation";
import type { ReactNode } from "react";
import { useCallback, useEffect, useState } from "react";

const nav = [
  { href: "/", label: "首页" },
  { href: "/files", label: "文件" },
  { href: "/ai", label: "AI" },
  { href: "/trades", label: "二手" },
  { href: "/orders", label: "订单" },
  { href: "/notices", label: "通知" },
  { href: "/admin", label: "后台" }
];

type CurrentUser = {
  userId: string;
  phone: string;
  roles: string[];
};

export function Shell({ children }: { children: ReactNode }) {
  const pathname = usePathname();
  const [currentUser, setCurrentUser] = useState<CurrentUser | null>(null);
  const [checkingAuth, setCheckingAuth] = useState(false);

  const loadCurrentUser = useCallback(async () => {
    const token = getToken();
    if (!token) {
      setCurrentUser(null);
      setCheckingAuth(false);
      return;
    }

    setCheckingAuth(true);
    try {
      setCurrentUser(await apiFetch<CurrentUser>("/auth/me"));
    } catch {
      clearToken();
      setCurrentUser(null);
    } finally {
      setCheckingAuth(false);
    }
  }, []);

  useEffect(() => {
    void loadCurrentUser();
  }, [loadCurrentUser, pathname]);

  useEffect(() => {
    const handleAuthChange = () => {
      void loadCurrentUser();
    };
    const handleStorage = (event: StorageEvent) => {
      if (event.key === AUTH_TOKEN_KEY || event.key === null) {
        void loadCurrentUser();
      }
    };

    window.addEventListener(AUTH_CHANGE_EVENT, handleAuthChange);
    window.addEventListener("storage", handleStorage);
    return () => {
      window.removeEventListener(AUTH_CHANGE_EVENT, handleAuthChange);
      window.removeEventListener("storage", handleStorage);
    };
  }, [loadCurrentUser]);

  async function logout() {
    try {
      if (getToken()) {
        await apiFetch<void>("/auth/logout", { method: "POST" });
      }
    } catch {
      // Local logout should still clear stale client credentials if the server token is already invalid.
    } finally {
      clearToken();
      setCurrentUser(null);
    }
  }

  return (
    <div className="mx-auto flex min-h-screen w-full max-w-md flex-col bg-cloud">
      <header className="sticky top-0 z-10 border-b border-slate-200 bg-white/95 px-4 py-3 backdrop-blur">
        <div className="flex items-center justify-between">
          <Link href="/" className="text-lg font-bold text-ink">CampusHub</Link>
          {currentUser ? (
            <div className="flex min-w-0 items-center gap-2">
              <span className="max-w-32 truncate text-sm font-medium text-slate-700">{currentUser.phone}</span>
              <button
                type="button"
                onClick={logout}
                className="rounded-md border border-slate-300 px-3 py-2 text-sm font-medium text-slate-700"
              >
                退出
              </button>
            </div>
          ) : (
            <Link href="/login" className="rounded-md bg-ink px-3 py-2 text-sm font-medium text-white">
              {checkingAuth ? "校验中" : "登录"}
            </Link>
          )}
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
