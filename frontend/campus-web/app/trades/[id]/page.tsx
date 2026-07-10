"use client";

import { Shell } from "@/components/Shell";
import { apiFetch, getToken } from "@/lib/api";
import { useParams, useRouter } from "next/navigation";
import { useEffect, useState } from "react";

type TradeItem = {
  id: string;
  sellerId: string;
  title: string;
  description: string;
  price: number;
  category: string;
  status: string;
};

type CurrentUser = { userId: string; phone: string; roles: string[] };
type CreatedOrder = { id: string };

export default function TradeDetailPage() {
  const { id } = useParams<{ id: string }>();
  const router = useRouter();
  const [item, setItem] = useState<TradeItem | null>(null);
  const [currentUser, setCurrentUser] = useState<CurrentUser | null>(null);
  const [checkingUser, setCheckingUser] = useState(true);
  const [creating, setCreating] = useState(false);
  const [message, setMessage] = useState("");

  useEffect(() => {
    apiFetch<TradeItem>(`/trades/items/${id}`).then(setItem).catch((e) => setMessage(e.message));
  }, [id]);

  useEffect(() => {
    if (!getToken()) {
      setCheckingUser(false);
      return;
    }

    let active = true;
    apiFetch<CurrentUser>("/auth/me")
      .then((user) => {
        if (active) setCurrentUser(user);
      })
      .catch(() => {
        if (active) setCurrentUser(null);
      })
      .finally(() => {
        if (active) setCheckingUser(false);
      });

    return () => {
      active = false;
    };
  }, []);

  const isOwnItem = item !== null && currentUser !== null && currentUser.userId === item.sellerId;

  async function createOrder() {
    if (!item || isOwnItem || checkingUser || creating) return;

    setMessage("");
    setCreating(true);
    try {
      const order = await apiFetch<CreatedOrder>("/orders", {
        method: "POST",
        body: JSON.stringify({ itemId: item.id })
      });
      router.push(`/orders?created=${encodeURIComponent(order.id)}`);
    } catch (e) {
      setMessage(e instanceof Error ? e.message : String(e));
    } finally {
      setCreating(false);
    }
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
          <button
            type="button"
            onClick={createOrder}
            disabled={isOwnItem || checkingUser || creating}
            className="mt-5 w-full rounded-md bg-ink px-4 py-3 font-medium text-white disabled:cursor-not-allowed disabled:bg-slate-300"
          >
            {isOwnItem ? "不能购买自己发布的商品" : checkingUser ? "校验中..." : creating ? "创建中..." : "创建订单"}
          </button>
          {isOwnItem && <p className="mt-2 text-sm text-slate-600">这是你发布的商品，不能创建订单。</p>}
        </article>
      )}
      {message && <p className="mt-3 text-sm text-coral">{message}</p>}
    </Shell>
  );
}
