"use client";

import { Shell } from "@/components/Shell";
import { apiFetch } from "@/lib/api";
import { useEffect, useState } from "react";

type Stats = { metrics: Record<string, number> };

export default function AdminPage() {
  const [stats, setStats] = useState<Stats | null>(null);
  const [message, setMessage] = useState("");

  useEffect(() => {
    apiFetch<Stats>("/admin/stats").then(setStats).catch((e) => setMessage(e.message));
  }, []);

  return (
    <Shell>
      <h1 className="text-xl font-bold text-ink">后台管理</h1>
      {message && <p className="mt-3 text-sm text-coral">{message}</p>}
      <div className="mt-4 grid grid-cols-2 gap-3">
        {stats && Object.entries(stats.metrics).map(([key, value]) => (
          <div key={key} className="rounded-lg border border-slate-200 bg-white p-4">
            <span className="text-xs uppercase text-slate-500">{key}</span>
            <strong className="mt-2 block text-2xl text-ink">{value}</strong>
          </div>
        ))}
      </div>
    </Shell>
  );
}
