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
  <section class="glass-card mx-auto mt-20 max-w-3xl rounded-xl p-7">
    <h2 class="font-display text-xl font-semibold text-white">Feedback</h2>
    <p class="mt-1 text-sm text-slate-400">
      你的每一条建议——学习规划、活动安排、社团建设——都在帮助我们变得更好 ✨
    </p>

    <div class="mt-6 grid gap-4 sm:grid-cols-[180px_1fr]">
      <div>
        <label class="mb-1.5 block text-sm font-medium text-slate-300">
          姓名 <span class="text-danger">*</span>
        </label>
        <input
          v-model="name"
          type="text"
          maxlength="50"
          placeholder="怎么称呼您?"
          class="w-full rounded-lg border border-white/10 bg-white/5 px-3.5 py-2.5 text-sm text-white placeholder-slate-500 outline-none transition focus:border-accent/60 focus:bg-white/10 focus:ring-2 focus:ring-accent/20"
        />
      </div>
      <div>
        <label class="mb-1.5 block text-sm font-medium text-slate-300">
          反馈内容 <span class="text-danger">*</span>
        </label>
        <textarea
          v-model="content"
          maxlength="2000"
          rows="3"
          placeholder="请畅所欲言… 哪些地方可以优化?有什么新想法?"
          class="w-full resize-y rounded-lg border border-white/10 bg-white/5 px-3.5 py-2.5 text-sm text-white placeholder-slate-500 outline-none transition focus:border-accent/60 focus:bg-white/10 focus:ring-2 focus:ring-accent/20"
        ></textarea>
      </div>
    </div>

    <div class="mt-5 flex items-center gap-4">
      <button
        type="button"
        :disabled="submitting"
        class="rounded-full border border-white/15 bg-white/5 px-7 py-2 font-display text-sm font-medium text-white transition hover:bg-white/10 active:scale-95 disabled:cursor-not-allowed disabled:opacity-50"
        @click="submit"
      >
        {{ submitting ? 'Sending…' : 'Send' }}
      </button>
      <span
        v-if="msg"
        class="text-sm"
        :class="{ 'text-success': msg.type === 'success', 'text-danger': msg.type === 'error', 'text-slate-400': msg.type === 'info' }"
      >{{ msg.text }}</span>
    </div>
  </section>
</template>
