export type ApiResult<T> = {
  code: number;
  message: string;
  data: T;
  timestamp?: string;
};

const API_BASE = process.env.NEXT_PUBLIC_API_BASE ?? "http://localhost:8080";

export function getToken() {
  if (typeof window === "undefined") return "";
  return localStorage.getItem("campus_token") ?? "";
}

export function setToken(token: string) {
  localStorage.setItem("campus_token", token);
}

export function clearToken() {
  localStorage.removeItem("campus_token");
}

export async function apiFetch<T>(path: string, init: RequestInit = {}): Promise<T> {
  const headers = new Headers(init.headers);
  const token = getToken();
  if (token) headers.set("Authorization", `Bearer ${token}`);
  if (!(init.body instanceof FormData) && !headers.has("Content-Type")) {
    headers.set("Content-Type", "application/json");
  }
  const res = await fetch(`${API_BASE}${path}`, { ...init, headers });
  const json = (await res.json()) as ApiResult<T>;
  if (!res.ok || json.code !== 0) {
    throw new Error(json.message || `HTTP ${res.status}`);
  }
  return json.data;
}
