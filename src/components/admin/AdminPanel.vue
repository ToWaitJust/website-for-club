<script setup lang="ts">
// ─── 管理端面板(Vue 岛)──────────────────────────────────────────────
// 登录态守卫 + 报名管理 + 反馈管理;深色玻璃风格与门户统一
import { computed, onMounted, ref } from 'vue';
import {
  clearToken,
  getFeedbackPage,
  getRegisterPage,
  getToken,
  REGISTER_STATUS_MAP,
  type FeedbackRecord,
  type RegisterRecord,
  updateFeedbackStatus,
  updateRegisterStatus,
} from '../../lib/api';

const BUSINESS_MAP: Record<string, string> = {
  'ai-it': 'AI+IT',
  'ai-biz': 'AI+Biz',
  'ai-embed': 'AI+Embed',
};
const STATUS_BADGE: Record<number, string> = {
  0: 'bg-amber-500/15 text-amber-300 border-amber-400/30',
  1: 'bg-sky-500/15 text-sky-300 border-sky-400/30',
  2: 'bg-emerald-500/15 text-emerald-300 border-emerald-400/30',
  3: 'bg-rose-500/15 text-rose-300 border-rose-400/30',
};

// ─── 登录态 ──────────────────────────────────────────────────────────
const authed = ref(false);
onMounted(() => {
  if (!getToken()) {
    location.href = '/admin/login?redirect=' + encodeURIComponent(location.pathname);
    return;
  }
  authed.value = true;
  loadRegister();
});

function logout() {
  clearToken();
  location.href = '/admin/login';
}

// ─── Tab ─────────────────────────────────────────────────────────────
const tab = ref<'register' | 'feedback'>('register');

// ─── 报名管理 ────────────────────────────────────────────────────────
const registers = ref<RegisterRecord[]>([]);
const registerTotal = ref(0);
const registerPage = ref(1);
const registerFilter = ref<{ businessLine: string; status: number | '' }>({ businessLine: '', status: '' });
const registerLoading = ref(false);
const registerError = ref('');

async function loadRegister() {
  registerLoading.value = true;
  registerError.value = '';
  try {
    const data = await getRegisterPage({
      pageNo: registerPage.value,
      pageSize: 20,
      businessLine: registerFilter.value.businessLine || undefined,
      status: registerFilter.value.status,
    });
    registers.value = data.list;
    registerTotal.value = data.total;
  } catch (e) {
    registerError.value = (e as Error).message;
  } finally {
    registerLoading.value = false;
  }
}

async function changeRegisterStatus(row: RegisterRecord, status: number) {
  try {
    await updateRegisterStatus(row.id, status);
    row.status = status;
  } catch (e) {
    alert((e as Error).message);
  }
}

// ─── 反馈管理 ────────────────────────────────────────────────────────
const feedbacks = ref<FeedbackRecord[]>([]);
const feedbackTotal = ref(0);
const feedbackPage = ref(1);
const feedbackFilter = ref<{ status: number | '' }>({ status: '' });
const feedbackLoading = ref(false);
const feedbackError = ref('');

async function loadFeedback() {
  feedbackLoading.value = true;
  feedbackError.value = '';
  try {
    const data = await getFeedbackPage({
      pageNo: feedbackPage.value,
      pageSize: 20,
      status: feedbackFilter.value.status,
    });
    feedbacks.value = data.list;
    feedbackTotal.value = data.total;
  } catch (e) {
    feedbackError.value = (e as Error).message;
  } finally {
    feedbackLoading.value = false;
  }
}

async function toggleFeedbackRead(row: FeedbackRecord) {
  try {
    const next = row.status === 1 ? 0 : 1;
    await updateFeedbackStatus(row.id, next);
    row.status = next;
  } catch (e) {
    alert((e as Error).message);
  }
}

// ─── 工具 ────────────────────────────────────────────────────────────
function fmtTime(ts: number): string {
  const d = new Date(ts);
  const p = (n: number) => String(n).padStart(2, '0');
  return `${d.getFullYear()}-${p(d.getMonth() + 1)}-${p(d.getDate())} ${p(d.getHours())}:${p(d.getMinutes())}`;
}

const registerTotalPages = computed(() => Math.max(1, Math.ceil(registerTotal.value / 20)));
const feedbackTotalPages = computed(() => Math.max(1, Math.ceil(feedbackTotal.value / 20)));

function switchTab(t: 'register' | 'feedback') {
  tab.value = t;
  if (t === 'register') loadRegister();
  else loadFeedback();
}
</script>

