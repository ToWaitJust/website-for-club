const http = require('http');
const fs = require('fs');
const path = require('path');

const PORT = process.env.DEPLOY_RUN_PORT || 5000;
const PUBLIC_DIR = __dirname;

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
};

// URL rewrite map: short path → actual file (relative paths, no leading /)
const REWRITES = {
  '/ai-learning': 'ai-learning.html',
  '/operation': 'operation.html',
};

const server = http.createServer((req, res) => {
  let url = new URL(req.url, `http://${req.headers.host || 'localhost'}`);
  let pathname = url.pathname;

  // 1. Apply rewrites
  if (REWRITES[pathname]) {
    pathname = REWRITES[pathname];
  }

  // 2. Default to index.html for root
  if (pathname === '/') {
    pathname = 'index.html';
  }

  // 3. Resolve file path (use relative path to avoid POSIX absolute-path bug)
  const filePath = path.join(PUBLIC_DIR, pathname.replace(/^\//, ''));

  fs.stat(filePath, (err, stats) => {
    if (err || !stats.isFile()) {
      // Static site: return 404 for missing files (no SPA fallback needed)
      res.writeHead(404, { 'Content-Type': 'text/plain; charset=utf-8' });
      res.end('404 Not Found');
      return;
    }

    const ext = path.extname(filePath).toLowerCase();
    const contentType = MIME_TYPES[ext] || 'application/octet-stream';

    res.writeHead(200, { 'Content-Type': contentType });

    if (ext === '.html' || ext === '.css' || ext === '.js' || ext === '.json' || ext === '.svg') {
      const stream = fs.createReadStream(filePath, { encoding: 'utf8' });
      stream.pipe(res);
    } else {
      const stream = fs.createReadStream(filePath);
      stream.pipe(res);
    }
  });
});

server.listen(PORT, '0.0.0.0', () => {
  console.log(`Serving HTTP on 0.0.0.0 port ${PORT} (http://0.0.0.0:${PORT}/) ...`);
});