// ─── 本地开发预览服务器 ──────────────────────────────────────────────
// 生产环境由 Nginx 托管静态文件,本服务器仅用于本地预览。
// 动态数据(报名/反馈)由前端直接调用 yudao API,与本站无关。
// 用法: npm run dev  (默认 5000 端口,可用 PORT 环境变量覆盖)

const http = require('http');
const fs = require('fs');
const path = require('path');

const PORT = process.env.PORT || 5000;
const PUBLIC_DIR = path.join(__dirname, '..');

const MIME_TYPES = {
  '.html': 'text/html; charset=utf-8',
  '.css': 'text/css; charset=utf-8',
  '.js': 'application/javascript; charset=utf-8',
  '.json': 'application/json',
  '.png': 'image/png',
  '.jpg': 'image/jpeg',
  '.jpeg': 'image/jpeg',
  '.gif': 'image/gif',
  '.svg': 'image/svg+xml',
  '.ico': 'image/x-icon',
  '.ttf': 'font/ttf',
  '.woff': 'font/woff',
  '.woff2': 'font/woff2',
  '.pdf': 'application/pdf',
  '.md': 'text/markdown; charset=utf-8',
};

http.createServer((req, res) => {
  let url;
  try {
    url = new URL(req.url, `http://${req.headers.host || 'localhost'}`);
  } catch {
    res.writeHead(400);
    res.end('Bad Request');
    return;
  }
  let pathname = url.pathname;

  if (pathname === '/') pathname = 'index.html';
  else if (pathname.endsWith('/')) pathname += 'index.html';
  const filePath = path.join(PUBLIC_DIR, pathname.replace(/^\//, ''));

  fs.stat(filePath, (err, stats) => {
    if (err || !stats.isFile()) {
      res.writeHead(404, { 'Content-Type': 'text/plain; charset=utf-8' });
      res.end('404 Not Found');
      return;
    }
    const ext = path.extname(filePath).toLowerCase();
    const contentType = MIME_TYPES[ext] || 'application/octet-stream';
    res.writeHead(200, { 'Content-Type': contentType });
    fs.createReadStream(filePath).pipe(res);
  });
}).listen(PORT, '0.0.0.0', () => {
  console.log(`Club portal dev server: http://localhost:${PORT}/`);
});
