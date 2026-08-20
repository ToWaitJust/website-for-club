// ─── 本地预览 + 后端 API 代理 ───────────────────────────────────────
// 作用: 静态服务 dist 目录(等价 astro preview),并把 /yudao-api 前缀
//       转发到 yudao 后端(默认 http://localhost:48080),便于本地调试登录/管理接口。
// 用法: npm run preview:proxy        (端口可用 PORT 覆盖,后端可用 YUDAO_TARGET 覆盖)
import { createServer as createHttpServer } from 'node:http';
import { request as httpRequest } from 'node:http';
import { readFile, stat } from 'node:fs/promises';
import { extname, join, normalize } from 'node:path';
import { fileURLToPath } from 'node:url';

const ROOT = normalize(fileURLToPath(new URL('../dist', import.meta.url)));
const PORT = Number(process.env.PORT || 4321);
const API_PREFIX = '/yudao-api';
const TARGET = process.env.YUDAO_TARGET || 'http://localhost:48080';

const MIME = {
  '.html': 'text/html; charset=utf-8',
  '.js': 'text/javascript; charset=utf-8',
  '.css': 'text/css; charset=utf-8',
  '.json': 'application/json; charset=utf-8',
  '.svg': 'image/svg+xml',
  '.png': 'image/png',
  '.jpg': 'image/jpeg',
  '.jpeg': 'image/jpeg',
  '.gif': 'image/gif',
  '.webp': 'image/webp',
  '.ico': 'image/x-icon',
  '.woff': 'font/woff',
  '.woff2': 'font/woff2',
  '.ttf': 'font/ttf',
  '.txt': 'text/plain; charset=utf-8',
  '.md': 'text/markdown; charset=utf-8',
};

async function resolveFile(pathname) {
  const candidates = [
    pathname, // 精确文件: /docs/learning-plan.html、/_astro/*.js 等
    `${pathname}.html`, // 无扩展名 → 同名 .html
    // 目录路径 → 目录内 index.html
    pathname.endsWith('/')
      ? `${pathname.slice(0, -1)}/index.html`
      : `${pathname}/index.html`,
  ];
  for (const c of candidates) {
    const file = normalize(join(ROOT, c));
    if (!file.startsWith(ROOT)) return null; // 防目录穿越
    try {
      const s = await stat(file);
      if (s.isFile()) return file;
    } catch {
      /* 继续尝试下一个候选 */
    }
  }
  return null;
}

function proxyApi(req, res) {
  const url = new URL(req.url, 'http://localhost');
  const targetPath = url.pathname.replace(API_PREFIX, '') + url.search;
  const target = new URL(targetPath, TARGET);
  const proxyReq = httpRequest(
    target,
    { method: req.method, headers: { ...req.headers, host: target.host } },
    (proxyRes) => {
      res.writeHead(proxyRes.statusCode, proxyRes.headers);
      proxyRes.pipe(res);
    },
  );
  proxyReq.on('error', () => {
    res.writeHead(502, { 'content-type': 'application/json; charset=utf-8' });
    res.end(JSON.stringify({ code: 1, msg: `后端服务未启动: ${TARGET}` }));
  });
  req.pipe(proxyReq);
}

createHttpServer(async (req, res) => {
  const url = new URL(req.url, 'http://localhost');
  const pathname = decodeURIComponent(url.pathname);

  if (pathname.startsWith(API_PREFIX)) {
    return proxyApi(req, res);
  }

  const file = await resolveFile(pathname);
  if (!file) {
    res.writeHead(404, { 'content-type': 'text/html; charset=utf-8' });
    res.end('<h1>404 Not Found</h1>');
    return;
  }
  const type = MIME[extname(file).toLowerCase()] || 'application/octet-stream';
  try {
    res.writeHead(200, { 'content-type': type });
    res.end(await readFile(file));
  } catch {
    res.writeHead(500, { 'content-type': 'text/plain; charset=utf-8' });
    res.end('500 Internal Server Error');
  }
}).listen(PORT, () => {
  console.log(`preview + API proxy ready → http://localhost:${PORT}  (API → ${TARGET})`);
});
