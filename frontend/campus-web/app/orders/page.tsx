"use client";

import { Shell } from "@/components/Shell";
import { apiFetch } from "@/lib/api";
import { useEffect, useState } from "react";

type Order = {
  id: string;
  orderNo: string;
  buyerId: string;
  sellerId: string;
  itemId: string;
  amount: number;
  status: string;
};
type Pay = { payNo: string; orderNo: string; amount: number; status: string };

export default function OrdersPage() {
  const [orders, setOrders] = useState<Order[]>([]);
  const [message, setMessage] = useState("");

  async function load() {
    setOrders(await apiFetch<Order[]>("/orders/my"));
  }

  async function pay(order: Order) {
    const record = await apiFetch<Pay>("/pay/create", { method: "POST", body: JSON.stringify({ orderNo: order.orderNo, amount: order.amount }) });
    await apiFetch<Pay>("/pay/mock-success", { method: "POST", body: JSON.stringify({ payNo: record.payNo }) });
    setMessage("模拟支付成功，订单服务会通过 MQ 更新订单状态。");
    await load();
  }

  useEffect(() => { load().catch((e) => setMessage(e.message)); }, []);

  return (
    <Shell>
      <div className="flex items-center justify-between">
        <h1 className="text-xl font-bold text-ink">我的订单</h1>
        <button onClick={load} className="rounded-md border border-slate-300 px-3 py-2 text-sm">刷新</button>
      </div>
      {message && <p className="mt-3 text-sm text-slate-600">{message}</p>}
      <div className="mt-4 space-y-3">
        {orders.map((order) => (
          <article key={order.id} className="rounded-lg border border-slate-200 bg-white p-4">
            <div className="flex items-center justify-between">
              <h2 className="font-semibold text-ink">{order.orderNo}</h2>
              <span className="rounded bg-slate-100 px-2 py-1 text-xs">{order.status}</span>
            </div>
            <p className="mt-2 text-sm text-slate-600">商品 #{order.itemId} · ¥{order.amount}</p>
            {order.status === "UNPAID" && <button onClick={() => pay(order)} className="mt-3 w-full rounded-md bg-sun px-4 py-3 font-medium text-ink">模拟支付</button>}
          </article>
        ))}
      </div>
    </Shell>
  );
}
