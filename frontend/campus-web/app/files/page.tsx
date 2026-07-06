"use client";

import { Shell } from "@/components/Shell";
import { apiFetch } from "@/lib/api";
import { useEffect, useState } from "react";

type FileRecord = { id: number; originalName: string; fileUrl: string; fileSize: number; status: string; createdAt: string };

export default function FilesPage() {
  const [files, setFiles] = useState<FileRecord[]>([]);
  const [message, setMessage] = useState("");

  async function load() {
    setFiles(await apiFetch<FileRecord[]>("/files/my"));
  }

  async function upload(formData: FormData) {
    setMessage("");
    await apiFetch<FileRecord>("/files/upload", { method: "POST", body: formData });
    setMessage("上传成功，AI 解析任务已异步创建。");
    await load();
  }

  useEffect(() => { load().catch((e) => setMessage(e.message)); }, []);

  return (
    <Shell>
      <h1 className="text-xl font-bold text-ink">文件上传</h1>
      <form action={upload} className="mt-4 rounded-lg border border-slate-200 bg-white p-4">
        <input name="file" type="file" className="w-full rounded-md border border-dashed border-slate-300 p-4 text-sm" required />
        <button className="mt-3 w-full rounded-md bg-mint px-4 py-3 font-medium text-white">上传并解析</button>
      </form>
      {message && <p className="mt-3 text-sm text-slate-600">{message}</p>}
      <div className="mt-4 space-y-3">
        {files.map((file) => (
          <article key={file.id} className="rounded-lg border border-slate-200 bg-white p-4">
            <h2 className="font-semibold text-ink">{file.originalName}</h2>
            <p className="mt-1 text-xs text-slate-500">{file.status} · {file.fileSize} bytes</p>
          </article>
        ))}
      </div>
    </Shell>
  );
}
