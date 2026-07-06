import type { Metadata } from "next";
import "./globals.css";

export const metadata: Metadata = {
  title: "CampusHub",
  description: "校园服务聚合平台"
};

export default function RootLayout({ children }: Readonly<{ children: React.ReactNode }>) {
  return (
    <html lang="zh-CN">
      <body>{children}</body>
    </html>
  );
}
