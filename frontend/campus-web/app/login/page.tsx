"use client";

import { apiFetch, setToken } from "@/lib/api";
import { useRouter } from "next/navigation";
import { useState } from "react";

export default function LoginPage() {
  const router = useRouter();
  const [phone, setPhone] = useState("13800000000");
  const [code, setCode] = useState("");
  const [message, setMessage] = useState("");

  async function sendCode() {
    setMessage("");
    try {
      await apiFetch<void>("/auth/sms/send", { method: "POST", body: JSON.stringify({ phone }) });
      setMessage("验证码已发送，请在 notice-service 控制台查看 mock 短信日志。");
    } catch (error) {
      setMessage(error instanceof Error ? error.message : "验证码发送失败");
    }
  }

  async function login() {
    setMessage("");
    try {
      const data = await apiFetch<{ token: string }>("/auth/login/sms", {
        method: "POST",
        body: JSON.stringify({ phone, code })
      });
      setToken(data.token);
      router.replace("/");
      router.refresh();
    } catch (error) {
      setMessage(error instanceof Error ? error.message : "登录失败");
    }
  }

  return (
    <main className="mx-auto flex min-h-screen max-w-md flex-col justify-center bg-cloud px-5">
      <div className="rounded-lg border border-slate-200 bg-white p-5 shadow-sm">
        <h1 className="text-2xl font-bold text-ink">CampusHub 登录</h1>
        <label className="mt-5 block text-sm text-slate-600">手机号</label>
        <input className="mt-2 w-full rounded-md border border-slate-300 px-3 py-3" value={phone} onChange={(e) => setPhone(e.target.value)} />
        <label className="mt-4 block text-sm text-slate-600">验证码</label>
        <input className="mt-2 w-full rounded-md border border-slate-300 px-3 py-3" value={code} onChange={(e) => setCode(e.target.value)} placeholder="6 位验证码" />
        <div className="mt-5 grid grid-cols-2 gap-3">
          <button onClick={sendCode} className="rounded-md border border-mint px-3 py-3 font-medium text-mint">发送验证码</button>
          <button onClick={login} className="rounded-md bg-ink px-3 py-3 font-medium text-white">登录</button>
        </div>
        {message && <p className="mt-4 text-sm text-slate-600">{message}</p>}
      </div>
    </main>
  );
}
