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
  <div class="w-full max-w-sm">
    <div class="glass-card rounded-2xl p-8">
      <div class="flex flex-col items-center text-center">
        <span
          class="flex h-11 w-11 items-center justify-center rounded-xl bg-gradient-to-br from-accent to-accent-soft font-display text-lg font-bold text-white"
        >AI</span>
        <h1 class="mt-4 font-display text-2xl font-semibold text-white">Admin Login</h1>
        <p class="mt-1 text-sm text-slate-400">AI+CLUB 运营管理端</p>
      </div>

      <form class="mt-7 space-y-4" @submit.prevent="submit">
        <div>
          <label class="mb-1.5 block text-sm font-medium text-slate-300">用户名</label>
          <input
            v-model="username"
            type="text"
            autocomplete="username"
            placeholder="admin"
            class="w-full rounded-lg border border-white/10 bg-white/5 px-3.5 py-2.5 text-sm text-white placeholder-slate-500 outline-none transition focus:border-accent/60 focus:bg-white/10 focus:ring-2 focus:ring-accent/20"
          />
        </div>
        <div>
          <label class="mb-1.5 block text-sm font-medium text-slate-300">密码</label>
          <input
            v-model="password"
            type="password"
            autocomplete="current-password"
            placeholder="••••••••"
            class="w-full rounded-lg border border-white/10 bg-white/5 px-3.5 py-2.5 text-sm text-white placeholder-slate-500 outline-none transition focus:border-accent/60 focus:bg-white/10 focus:ring-2 focus:ring-accent/20"
          />
        </div>

        <button
          type="submit"
          :disabled="submitting"
          class="w-full rounded-full bg-gradient-to-r from-accent to-accent-soft py-2.5 font-display text-sm font-semibold text-white transition hover:opacity-90 active:scale-95 disabled:cursor-not-allowed disabled:opacity-50"
        >
          {{ submitting ? 'Signing in…' : 'Sign In' }}
        </button>

        <p
          v-if="msg"
          class="text-center text-sm"
          :class="{ 'text-danger': msg.type === 'error', 'text-slate-400': msg.type === 'info' }"
        >{{ msg.text }}</p>
      </form>
    </div>

    <a
      href="/"
      class="mt-5 block text-center text-sm text-slate-400 transition hover:text-white"
    >← Back to AI+CLUB</a>
  </div>
</template>
