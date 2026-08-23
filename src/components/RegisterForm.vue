<script setup lang="ts">
// ─── 报名表单(Vue 岛)────────────────────────────────────────────────
// 提交: POST {apiBase}/admin-api/club/register (yudao 公开接口 @PermitAll)
import { ref } from 'vue';
import { api, type RegisterPayload } from '../lib/api';

interface Props {
  businessLine: string;
  businessName: string;
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
    const payload: RegisterPayload = {
      businessLine: props.businessLine,
      name: (form.value['name'] || '').trim(),
      studentId: (form.value['studentId'] || '').trim(),
      college: (form.value['college'] || '').trim(),
      major: (form.value['major'] || '').trim(),
      phone: (form.value['phone'] || '').trim(),
      wechat: (form.value['wechat'] || '').trim(),
      motivation: (form.value['motivation'] || '').trim(),
    };
    await api.register(payload);
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
  <div
    class="glass-card mx-auto max-w-[var(--form-max)] rounded-xl"
    style="padding: var(--sp-8) var(--sp-6)"
  >
    <template v-if="props.open">
      <h2
        class="font-display font-semibold text-white"
        style="font-size: var(--fs-h2)"
      >
        Join {{ props.businessName }}
      </h2>
      <p
        class="text-slate-400"
        style="font-size: var(--fs-sm); margin-top: var(--sp-1)"
      >
        填写信息提交后,我们会在招新周期内通过微信 / 短信联系你。
      </p>

      <!-- 窄屏单列 → sm 双列;gap 与上边距流式 -->
      <form
        class="grid gap-f4 sm:grid-cols-2"
        style="margin-top: var(--sp-6)"
        @submit.prevent="submit"
      >
        <div
          v-for="f in fields"
          :key="f.key"
          class="min-w-0 sm:last:col-span-2"
          :class="{ 'sm:col-span-2': f.area }"
        >
          <label
            class="block font-medium text-slate-300"
            style="font-size: var(--fs-sm); margin-bottom: var(--sp-1)"
          >
            {{ f.label }} <span v-if="f.required" class="text-danger">*</span>
          </label>
          <!-- 输入控件高度 ≥44px(触摸友好);字号由 global.css 统一控制(iOS 防缩放) -->
          <textarea
            v-if="f.area"
            v-model="form[f.key]"
            :placeholder="f.placeholder"
            :maxlength="f.max"
            rows="3"
            class="w-full resize-y rounded-lg border border-white/10 bg-white/5 text-white placeholder-slate-500 outline-none transition focus:border-accent/60 focus:bg-white/10 focus:ring-2 focus:ring-accent/20"
            style="padding: var(--sp-3) var(--sp-3); min-height: calc(var(--tap) * 1.8)"
          ></textarea>
          <input
            v-else
            v-model="form[f.key]"
            type="text"
            :placeholder="f.placeholder"
            :maxlength="f.max"
            class="w-full rounded-lg border border-white/10 bg-white/5 text-white placeholder-slate-500 outline-none transition focus:border-accent/60 focus:bg-white/10 focus:ring-2 focus:ring-accent/20"
            style="padding: var(--sp-2) var(--sp-3); min-height: var(--tap)"
          />
        </div>

        <!-- 窄屏纵向堆叠,提交按钮不被提示文字挤压 -->
        <div
          class="flex flex-col items-start gap-f3 sm:col-span-2 sm:flex-row sm:items-center sm:gap-f4"
        >
          <button
            type="submit"
            :disabled="submitting"
            class="tap-target w-full shrink-0 rounded-full bg-gradient-to-r from-accent to-accent-soft font-display font-semibold text-white transition hover:opacity-90 active:scale-95 disabled:cursor-not-allowed disabled:opacity-50 sm:w-auto"
            style="padding: var(--sp-3) var(--sp-8); font-size: var(--fs-sm)"
          >
            {{ submitting ? 'Submitting…' : 'Submit' }}
          </button>
          <span
            v-if="msg"
            class="min-w-0"
            style="font-size: var(--fs-sm)"
            :class="{ 'text-success': msg.type === 'success', 'text-danger': msg.type === 'error', 'text-slate-400': msg.type === 'info' }"
          >{{ msg.text }}</span>
        </div>
      </form>
    </template>

    <template v-else>
      <p
        class="text-center font-display text-white"
        style="font-size: var(--fs-h3)"
      >
        Registration Closed
      </p>
      <p
        class="text-center text-slate-400"
        style="font-size: var(--fs-sm); margin-top: var(--sp-2)"
      >
        {{ props.businessName }} 的报名通道当前关闭,请关注公告或加入咨询群。
      </p>
    </template>
  </div>
</template>
