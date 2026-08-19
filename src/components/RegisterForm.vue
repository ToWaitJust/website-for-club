<script setup lang="ts">
// ─── 报名表单(Vue 岛)────────────────────────────────────────────────
// 提交: POST {apiBase}/admin-api/club/register (yudao 公开接口 @PermitAll)
import { ref } from 'vue';
import { post } from '../lib/api';

interface Props {
  businessLine: string;
  businessName: string;
  apiBase: string;
  open?: boolean;
}

const props = withDefaults(defineProps<Props>(), { open: true });

const fields = [
  { key: 'name', label: '姓名', required: true, placeholder: '您的姓名', max: 50 },
  { key: 'studentId', label: '学号', required: true, placeholder: '如 2025xxxxxx', max: 20 },
  { key: 'college', label: '学院', required: true, placeholder: '所在学院', max: 50 },
  { key: 'major', label: '专业', required: false, placeholder: '所在专业', max: 50 },
  { key: 'phone', label: '手机号', required: true, placeholder: '用于联系通知', max: 20 },
  { key: 'wechat', label: '微信号', required: false, placeholder: '方便拉群', max: 50 },
  { key: 'motivation', label: '报名动机', required: false, placeholder: '为什么想加入这条业务线?', max: 1000, area: true },
];

const form = ref<Record<string, string>>({});
const submitting = ref(false);
const msg = ref<{ type: 'error' | 'success' | 'info'; text: string } | null>(null);

async function submit() {
  msg.value = null;
  for (const f of fields) {
    if (f.required && !(form.value[f.key] || '').trim()) {
      msg.value = { type: 'error', text: `请填写${f.label}` };
      return;
    }
  }
  submitting.value = true;
  msg.value = { type: 'info', text: '正在提交…' };
  try {
    const payload: Record<string, string> = { businessLine: props.businessLine };
    fields.forEach((f) => { payload[f.key] = (form.value[f.key] || '').trim(); });
    await post(`${props.apiBase}/admin-api/club/register`, payload);
    msg.value = { type: 'success', text: '报名成功!我们会尽快联系你 🎉' };
    form.value = {};
  } catch (e) {
    msg.value = { type: 'error', text: (e as Error).message };
  } finally {
    submitting.value = false;
  }
}
</script>

<template>
  <div class="glass-card mx-auto max-w-2xl rounded-xl p-7 sm:p-9">
    <template v-if="props.open">
      <h2 class="font-display text-2xl font-semibold text-white">
        Join {{ props.businessName }}
      </h2>
      <p class="mt-1.5 text-sm text-slate-400">
        填写信息提交后,我们会在招新周期内通过微信 / 短信联系你。
      </p>

      <form class="mt-7 grid gap-4 sm:grid-cols-2" @submit.prevent="submit">
        <div v-for="f in fields" :key="f.key" class="sm:last:col-span-2" :class="{ 'sm:col-span-2': f.area }">
          <label class="mb-1.5 block text-sm font-medium text-slate-300">
            {{ f.label }} <span v-if="f.required" class="text-danger">*</span>
          </label>
          <textarea
            v-if="f.area"
            v-model="form[f.key]"
            :placeholder="f.placeholder"
            :maxlength="f.max"
            rows="3"
            class="w-full resize-y rounded-lg border border-white/10 bg-white/5 px-3.5 py-2.5 text-sm text-white placeholder-slate-500 outline-none transition focus:border-accent/60 focus:bg-white/10 focus:ring-2 focus:ring-accent/20"
          ></textarea>
          <input
            v-else
            v-model="form[f.key]"
            type="text"
            :placeholder="f.placeholder"
            :maxlength="f.max"
            class="w-full rounded-lg border border-white/10 bg-white/5 px-3.5 py-2.5 text-sm text-white placeholder-slate-500 outline-none transition focus:border-accent/60 focus:bg-white/10 focus:ring-2 focus:ring-accent/20"
          />
        </div>

        <div class="flex items-center gap-4 sm:col-span-2">
          <button
            type="submit"
            :disabled="submitting"
            class="rounded-full bg-gradient-to-r from-accent to-accent-soft px-8 py-2.5 font-display text-sm font-semibold text-white transition hover:opacity-90 active:scale-95 disabled:cursor-not-allowed disabled:opacity-50"
          >
            {{ submitting ? 'Submitting…' : 'Submit' }}
          </button>
          <span
            v-if="msg"
            class="text-sm"
            :class="{ 'text-success': msg.type === 'success', 'text-danger': msg.type === 'error', 'text-slate-400': msg.type === 'info' }"
          >{{ msg.text }}</span>
        </div>
      </form>
    </template>

    <template v-else>
      <p class="text-center font-display text-xl text-white">Registration Closed</p>
      <p class="mt-2 text-center text-sm text-slate-400">
        {{ props.businessName }} 的报名通道当前关闭,请关注公告或加入咨询群。
      </p>
    </template>
  </div>
</template>
