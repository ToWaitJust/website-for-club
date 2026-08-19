// ─── AI+社团门户 公共脚本 ────────────────────────────────────────────
// 职责: 导航渲染 / 站点数据加载 / 公告渲染 / 轻量 Markdown 渲染
// 数据源: content/site.json(后续可切换为 yudao API,只改 loadSiteData)

const App = (() => {
  let siteData = null;

  // ── 站点数据 ────────────────────────────────────────────────────────
  async function loadSiteData() {
    if (siteData) return siteData;
    try {
      const res = await fetch('/content/site.json', { cache: 'no-cache' });
      if (!res.ok) throw new Error('site.json not found');
      siteData = await res.json();
    } catch (err) {
      console.error('[App] 站点数据加载失败:', err);
      siteData = { site: {}, businessLines: [], notices: [] };
    }
    return siteData;
  }

  // ── 导航 ────────────────────────────────────────────────────────────
  function renderNav(data) {
    const nav = document.querySelector('.site-nav .nav-links');
    if (!nav) return;
    const links = (data.site && data.site.nav) || [];
    const path = location.pathname;
    nav.innerHTML = links.map((item) => {
      const active = new RegExp(item.match || '^$').test(path);
      const cls = ['nav-link', active ? 'active' : '', item.external ? 'external' : ''].filter(Boolean).join(' ');
      return `<li><a class="${cls}" href="${item.href}">${item.label}</a></li>`;
    }).join('');
  }

  // ── 公告 ────────────────────────────────────────────────────────────
  function renderNotices(container, notices) {
    if (!container) return;
    if (!notices || !notices.length) {
      container.innerHTML = '<div class="empty">暂无公告</div>';
      return;
    }
    container.innerHTML = notices.map((n) => `
      <div class="notice">
        <div class="n-title">${n.title}</div>
        <div class="n-meta">${n.date || ''}</div>
        ${n.content ? `<div class="n-content">${n.content}</div>` : ''}
      </div>
    `).join('');
  }

  // ── 轻量 Markdown 渲染 ──────────────────────────────────────────────
  // 支持: 标题 / 表格 / 列表 / 引用 / 代码 / 链接 / 加粗 / 换行
  // 不依赖任何库,足够渲染业务线简介这类内容
  function escapeHtml(s) {
    return s.replace(/&/g, '&amp;').replace(/</g, '&lt;').replace(/>/g, '&gt;');
  }

  function inlineMd(s) {
    return escapeHtml(s)
      .replace(/\*\*(.+?)\*\*/g, '<strong>$1</strong>')
      .replace(/`(.+?)`/g, '<code>$1</code>')
      .replace(/\[(.+?)\]\((.+?)\)/g, '<a href="$2">$1</a>');
  }

  function renderMarkdown(md, container) {
    if (!container) return;
    const lines = md.split('\n');
    let html = '';
    let inTable = false;

    for (let i = 0; i < lines.length; i++) {
      const line = lines[i];

      // 表格分隔行
      if (/^\s*\|?[\s:|-]+\|?\s*$/.test(line) && line.includes('-') && inTable) continue;
      // 表格
      if (line.trim().startsWith('|') && line.trim().endsWith('|')) {
        const cells = line.trim().replace(/^\||\|$/g, '').split('|').map((c) => inlineMd(c.trim()));
        const isHeader = !inTable;
        if (isHeader) { html += '<table><thead><tr>'; } else { html += '<tr>'; }
        cells.forEach((c) => { html += isHeader ? `<th>${c}</th>` : `<td>${c}</td>`; });
        html += isHeader ? '</tr></thead><tbody>' : '</tr>';
        inTable = true;
        continue;
      } else if (inTable) { html += '</tbody></table>'; inTable = false; }

      if (/^#\s+/.test(line)) { html += `<h1>${inlineMd(line.replace(/^#\s+/, ''))}</h1>`; }
      else if (/^##\s+/.test(line)) { html += `<h2>${inlineMd(line.replace(/^##\s+/, ''))}</h2>`; }
      else if (/^###\s+/.test(line)) { html += `<h3>${inlineMd(line.replace(/^###\s+/, ''))}</h3>`; }
      else if (/^>\s?/.test(line)) { html += `<blockquote>${inlineMd(line.replace(/^>\s?/, ''))}</blockquote>`; }
      else if (/^\s*[-*]\s+/.test(line)) { html += `<li>${inlineMd(line.replace(/^\s*[-*]\s+/, ''))}</li>`; }
      else if (/^\d+\.\s+/.test(line)) { html += `<li>${inlineMd(line.replace(/^\d+\.\s+/, ''))}</li>`; }
      else if (/^\s*$/.test(line)) {
        html += html.endsWith('</li>') ? '</ul>' : '';
      }
      else { html += `<p>${inlineMd(line)}</p>`; }
    }
    if (inTable) html += '</tbody></table>';
    if (html.endsWith('</li>')) html += '</ul>';
    container.innerHTML = html;
  }

  // ── 页面初始化 ──────────────────────────────────────────────────────
  async function init(options = {}) {
    const data = await loadSiteData();
    renderNav(data);
    if (options.notices) renderNotices(document.querySelector(options.notices), data.notices);
    if (options.markdown) {
      const el = document.querySelector(options.markdown);
      if (el && options.markdownSrc) {
        try {
          const res = await fetch(options.markdownSrc, { cache: 'no-cache' });
          const md = await res.text();
          renderMarkdown(md, el);
        } catch (err) {
          el.innerHTML = '<p>内容加载失败</p>';
          console.error('[App] Markdown 加载失败:', err);
        }
      }
    }
    return data;
  }

  return { init, loadSiteData, renderMarkdown };
})();