<template>
  <div v-if="authed" class="w-full max-w-5xl">
    <!-- 顶栏 -->
    <div class="flex flex-wrap items-center justify-between gap-4">
      <div>
        <h1 class="font-display text-2xl font-semibold text-white">Admin Console</h1>
        <p class="mt-1 text-sm text-slate-400">AI+CLUB 运营管理端 · 报名与反馈数据实时同步自 yudao</p>
      </div>
      <div class="flex items-center gap-3">
        <a
          href="/"
          class="rounded-full border border-white/15 bg-white/5 px-4 py-1.5 text-sm text-slate-300 transition hover:bg-white/10 hover:text-white"
        >← Site</a>
        <button
          @click="logout"
          class="rounded-full border border-rose-400/30 bg-rose-500/10 px-4 py-1.5 text-sm text-rose-300 transition hover:bg-rose-500/20"
        >Sign Out</button>
      </div>
    </div>

    <!-- Tab 切换 -->
    <div class="mt-6 flex gap-2">
      <button
        @click="switchTab('register')"
        class="rounded-full px-5 py-2 text-sm font-medium transition"
        :class="tab === 'register' ? 'bg-white/15 text-white' : 'bg-white/5 text-slate-400 hover:bg-white/10 hover:text-white'"
      >报名管理 ({{ registerTotal }})</button>
      <button
        @click="switchTab('feedback')"
        class="rounded-full px-5 py-2 text-sm font-medium transition"
        :class="tab === 'feedback' ? 'bg-white/15 text-white' : 'bg-white/5 text-slate-400 hover:bg-white/10 hover:text-white'"
      >反馈管理 ({{ feedbackTotal }})</button>
    </div>

    <!-- ─── 报名管理 ─── -->
    <div v-if="tab === 'register'" class="glass-card mt-5 rounded-xl p-5 sm:p-6">
      <div class="flex flex-wrap items-center gap-3">
        <select
          v-model="registerFilter.businessLine"
          @change="registerPage = 1; loadRegister()"
          class="rounded-lg border border-white/10 bg-[#101a2e] px-3 py-2 text-sm text-white outline-none focus:border-accent/60"
        >
          <option value="">全部业务线</option>
          <option v-for="(name, slug) in BUSINESS_MAP" :key="slug" :value="slug">{{ name }}</option>
        </select>
        <select
          v-model="registerFilter.status"
          @change="registerPage = 1; loadRegister()"
          class="rounded-lg border border-white/10 bg-[#101a2e] px-3 py-2 text-sm text-white outline-none focus:border-accent/60"
        >
          <option value="">全部状态</option>
          <option v-for="(label, st) in REGISTER_STATUS_MAP" :key="st" :value="Number(st)">{{ label }}</option>
        </select>
        <span class="ml-auto text-sm text-slate-400">共 {{ registerTotal }} 条</span>
      </div>

      <p v-if="registerError" class="mt-3 text-sm text-danger">{{ registerError }}</p>

      <div class="mt-4 overflow-x-auto">
        <table class="w-full min-w-[760px] text-left text-sm">
          <thead>
            <tr class="border-b border-white/10 text-xs uppercase tracking-wider text-slate-500">
              <th class="pb-2.5 pr-3 font-medium">姓名</th>
              <th class="pb-2.5 pr-3 font-medium">业务线</th>
              <th class="pb-2.5 pr-3 font-medium">学号</th>
              <th class="pb-2.5 pr-3 font-medium">学院 / 专业</th>
              <th class="pb-2.5 pr-3 font-medium">联系方式</th>
              <th class="pb-2.5 pr-3 font-medium">状态</th>
              <th class="pb-2.5 pr-3 font-medium">提交时间</th>
              <th class="pb-2.5 font-medium">操作</th>
            </tr>
          </thead>
          <tbody>
            <tr
              v-for="row in registers"
              :key="row.id"
              class="border-b border-white/5 transition hover:bg-white/[0.03]"
            >
              <td class="py-3 pr-3 font-medium text-white">{{ row.name }}</td>
              <td class="py-3 pr-3">
                <span class="rounded-full border border-white/10 bg-white/5 px-2 py-0.5 text-xs text-slate-300">
                  {{ BUSINESS_MAP[row.businessLine] || row.businessLine }}
                </span>
              </td>
              <td class="py-3 pr-3 text-slate-400">{{ row.studentId || '—' }}</td>
              <td class="py-3 pr-3 text-slate-400">{{ row.college || '—' }}<span v-if="row.major"> / {{ row.major }}</span></td>
              <td class="py-3 pr-3 text-slate-400">
                <span v-if="row.phone">{{ row.phone }}</span>
                <span v-if="row.wechat" class="text-slate-500"> ({{ row.wechat }})</span>
              </td>
              <td class="py-3 pr-3">
                <span class="rounded-full border px-2 py-0.5 text-xs" :class="STATUS_BADGE[row.status] || 'border-white/10 text-slate-400'">
                  {{ REGISTER_STATUS_MAP[row.status] || row.status }}
                </span>
              </td>
              <td class="py-3 pr-3 text-xs text-slate-500">{{ fmtTime(row.createTime) }}</td>
              <td class="py-3">
                <div class="flex flex-wrap gap-1">
                  <button
                    v-for="(label, st) in REGISTER_STATUS_MAP"
                    :key="st"
                    @click="changeRegisterStatus(row, Number(st))"
                    :disabled="row.status === Number(st)"
                    class="rounded-md border border-white/10 bg-white/5 px-2 py-0.5 text-xs text-slate-300 transition hover:bg-white/15 hover:text-white disabled:cursor-default disabled:opacity-40"
                  >{{ label }}</button>
                </div>
              </td>
            </tr>
            <tr v-if="!registerLoading && registers.length === 0">
              <td colspan="8" class="py-10 text-center text-sm text-slate-500">暂无报名数据</td>
            </tr>
            <tr v-if="registerLoading">
              <td colspan="8" class="py-10 text-center text-sm text-slate-400">加载中…</td>
            </tr>
          </tbody>
        </table>
      </div>

      <div v-if="registerTotalPages > 1" class="mt-4 flex items-center justify-end gap-3 text-sm">
        <button
          :disabled="registerPage <= 1"
          @click="registerPage--; loadRegister()"
          class="rounded-lg border border-white/10 bg-white/5 px-3 py-1.5 text-slate-300 transition hover:bg-white/10 disabled:opacity-40"
        >← Prev</button>
        <span class="text-slate-400">{{ registerPage }} / {{ registerTotalPages }}</span>
        <button
          :disabled="registerPage >= registerTotalPages"
          @click="registerPage++; loadRegister()"
          class="rounded-lg border border-white/10 bg-white/5 px-3 py-1.5 text-slate-300 transition hover:bg-white/10 disabled:opacity-40"
        >Next →</button>
      </div>
    </div>

    <!-- ─── 反馈管理 ─── -->
    <div v-else class="glass-card mt-5 rounded-xl p-5 sm:p-6">
      <div class="flex flex-wrap items-center gap-3">
        <select
          v-model="feedbackFilter.status"
          @change="feedbackPage = 1; loadFeedback()"
          class="rounded-lg border border-white/10 bg-[#101a2e] px-3 py-2 text-sm text-white outline-none focus:border-accent/60"
        >
          <option value="">全部状态</option>
          <option value="0">未处理</option>
          <option value="1">已处理</option>
        </select>
        <span class="ml-auto text-sm text-slate-400">共 {{ feedbackTotal }} 条</span>
      </div>

      <p v-if="feedbackError" class="mt-3 text-sm text-danger">{{ feedbackError }}</p>

      <div class="mt-4 space-y-3">
        <div
          v-for="row in feedbacks"
          :key="row.id"
          class="flex flex-wrap items-start justify-between gap-3 rounded-lg border border-white/5 bg-white/[0.03] p-4"
          :class="{ 'opacity-60': row.status === 1 }"
        >
          <div class="min-w-0 flex-1">
            <div class="flex flex-wrap items-center gap-2 text-xs">
              <span class="font-medium text-white">{{ row.name }}</span>
              <span class="rounded-full border border-white/10 bg-white/5 px-2 py-0.5 text-slate-400">来源:{{ row.page }}</span>
              <span
                class="rounded-full border px-2 py-0.5"
                :class="row.status === 1 ? 'border-emerald-400/30 bg-emerald-500/15 text-emerald-300' : 'border-amber-400/30 bg-amber-500/15 text-amber-300'"
              >{{ row.status === 1 ? '已处理' : '未处理' }}</span>
              <span class="text-slate-500">{{ fmtTime(row.createTime) }}</span>
            </div>
            <p class="mt-2 break-words text-sm leading-relaxed text-slate-300">{{ row.content }}</p>
          </div>
          <button
            @click="toggleFeedbackRead(row)"
            class="shrink-0 rounded-full border border-white/15 bg-white/5 px-4 py-1.5 text-xs text-slate-300 transition hover:bg-white/15 hover:text-white"
          >{{ row.status === 1 ? '标记未处理' : '标记已处理' }}</button>
        </div>
        <p v-if="!feedbackLoading && feedbacks.length === 0" class="py-10 text-center text-sm text-slate-500">
          暂无反馈数据
        </p>
        <p v-if="feedbackLoading" class="py-10 text-center text-sm text-slate-400">加载中…</p>
      </div>

      <div v-if="feedbackTotalPages > 1" class="mt-4 flex items-center justify-end gap-3 text-sm">
        <button
          :disabled="feedbackPage <= 1"
          @click="feedbackPage--; loadFeedback()"
          class="rounded-lg border border-white/10 bg-white/5 px-3 py-1.5 text-slate-300 transition hover:bg-white/10 disabled:opacity-40"
        >← Prev</button>
        <span class="text-slate-400">{{ feedbackPage }} / {{ feedbackTotalPages }}</span>
        <button
          :disabled="feedbackPage >= feedbackTotalPages"
          @click="feedbackPage++; loadFeedback()"
          class="rounded-lg border border-white/10 bg-white/5 px-3 py-1.5 text-slate-300 transition hover:bg-white/10 disabled:opacity-40"
        >Next →</button>
      </div>
    </div>
  </div>
</template>
