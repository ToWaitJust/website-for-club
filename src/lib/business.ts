// ─── 业务线内容读取(绕过 Astro content collections)──────────────────
// 背景:当前自动化环境与 Astro glob loader 存在兼容问题(frontmatter 读空)。
// 方案:直接用 node:fs 读取 Markdown 文件 + @astrojs/markdown-remark 渲染。
// 数据文件仍在 src/content/business/*.md,格式:frontmatter(slug/name/order)+ 正文。

import fs from 'node:fs';
import path from 'node:path';

export interface BusinessContent {
  slug: string;
  name: string;
  order: number;
  markdown: string;
}

const DIR = path.join(process.cwd(), 'src', 'content', 'business');

function parseFrontmatter(raw: string): { data: Record<string, string>; body: string } {
  const m = raw.match(/^---\r?\n([\s\S]*?)\r?\n---\r?\n?/);
  if (!m) return { data: {}, body: raw };
  const data: Record<string, string> = {};
  for (const line of m[1].split('\n')) {
    const kv = line.match(/^(\w+):\s*(.*)$/);
    if (kv) data[kv[1]] = kv[2].trim();
  }
  return { data, body: raw.slice(m[0].length) };
}

export function getBusinessContents(): BusinessContent[] {
  const files = fs.readdirSync(DIR).filter((f) => f.endsWith('.md'));
  const items = files.map((f) => {
    const raw = fs.readFileSync(path.join(DIR, f), 'utf8');
    const { data, body } = parseFrontmatter(raw);
    return {
      slug: data.slug || f.replace(/\.md$/, ''),
      name: data.name || f.replace(/\.md$/, ''),
      order: Number(data.order || 0),
      markdown: body,
    };
  });
  return items.sort((a, b) => a.order - b.order);
}

export function getBusinessContent(slug: string): BusinessContent | undefined {
  return getBusinessContents().find((c) => c.slug === slug);
}
