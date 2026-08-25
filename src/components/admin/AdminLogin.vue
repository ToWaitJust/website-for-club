<script setup lang="ts">
// ─── 管理员登录(Vue 岛)──────────────────────────────────────────────
// 调 yudao /system/auth/login,成功存 token 并跳转管理端
import { ref } from 'vue';
import { login, setToken } from '../../lib/api';

const username = ref('');
const password = ref('');
const submitting = ref(false);
const msg = ref<{ type: 'error' | 'info'; text: string } | null>(null);

async function submit() {
  msg.value = null;
  if (!username.value.trim() || !password.value) {
    msg.value = { type: 'error', text: '请输入用户名和密码' };
    return;
  }
  submitting.value = true;
  msg.value = { type: 'info', text: '验证中…' };
  try {
    const data = await login(username.value.trim(), password.value);
    setToken(data.accessToken);
    // 跳回管理端(支持 ?redirect= 回跳)
    const redirect = new URLSearchParams(location.search).get('redirect') || '/admin/';
    location.href = redirect;
  } catch (e) {
    msg.value = { type: 'error', text: (e as Error).message };
  } finally {
    submitting.value = false;
  }
}
</script>

<template>
  <div class="w-full max-w-[var(--form-max)]">
    <div class="glass-card rounded-2xl" style="padding: var(--sp-8) var(--sp-6)">
      <div class="flex flex-col items-center text-center">
        <span
          class="flex items-center justify-center font-bold text-gradient-brand"
          style="width: min(calc(88 * var(--rpx)), 96px); height: min(calc(88 * var(--rpx)), 96px); min-width: 40px; min-height: 40px; font-size: var(--fs-d2)"
        >♾</span>
        <h1
          class="font-display font-semibold text-ink"
          style="font-size: var(--fs-h2); margin-top: var(--sp-4)"
        >
          Admin Login
        </h1>
        <p
          class="text-muted"
          style="font-size: var(--fs-sm); margin-top: var(--sp-1)"
        >
          AI+CLUB 运营管理端
        </p>
      </div>

      <form class="grid gap-f4" style="margin-top: var(--sp-6)" @submit.prevent="submit">
        <div>
          <label
            class="block font-medium text-ink-soft"
            style="font-size: var(--fs-sm); margin-bottom: var(--sp-1)"
          >
            用户名
          </label>
          <input
            v-model="username"
            type="text"
            autocomplete="username"
            placeholder="admin"
            class="w-full rounded-lg border border-line bg-surface text-ink placeholder:text-muted outline-none transition focus:border-accent/60 focus:bg-surface-strong focus:ring-2 focus:ring-accent/20"
            style="padding: var(--sp-2) var(--sp-3); min-height: var(--tap)"
          />
        </div>
        <div>
          <label
            class="block font-medium text-ink-soft"
            style="font-size: var(--fs-sm); margin-bottom: var(--sp-1)"
          >
            密码
          </label>
          <input
            v-model="password"
            type="password"
            autocomplete="current-password"
            placeholder="••••••••"
            class="w-full rounded-lg border border-line bg-surface text-ink placeholder:text-muted outline-none transition focus:border-accent/60 focus:bg-surface-strong focus:ring-2 focus:ring-accent/20"
            style="padding: var(--sp-2) var(--sp-3); min-height: var(--tap)"
          />
        </div>

        <button
          type="submit"
          :disabled="submitting"
          class="btn-brand tap-target w-full rounded-full bg-gradient-to-r from-accent to-accent-soft font-display font-semibold text-white disabled:cursor-not-allowed disabled:opacity-50"
          style="padding: var(--sp-3) var(--sp-4); font-size: var(--fs-sm)"
        >
          {{ submitting ? 'Signing in…' : 'Sign In' }}
        </button>

        <p
          v-if="msg"
          class="text-center"
          style="font-size: var(--fs-sm)"
          :class="{ 'text-danger': msg.type === 'error', 'text-muted': msg.type === 'info' }"
        >{{ msg.text }}</p>
      </form>
    </div>

    <a
      href="/"
      class="block text-center text-muted transition hover:text-ink"
      style="font-size: var(--fs-sm); margin-top: var(--sp-5)"
    >← Back to AI+CLUB</a>
  </div>
</template>
