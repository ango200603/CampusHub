"use client";

import { Shell } from "@/components/Shell";
import { apiFetch } from "@/lib/api";
import { useEffect, useState } from "react";

type Task = { id: number; fileId: number; taskType: string; status: string; resultText?: string; errorMessage?: string };

export default function AiPage() {
  const [tasks, setTasks] = useState<Task[]>([]);
  const [message, setMessage] = useState("");

  async function load() {
    setTasks(await apiFetch<Task[]>("/ai/tasks/my"));
  }

  useEffect(() => { load().catch((e) => setMessage(e.message)); }, []);

  return (
    <Shell>
      <div className="flex items-center justify-between">
        <h1 className="text-xl font-bold text-ink">AI 任务</h1>
        <button onClick={load} className="rounded-md border border-slate-300 px-3 py-2 text-sm">刷新</button>
      </div>
      {message && <p className="mt-3 text-sm text-coral">{message}</p>}
      <div className="mt-4 space-y-3">
        {tasks.map((task) => (
          <article key={task.id} className="rounded-lg border border-slate-200 bg-white p-4">
            <div className="flex items-center justify-between">
              <h2 className="font-semibold text-ink">任务 #{task.id}</h2>
              <span className="rounded bg-slate-100 px-2 py-1 text-xs text-slate-600">{task.status}</span>
            </div>
            <p className="mt-2 text-sm text-slate-600">{task.resultText || task.errorMessage || "等待解析结果"}</p>
          </article>
        ))}
      </div>
    </Shell>
  );
}
