"use client";

import { Shell } from "@/components/Shell";
import { apiFetch } from "@/lib/api";
import { useParams, useRouter } from "next/navigation";
import { useEffect, useState } from "react";

type Item = { id: number; title: string; description: string; price: number; category: string; status: string };

export default function TradeDetailPage() {
  const { id } = useParams<{ id: string }>();
  const router = useRouter();
  const [item, setItem] = useState<Item | null>(null);
  const [message, setMessage] = useState("");

  useEffect(() => {
    apiFetch<Item>(`/trades/items/${id}`).then(setItem).catch((e) => setMessage(e.message));
  }, [id]);

  async function createOrder() {
    const order = await apiFetch<{ id: number }>("/orders", { method: "POST", body: JSON.stringify({ itemId: Number(id) }) });
    router.push(`/orders?created=${order.id}`);
  }

  return (
    <Shell>
      {item && (
        <article className="rounded-lg border border-slate-200 bg-white p-4">
          <h1 className="text-xl font-bold text-ink">{item.title}</h1>
          <p className="mt-2 text-sm text-slate-600">{item.description}</p>
          <div className="mt-4 flex items-center justify-between">
            <strong className="text-2xl text-coral">¥{item.price}</strong>
            <span className="rounded bg-slate-100 px-2 py-1 text-xs">{item.status}</span>
          </div>
          <button onClick={createOrder} className="mt-5 w-full rounded-md bg-ink px-4 py-3 font-medium text-white">创建订单</button>
        </article>
      )}
      {message && <p className="mt-3 text-sm text-coral">{message}</p>}
    </Shell>
  );
}
