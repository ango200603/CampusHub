import { Shell } from "@/components/Shell";
import Link from "next/link";

const actions = [
  { href: "/files", title: "上传资料", desc: "触发 OCR 和 AI 摘要", color: "bg-mint" },
  { href: "/trades", title: "逛二手", desc: "发布、浏览、下单", color: "bg-coral" },
  { href: "/orders", title: "我的订单", desc: "模拟支付和状态同步", color: "bg-sun" },
  { href: "/notices", title: "站内通知", desc: "支付与系统消息", color: "bg-ink" }
];

export default function Home() {
  return (
    <Shell>
      <section className="mb-4 overflow-hidden rounded-lg bg-white">
        <div className="bg-ink px-4 py-5 text-white">
          <p className="text-sm text-white/70">校园服务聚合平台</p>
          <h1 className="mt-1 text-2xl font-bold">今天的校园事务，一处处理。</h1>
        </div>
        <div className="grid grid-cols-2 gap-3 p-4">
          {actions.map((item) => (
            <Link key={item.href} href={item.href} className="rounded-lg border border-slate-200 bg-white p-3 shadow-sm">
              <span className={`mb-3 block h-2 w-10 rounded ${item.color}`} />
              <strong className="block text-sm text-ink">{item.title}</strong>
              <span className="mt-1 block text-xs leading-5 text-slate-500">{item.desc}</span>
            </Link>
          ))}
        </div>
      </section>
      <section className="rounded-lg border border-slate-200 bg-white p-4">
        <h2 className="text-base font-semibold text-ink">核心链路</h2>
        <div className="mt-3 space-y-2 text-sm text-slate-600">
          <p>手机号验证码登录，经网关携带 JWT 访问各服务。</p>
          <p>文件上传到 MinIO 后发送 MQ，由 AI 服务异步模拟解析。</p>
          <p>二手商品创建订单后锁定库存，支付成功消息最终同步订单、商品和通知。</p>
        </div>
      </section>
    </Shell>
  );
}
