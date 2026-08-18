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

// URL rewrite map: short path → actual file
const REWRITES = {
  '/ai-learning': 'ai-learning.html',
  '/operation': 'operation.html',
};

// ─── Database ────────────────────────────────────────────────────────

let _db = null;

async function getDb() {
  if (_db) return _db;
  // 部署环境：通过 COZE_SUPABASE_URL 环境变量使用 Supabase SDK
  if (process.env.COZE_SUPABASE_URL && process.env.COZE_SUPABASE_ANON_KEY) {
    const { createClient } = require('@supabase/supabase-js');
    _db = createClient(process.env.COZE_SUPABASE_URL, process.env.COZE_SUPABASE_ANON_KEY);
    return _db;
  }
  // 开发环境：通过 coze-coding-dev-sdk 获取数据库连接
  try {
    const sdk = require('coze-coding-dev-sdk');
    _db = await sdk.getDb();
    return _db;
  } catch {
    throw new Error('No database connection available');
  }
}

async function insertFeedback(name, content, page) {
  const db = await getDb();
  // 判断 db 类型：Supabase 客户端有 from() 方法，Drizzle 客户端有 execute() 方法
  if (typeof db.from === 'function') {
    const { error } = await db.from('feedback').insert({
      name, content, page: page || 'general'
    });
    if (error) throw error;
  } else {
    await db.$client.query(
      'INSERT INTO feedback (name, content, page) VALUES ($1, $2, $3)',
      [name, content, page || 'general']
    );
  }
}

// ─── Request body parser ─────────────────────────────────────────────

function parseBody(req) {
  return new Promise((resolve, reject) => {
    let body = '';
    req.on('data', chunk => { body += chunk; });
    req.on('end', () => {
      try { resolve(JSON.parse(body)); }
      catch { reject(new Error('Invalid JSON')); }
    });
    req.on('error', reject);
  });
}

// ─── JSON response helper ────────────────────────────────────────────

function json(res, status, data) {
  res.writeHead(status, { 'Content-Type': 'application/json; charset=utf-8' });
  res.end(JSON.stringify(data));
}

// ─── Server ──────────────────────────────────────────────────────────

const server = http.createServer(async (req, res) => {
  // ── API routes ──
  if (req.method === 'POST' && req.url === '/api/feedback') {
    try {
      const { name, content, page } = await parseBody(req);
      if (!name || !name.trim()) {
        return json(res, 400, { success: false, error: '请填写您的姓名' });
      }
      if (!content || !content.trim()) {
        return json(res, 400, { success: false, error: '请填写反馈内容' });
      }
      if (content.trim().length < 5) {
        return json(res, 400, { success: false, error: '反馈内容太短了，再多写几句吧 😊' });
      }
      await insertFeedback(name.trim(), content.trim(), (page || 'general').trim());
      json(res, 200, { success: true, message: '感谢您的反馈！您的每一条建议都在帮助我们变得更好 🚀' });
    } catch (err) {
      console.error('Feedback error:', err);
      json(res, 500, { success: false, error: '反馈提交失败，请稍后再试' });
    }
    return;
  }

  // ── Static file serving ──
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

  // 3. Resolve file path
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