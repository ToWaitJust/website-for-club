// ─── 反馈信箱组件 ──────────────────────────────────────────────────────
// 用法：在页面底部调用 FeedbackBox.mount(containerElement, pageName)

const FeedbackBox = (() => {
  const STYLES = `
    .feedback-box {
      max-width: 720px;
      margin: 4rem auto 2rem;
      padding: 2.5rem 2rem;
      background: #fff;
      border-radius: 20px;
      box-shadow: 0 2px 16px rgba(0,0,0,0.06);
      font-family: -apple-system, 'PingFang SC', 'Microsoft YaHei', sans-serif;
    }
    .feedback-box h2 {
      font-size: 1.5rem;
      font-weight: 700;
      color: #1d1d1f;
      margin: 0 0 0.4rem;
    }
    .feedback-box .sub {
      font-size: 0.92rem;
      color: #86868b;
      margin: 0 0 1.6rem;
      line-height: 1.5;
    }
    .feedback-box .sub strong {
      color: #1d1d1f;
      font-weight: 600;
    }
    .feedback-box .field {
      margin-bottom: 1.2rem;
    }
    .feedback-box .field label {
      display: block;
      font-size: 0.85rem;
      font-weight: 600;
      color: #1d1d1f;
      margin-bottom: 0.35rem;
    }
    .feedback-box .field input,
    .feedback-box .field textarea {
      width: 100%;
      padding: 0.7rem 0.9rem;
      border: 1.5px solid #e5e5ea;
      border-radius: 12px;
      font-size: 0.95rem;
      font-family: inherit;
      outline: none;
      transition: border-color 0.25s, box-shadow 0.25s;
      box-sizing: border-box;
    }
    .feedback-box .field input:focus,
    .feedback-box .field textarea:focus {
      border-color: #0071e3;
      box-shadow: 0 0 0 3px rgba(0,113,227,0.15);
    }
    .feedback-box .field textarea {
      resize: vertical;
      min-height: 100px;
    }
    .feedback-box .field .hint {
      font-size: 0.8rem;
      color: #86868b;
      margin-top: 0.3rem;
    }
    .feedback-box .btn-row {
      display: flex;
      gap: 0.75rem;
      align-items: center;
    }
    .feedback-box .btn-submit {
      padding: 0.7rem 2rem;
      background: #0071e3;
      color: #fff;
      border: none;
      border-radius: 40px;
      font-size: 0.95rem;
      font-weight: 600;
      cursor: pointer;
      transition: background 0.25s, transform 0.15s;
    }
    .feedback-box .btn-submit:hover { background: #0077ed; }
    .feedback-box .btn-submit:active { transform: scale(0.97); }
    .feedback-box .btn-submit:disabled {
      background: #a0c8f0;
      cursor: not-allowed;
      transform: none;
    }
    .feedback-box .msg {
      font-size: 0.9rem;
      padding: 0.5rem 0;
    }
    .feedback-box .msg.success { color: #1d8a3a; }
    .feedback-box .msg.error { color: #d32f2f; }
    .feedback-box .msg.info { color: #0071e3; }
  `;

  function injectStyles() {
    if (!document.getElementById('fb-styles')) {
      const s = document.createElement('style');
      s.id = 'fb-styles';
      s.textContent = STYLES;
      document.head.appendChild(s);
    }
  }

  function render(container, pageLabel) {
    container.innerHTML = `
      <div class="feedback-box">
        <h2>📬 反馈信箱</h2>
        <p class="sub">
          我们正在持续优化<strong>${pageLabel}</strong>。
          你的每一条建议——无论是学习规划、运营方案还是社团建设——都在帮助我们变得更好 ✨
        </p>
        <div class="field">
          <label for="fb-name">您的姓名</label>
          <input type="text" id="fb-name" placeholder="怎么称呼您？" maxlength="50" />
        </div>
        <div class="field">
          <label for="fb-content">反馈内容</label>
          <textarea id="fb-content" placeholder="请畅所欲言… 哪些地方可以优化？有什么新想法？"></textarea>
          <div class="hint">💡 您的每一条反馈都会认真阅读和记录</div>
        </div>
        <div class="btn-row">
          <button class="btn-submit" id="fb-submit">提交反馈</button>
          <span class="msg" id="fb-msg"></span>
        </div>
      </div>
    `;
  }

  function bindEvents(container, pageKey) {
    const btn = container.querySelector('#fb-submit');
    const nameInput = container.querySelector('#fb-name');
    const contentInput = container.querySelector('#fb-content');
    const msg = container.querySelector('#fb-msg');

    btn.addEventListener('click', async () => {
      const name = nameInput.value.trim();
      const content = contentInput.value.trim();

      if (!name) { msg.className = 'msg error'; msg.textContent = '请填写您的姓名'; return; }
      if (!content || content.length < 5) {
        msg.className = 'msg error';
        msg.textContent = content ? '内容太短了，再多写几句吧 😊' : '请填写反馈内容';
        return;
      }

      btn.disabled = true;
      btn.textContent = '提交中…';
      msg.className = 'msg info';
      msg.textContent = '正在提交…';

      try {
        const res = await fetch('/api/feedback', {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify({ name, content, page: pageKey }),
        });
        const data = await res.json();
        if (data.success) {
          msg.className = 'msg success';
          msg.textContent = data.message || '感谢您的反馈！🎉';
          nameInput.value = '';
          contentInput.value = '';
        } else {
          msg.className = 'msg error';
          msg.textContent = data.error || '提交失败，请稍后再试';
        }
      } catch {
        msg.className = 'msg error';
        msg.textContent = '网络错误，请稍后再试';
      } finally {
        btn.disabled = false;
        btn.textContent = '提交反馈';
      }
    });
  }

  return {
    mount(container, pageKey, pageLabel) {
      injectStyles();
      render(container, pageLabel);
      bindEvents(container, pageKey);
    },
  };
})();