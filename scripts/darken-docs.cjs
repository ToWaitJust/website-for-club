// ─── 文档页深色化脚本 ────────────────────────────────────────────────
// 作用: 把 public/docs/ 下的浅色文档页(学习规划/运营方案)转换为深色主题,
//       移除对旧归档资源(nav.css/app.js/feedback.js)的引用,加返回条。
// 用法: node scripts/darken-docs.js

const fs = require('fs');
const path = require('path');

const ROOT = path.join(__dirname, '..');

function load(file) {
  return fs.readFileSync(path.join(ROOT, file), 'utf8');
}
function save(file, content) {
  fs.writeFileSync(path.join(ROOT, file), content);
}

const BACK_BAR = `<a href="/" style="position:fixed;top:0;left:0;right:0;z-index:9999;display:flex;align-items:center;justify-content:space-between;height:48px;padding:0 24px;background:rgba(10,15,30,.88);backdrop-filter:blur(12px);color:#94a3b8;font-size:13px;text-decoration:none;border-bottom:1px solid rgba(255,255,255,.08);font-family:-apple-system,'PingFang SC','Microsoft YaHei',sans-serif;">
  <span>&larr; Back to AI+CLUB</span>
  <span>AI+CLUB</span>
</a>`;

// 1. learning-plan.html: 硬编码颜色批量替换(区分 background/color/fill 上下文)
function darkenLearningPlan() {
  const file = 'public/docs/learning-plan.html';
  let s = load(file);

  // 背景
  s = s.replace(/(background(?:-color)?:\s*)#f5f8fc/gi, '$1#0a0f1e');
  s = s.replace(/(background(?:-color)?:\s*)#faf5ff/gi, '$1#141b31');
  s = s.replace(/(background(?:-color)?:\s*)#eef2f7/gi, '$1#1a2440');
  s = s.replace(/(background(?:-color)?:\s*)#ffffff/gi, '$1#101a2e');
  s = s.replace(/(background(?:-color)?:\s*)#fff\b/gi, '$1#101a2e');

  // 文字(白字保留)
  s = s.replace(/(color:\s*)#0f172a/gi, '$1#e2e8f0');
  s = s.replace(/(color:\s*)#64748b/gi, '$1#94a3b8');
  s = s.replace(/(color:\s*)#0a2a5e/gi, '$1#93c5fd');
  s = s.replace(/(color:\s*)#334155/gi, '$1#cbd5e1');
  s = s.replace(/(color:\s*)#4c6a8c/gi, '$1#a8c0dd');

  // SVG 图形
  s = s.replace(/(fill|stroke)="(?:#0f172a|#334155)"/g, (m, a) => (a === 'fill' ? 'fill="#cbd5e1"' : 'stroke="#cbd5e1"'));
  s = s.replace(/(fill|stroke)="#64748b"/g, (m, a) => (a === 'fill' ? 'fill="#94a3b8"' : 'stroke="#94a3b8"'));
  s = s.replace(/(fill|stroke)="#0a2a5e"/g, (m, a) => (a === 'fill' ? 'fill="#93c5fd"' : 'stroke="#93c5fd"'));

  // 边框 / 分隔 / 阴影
  s = s.replace(/(border[^;]*:\s*)#e7edf5/gi, '$1rgba(255,255,255,0.10)');
  s = s.replace(/rgba\(0,\s*0,\s*0,\s*\.0?6\)/g, 'rgba(255,255,255,0.08)');
  s = s.replace(/rgba\(0,\s*0,\s*0,\s*\.0?4\)/g, 'rgba(255,255,255,0.06)');
  s = s.replace(/rgba\(0,\s*20,\s*40,\s*0\.0?6\)/g, 'rgba(0,0,0,0.40)');

  // 移除旧资源引用与导航/反馈
  s = s.replace(/<link rel="stylesheet" href="\/assets\/css\/nav\.css">\s*/, '');
  s = s.replace(/<nav class="site-nav">[\s\S]*?<\/nav>\s*/, BACK_BAR + '\n');
  s = s.replace(/<script src="\/assets\/js\/app\.js"><\/script>\s*/, '');
  s = s.replace(/<script src="\/assets\/js\/feedback\.js"><\/script>\s*/, '');
  s = s.replace(/<div id="feedback-container"><\/div>\s*/, '');
  s = s.replace(/<script>[\s\S]*?FeedbackBox\.mount[\s\S]*?<\/script>\s*/, '');
  s = s.replace(/padding: calc\(2rem \+ 56px\) 1\.5rem/, 'padding: calc(2rem + 48px) 1.5rem');

  save(file, s);
  console.log('darkened:', file);
}

// 2. operation.html: 替换 :root 变量 + 移除旧引用
function darkenOperation() {
  const file = 'public/docs/operation.html';
  let s = load(file);

  const newRoot = `:root {
  --bg: #0a0f1e;
  --bg2: #101a2e;
  --ink: #f1f5f9;
  --muted: #94a3b8;
  --rule: rgba(255,255,255,0.10);
  --accent: #8b9cf5;
  --accent2: #34d399;
  --warn: #fbbf24;
  --danger: #f87171;
  --accent-soft: rgba(139,156,245,0.12);
  --accent2-soft: rgba(52,211,153,0.12);
  --warn-soft: rgba(251,191,36,0.12);
  --danger-soft: rgba(248,113,113,0.12);
  --shadow-sm: 0 1px 2px rgba(0,0,0,0.40);
  --shadow: 0 1px 3px rgba(0,0,0,0.50), 0 1px 2px rgba(0,0,0,0.40);
  --shadow-md: 0 4px 6px -1px rgba(0,0,0,0.50), 0 2px 4px -2px rgba(0,0,0,0.40);
  --shadow-lg: 0 10px 15px -3px rgba(0,0,0,0.50), 0 4px 6px -4px rgba(0,0,0,0.40);
  --gradient: linear-gradient(135deg, #4f46e5 0%, #7c3aed 100%);
  --gradient-soft: linear-gradient(135deg, rgba(139,156,245,0.10) 0%, rgba(124,58,237,0.08) 100%);
}`;

  s = s.replace(/:root\s*\{[\s\S]*?\}/, newRoot);

  // 移除旧资源引用与导航/反馈
  s = s.replace(/<link rel="stylesheet" href="\/assets\/css\/nav\.css">\s*/, '');
  s = s.replace(/<nav class="site-nav">[\s\S]*?<\/nav>\s*/, BACK_BAR + '\n');
  s = s.replace(/<script src="\/assets\/js\/app\.js"><\/script>\s*/, '');
  s = s.replace(/<script src="\/assets\/js\/feedback\.js"><\/script>\s*/, '');
  s = s.replace(/<div id="feedback-container"><\/div>\s*/, '');
  s = s.replace(/<script>[\s\S]*?FeedbackBox\.mount[\s\S]*?<\/script>\s*/, '');
  s = s.replace(/padding-top: 56px;/, 'padding-top: 48px;');

  save(file, s);
  console.log('darkened:', file);
}

darkenLearningPlan();
darkenOperation();
console.log('done');
