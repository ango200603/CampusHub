"use client";

import { Shell } from "@/components/Shell";
import { apiFetch } from "@/lib/api";
import Link from "next/link";
import { useEffect, useState } from "react";

type Item = { id: string; title: string; price: number; category: string; status: string; coverUrl?: string };

export default function TradesPage() {
  const [items, setItems] = useState<Item[]>([]);
  const [message, setMessage] = useState("");

  async function load() {
    setItems(await apiFetch<Item[]>("/trades/items"));
  }

  async function create(formData: FormData) {
    setMessage("");
    await apiFetch<Item>("/trades/items", {
      method: "POST",
      body: JSON.stringify({
        title: formData.get("title"),
        description: formData.get("description"),
        price: Number(formData.get("price")),
        category: formData.get("category"),
        coverUrl: ""
      })
    });
    await load();
  }

  useEffect(() => { load().catch((e) => setMessage(e.message)); }, []);

  return (
    <Shell>
      <h1 className="text-xl font-bold text-ink">二手商品</h1>
      <form action={create} className="mt-4 grid gap-3 rounded-lg border border-slate-200 bg-white p-4">
        <input name="title" placeholder="商品标题" className="rounded-md border border-slate-300 px-3 py-3" required />
        <input name="category" placeholder="分类，如教材" className="rounded-md border border-slate-300 px-3 py-3" required />
        <input name="price" type="number" step="0.01" placeholder="价格" className="rounded-md border border-slate-300 px-3 py-3" required />
        <textarea name="description" placeholder="描述" className="rounded-md border border-slate-300 px-3 py-3" />
        <button className="rounded-md bg-coral px-4 py-3 font-medium text-white">发布商品</button>
      </form>
      {message && <p className="mt-3 text-sm text-coral">{message}</p>}
      <div className="mt-4 space-y-3">
        {items.map((item) => (
          <Link href={`/trades/${item.id}`} key={item.id} className="block rounded-lg border border-slate-200 bg-white p-4">
            <div className="flex items-center justify-between gap-3">
              <h2 className="font-semibold text-ink">{item.title}</h2>
              <strong className="text-coral">¥{item.price}</strong>
            </div>
            <p className="mt-1 text-xs text-slate-500">{item.category} · {item.status}</p>
          </Link>
        ))}
      </div>
    </Shell>
  );
}
