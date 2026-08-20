<script setup lang="ts">
// ─── 反馈信箱(Vue 岛)────────────────────────────────────────────────
// 提交: POST {apiBase}/admin-api/club/feedback (yudao 公开接口 @PermitAll)
import { ref } from 'vue';
import { post } from '../lib/api';

interface Props {
  page: string;
  apiBase: string;
}

const props = defineProps<Props>();

const name = ref('');
const content = ref('');
const submitting = ref(false);
const msg = ref<{ type: 'error' | 'success' | 'info'; text: string } | null>(null);

async function submit() {
  msg.value = null;
  if (!name.value.trim()) {
    msg.value = { type: 'error', text: '请填写您的姓名' };
    return;
  }
  if (content.value.trim().length < 5) {
    msg.value = { type: 'error', text: content.value.trim() ? '内容太短了,再多写几句吧' : '请填写反馈内容' };
    return;
  }
  submitting.value = true;
  msg.value = { type: 'info', text: '正在提交…' };
  try {
    await post(`${props.apiBase}/admin-api/club/feedback`, {
      page: props.page,
      name: name.value.trim(),
      content: content.value.trim(),
    });
    msg.value = { type: 'success', text: '感谢反馈!我们每一条都会认真阅读 🚀' };
    name.value = '';
    content.value = '';
  } catch (e) {
    msg.value = { type: 'error', text: (e as Error).message };
  } finally {
    submitting.value = false;
  }
}
</script>

<template>
  <!-- 眉标:与首页 Announcements 标题风格一致,置于卡片上方 -->
  <div style="margin-top: var(--sp-16)">
    <h2
      class="font-display uppercase text-slate-400"
      style="font-size: var(--fs-h3); letter-spacing: clamp(0.12em, 0.6vw, 0.22em); margin-bottom: var(--sp-4)"
    >
      Feedback
    </h2>
    <!-- 外层 container-site 由 BaseLayout 提供,此处只管自身内边距 -->
    <section
      class="glass-card mx-auto max-w-[var(--content-max)] rounded-xl"
      style="padding: var(--sp-6)"
    >
      <p class="text-slate-400" style="font-size: var(--fs-sm)">
        你的每一条建议——学习规划、活动安排、社团建设——都在帮助我们变得更好 ✨
      </p>

      <!-- 姓名:整行,标签在上、输入框在下,左右严格对齐 -->
      <div style="margin-top: var(--sp-6)">
        <label
          class="block font-medium text-slate-300"
          style="font-size: var(--fs-sm); margin-bottom: var(--sp-1)"
        >
          姓名 <span class="text-danger">*</span>
        </label>
        <input
          v-model="name"
          type="text"
          maxlength="50"
          placeholder="怎么称呼您?"
          class="w-full rounded-lg border border-white/10 bg-white/5 text-white placeholder-slate-500 outline-none transition focus:border-accent/60 focus:bg-white/10 focus:ring-2 focus:ring-accent/20"
          style="padding: var(--sp-2) var(--sp-3); min-height: var(--tap)"
        />
      </div>

      <!-- 反馈内容:整行 -->
      <div style="margin-top: var(--sp-5)">
        <label
          class="block font-medium text-slate-300"
          style="font-size: var(--fs-sm); margin-bottom: var(--sp-1)"
        >
          反馈内容 <span class="text-danger">*</span>
        </label>
        <textarea
          v-model="content"
          maxlength="2000"
          rows="3"
          placeholder="请畅所欲言… 哪些地方可以优化?有什么新想法?"
          class="w-full resize-y rounded-lg border border-white/10 bg-white/5 text-white placeholder-slate-500 outline-none transition focus:border-accent/60 focus:bg-white/10 focus:ring-2 focus:ring-accent/20"
          style="padding: var(--sp-3) var(--sp-3); min-height: calc(var(--tap) * 1.8)"
        ></textarea>
      </div>

      <!-- 操作行:按钮 + 状态消息 -->
      <div
        class="flex flex-col items-start gap-f3 sm:flex-row sm:items-center sm:gap-f4"
        style="margin-top: var(--sp-5)"
      >
        <button
          type="button"
          :disabled="submitting"
          class="tap-target w-full shrink-0 rounded-full border border-white/15 bg-white/5 font-display font-medium text-white transition hover:bg-white/10 active:scale-95 disabled:cursor-not-allowed disabled:opacity-50 sm:w-auto"
          style="padding: var(--sp-2) var(--sp-6); font-size: var(--fs-sm)"
          @click="submit"
        >
          {{ submitting ? 'Sending…' : 'Send' }}
        </button>
        <span
          v-if="msg"
          class="min-w-0"
          style="font-size: var(--fs-sm)"
          :class="{ 'text-success': msg.type === 'success', 'text-danger': msg.type === 'error', 'text-slate-400': msg.type === 'info' }"
        >{{ msg.text }}</span>
      </div>
    </section>
  </div>
</template>
