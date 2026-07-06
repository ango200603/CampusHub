"use client";

import { Shell } from "@/components/Shell";
import { apiFetch } from "@/lib/api";
import { useEffect, useState } from "react";

type Notice = { id: number; title: string; content: string; readStatus: number; createdAt: string };

export default function NoticesPage() {
  const [items, setItems] = useState<Notice[]>([]);
  const [message, setMessage] = useState("");

  async function load() {
    setItems(await apiFetch<Notice[]>("/notices/my"));
  }

  async function read(id: number) {
    await apiFetch<void>(`/notices/${id}/read`, { method: "PUT" });
    await load();
  }

  useEffect(() => { load().catch((e) => setMessage(e.message)); }, []);

  return (
    <Shell>
      <h1 className="text-xl font-bold text-ink">我的通知</h1>
      {message && <p className="mt-3 text-sm text-coral">{message}</p>}
      <div className="mt-4 space-y-3">
        {items.map((item) => (
          <article key={item.id} className="rounded-lg border border-slate-200 bg-white p-4">
            <div className="flex items-center justify-between">
              <h2 className="font-semibold text-ink">{item.title}</h2>
              <span className="text-xs text-slate-500">{item.readStatus ? "已读" : "未读"}</span>
            </div>
            <p className="mt-2 text-sm text-slate-600">{item.content}</p>
            {!item.readStatus && <button onClick={() => read(item.id)} className="mt-3 rounded-md border border-slate-300 px-3 py-2 text-sm">标记已读</button>}
          </article>
        ))}
      </div>
    </Shell>
  );
}
