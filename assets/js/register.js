// ─── 报名表单组件 ─────────────────────────────────────────────────────
// 用法: RegisterBox.mount(container, { businessLine, businessName, open: true })
// 数据: POST {apiBase}/admin-api/club/register  (yudao 公开接口 @PermitAll)
// yudao 统一响应: { code: 0, data: ..., msg: '...' }  → code === 0 为成功

const RegisterBox = (() => {
  const DEFAULT_FIELDS = [
    { key: 'name', label: '姓名', type: 'text', required: true, placeholder: '您的姓名', maxlength: 50 },
    { key: 'studentId', label: '学号', type: 'text', required: true, placeholder: '如 2025xxxxxx', maxlength: 20 },
    { key: 'college', label: '学院', type: 'text', required: true, placeholder: '所在学院', maxlength: 50 },
    { key: 'major', label: '专业', type: 'text', required: false, placeholder: '所在专业', maxlength: 50 },
    { key: 'phone', label: '手机号', type: 'text', required: true, placeholder: '用于联系通知', maxlength: 20 },
    { key: 'wechat', label: '微信号', type: 'text', required: false, placeholder: '方便拉群', maxlength: 50 },
    { key: 'motivation', label: '报名动机', type: 'textarea', required: false, placeholder: '为什么想加入这条业务线？有什么想做的？', maxlength: 500 },
  ];

  const STYLES = `
    .register-box { max-width: 640px; margin: 0 auto; }
    .register-box .reg-closed {
      text-align: center; padding: 3rem 2rem;
      background: var(--surface, #fff); border-radius: var(--radius-lg, 18px);
      border: 1px solid var(--line, rgba(0,0,0,0.08));
      color: var(--muted, #86868b);
    }
  `;

  function injectStyles() {
    if (!document.getElementById('rg-styles')) {
      const s = document.createElement('style');
      s.id = 'rg-styles';
      s.textContent = STYLES;
      document.head.appendChild(s);
    }
  }

  function render(container, cfg, fields) {
    const inputs = fields.map((f) => {
      const required = f.required ? '<span class="req">*</span>' : '';
      const tag = f.type === 'textarea' ? 'textarea' : 'input';
      const attrs = f.type === 'textarea'
        ? `placeholder="${f.placeholder || ''}" maxlength="${f.maxlength || 500}"`
        : `type="text" placeholder="${f.placeholder || ''}" maxlength="${f.maxlength || 50}"`;
      return `
        <div class="field">
          <label for="rg-${f.key}">${f.label} ${required}</label>
          <${tag} id="rg-${f.key}" ${attrs}></${tag}>
        </div>`;
    }).join('');

    container.innerHTML = `
      <div class="register-box">
        <div class="form-card">
          <h2>📝 ${cfg.businessName} · 报名表</h2>
          <p class="sub">填写信息后提交,我们会在招新周期内通过微信/短信联系你。</p>
          <div id="rg-fields">${inputs}</div>
          <div class="btn-row">
            <button class="btn" id="rg-submit">提交报名</button>
            <span class="msg" id="rg-msg"></span>
          </div>
        </div>
      </div>
    `;
  }

  function renderClosed(container, cfg) {
    container.innerHTML = `
      <div class="register-box">
        <div class="reg-closed">
          <p style="font-size:1.2rem; font-weight:600; color:var(--ink, #1d1d1f); margin-bottom:0.5rem;">🕐 报名暂未开放</p>
          <p>${cfg.businessName} 的报名通道当前关闭,请关注公告或加入咨询群了解最新动态。</p>
        </div>
      </div>
    `;
  }

  function bindEvents(container, cfg, fields) {
    const btn = container.querySelector('#rg-submit');
    const msg = container.querySelector('#rg-msg');

    btn.addEventListener('click', async () => {
      const payload = {};
      for (const f of fields) {
        const el = container.querySelector(`#rg-${f.key}`);
        const value = el.value.trim();
        if (f.required && !value) {
          msg.className = 'msg error';
          msg.textContent = `请填写${f.label}`;
          el.focus();
          return;
        }
        payload[f.key] = value;
      }
      payload.businessLine = cfg.businessLine;

      btn.disabled = true;
      btn.textContent = '提交中…';
      msg.className = 'msg info';
      msg.textContent = '正在提交…';

      try {
        const data = await App.loadSiteData();
        const apiBase = (data.site && data.site.apiBase) || '/yudao-api';
        const res = await fetch(`${apiBase}/admin-api/club/register`, {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify(payload),
        });
        const result = await res.json();
        if (result && result.code === 0) {
          msg.className = 'msg success';
          msg.textContent = '报名成功!我们会尽快联系你 🎉';
          fields.forEach((f) => { container.querySelector(`#rg-${f.key}`).value = ''; });
        } else {
          msg.className = 'msg error';
          msg.textContent = (result && result.msg) || '提交失败，请稍后再试';
        }
      } catch {
        msg.className = 'msg error';
        msg.textContent = '网络错误，请稍后再试（若后台服务未部署，报名暂不可用）';
      } finally {
        btn.disabled = false;
        btn.textContent = '提交报名';
      }
    });
  }

  return {
    mount(container, cfg = {}) {
      if (!container) return;
      injectStyles();
      const fields = cfg.fields || DEFAULT_FIELDS;
      if (cfg.open === false) {
        renderClosed(container, cfg);
        return;
      }
      render(container, cfg, fields);
      bindEvents(container, cfg, fields);
    },
  };
})();
