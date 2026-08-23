<script setup lang="ts">
// ─── 管理端面板(Vue 岛)──────────────────────────────────────────────
// 登录态守卫 + 报名管理 + 反馈管理;深色玻璃风格与门户统一
import { computed, onMounted, ref } from 'vue';
import {
  clearToken,
  exportFeedbacks,
  exportRegisters,
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

// ─── 导出 ────────────────────────────────────────────────────────────
const exporting = ref(false);

function downloadBlob(blob: Blob, filename: string) {
  const url = URL.createObjectURL(blob);
  const a = document.createElement('a');
  a.href = url;
  a.download = filename;
  a.click();
  URL.revokeObjectURL(url);
}

async function doExportRegister() {
  exporting.value = true;
  try {
    const blob = await exportRegisters({
      businessLine: registerFilter.value.businessLine || undefined,
      status: registerFilter.value.status,
    });
    downloadBlob(blob, `报名记录_${new Date().toISOString().slice(0, 10)}.xlsx`);
  } catch (e) {
    alert((e as Error).message);
  } finally {
    exporting.value = false;
  }
}

async function doExportFeedback() {
  exporting.value = true;
  try {
    const blob = await exportFeedbacks({
      status: feedbackFilter.value.status,
    });
    downloadBlob(blob, `反馈记录_${new Date().toISOString().slice(0, 10)}.xlsx`);
  } catch (e) {
    alert((e as Error).message);
  } finally {
    exporting.value = false;
  }
}
</script>

<template>
  <div v-if="authed" class="w-full max-w-5xl">
    <!-- 顶栏:窄屏纵向堆叠,操作按钮不被标题挤压 -->
    <div class="flex flex-wrap items-center justify-between gap-f4">
      <div class="min-w-0">
        <h1
          class="font-display font-semibold text-white"
          style="font-size: var(--fs-h2)"
        >
          Admin Console
        </h1>
        <p
          class="text-slate-400"
          style="font-size: var(--fs-sm); margin-top: var(--sp-1)"
        >
          AI+CLUB 运营管理端 · 报名与反馈数据实时同步自 yudao
        </p>
      </div>
      <div class="flex shrink-0 items-center gap-f3">
        <a
          href="/"
          class="tap-target flex items-center rounded-full border border-white/15 bg-white/5 text-slate-300 transition hover:bg-white/10 hover:text-white"
          style="padding: var(--sp-2) var(--sp-4); font-size: var(--fs-sm)"
        >← Site</a>
        <button
          @click="logout"
          class="tap-target flex items-center rounded-full border border-rose-400/30 bg-rose-500/10 text-rose-300 transition hover:bg-rose-500/20"
          style="padding: var(--sp-2) var(--sp-4); font-size: var(--fs-sm)"
        >Sign Out</button>
      </div>
    </div>

    <!-- Tab 切换:窄屏可横向滚动 -->
    <div
      class="no-scrollbar flex gap-2 overflow-x-auto"
      style="margin-top: var(--sp-6)"
    >
      <button
        @click="switchTab('register')"
        class="tap-target shrink-0 whitespace-nowrap rounded-full font-medium transition"
        style="padding: var(--sp-2) var(--sp-5); font-size: var(--fs-sm)"
        :class="tab === 'register' ? 'bg-white/15 text-white' : 'bg-white/5 text-slate-400 hover:bg-white/10 hover:text-white'"
      >报名管理 ({{ registerTotal }})</button>
      <button
        @click="switchTab('feedback')"
        class="tap-target shrink-0 whitespace-nowrap rounded-full font-medium transition"
        style="padding: var(--sp-2) var(--sp-5); font-size: var(--fs-sm)"
        :class="tab === 'feedback' ? 'bg-white/15 text-white' : 'bg-white/5 text-slate-400 hover:bg-white/10 hover:text-white'"
      >反馈管理 ({{ feedbackTotal }})</button>
    </div>

    <!-- ─── 报名管理 ─── -->
    <div
      v-if="tab === 'register'"
      class="glass-card rounded-xl"
      style="margin-top: var(--sp-5); padding: var(--sp-5)"
    >
      <!-- 筛选器:窄屏两列平分,不再挤成一行 -->
      <div class="flex flex-wrap items-center gap-f3">
        <select
          v-model="registerFilter.businessLine"
          @change="registerPage = 1; loadRegister()"
          class="min-w-0 flex-1 rounded-lg border border-white/10 bg-[#101a2e] text-white outline-none focus:border-accent/60 sm:flex-none"
          style="padding: var(--sp-2) var(--sp-3); min-height: var(--tap)"
        >
          <option value="">全部业务线</option>
          <option v-for="(name, slug) in BUSINESS_MAP" :key="slug" :value="slug">{{ name }}</option>
        </select>
        <select
          v-model="registerFilter.status"
          @change="registerPage = 1; loadRegister()"
          class="min-w-0 flex-1 rounded-lg border border-white/10 bg-[#101a2e] text-white outline-none focus:border-accent/60 sm:flex-none"
          style="padding: var(--sp-2) var(--sp-3); min-height: var(--tap)"
        >
          <option value="">全部状态</option>
          <option v-for="(label, st) in REGISTER_STATUS_MAP" :key="st" :value="Number(st)">{{ label }}</option>
        </select>
        <span
          class="w-full text-slate-400 sm:ml-auto sm:w-auto"
          style="font-size: var(--fs-sm)"
        >共 {{ registerTotal }} 条</span>
        <button
          @click="doExportRegister"
          :disabled="exporting || registerTotal === 0"
          class="tap-target flex shrink-0 items-center whitespace-nowrap rounded-full border border-white/15 bg-white/5 text-slate-300 transition hover:bg-white/10 hover:text-white disabled:cursor-not-allowed disabled:opacity-40"
          style="padding: var(--sp-2) var(--sp-4); font-size: var(--fs-sm)"
        >Export</button>
      </div>

      <p
        v-if="registerError"
        class="text-danger"
        style="font-size: var(--fs-sm); margin-top: var(--sp-3)"
      >{{ registerError }}</p>

      <!-- ── 桌面(≥lg):表格 ── -->
      <div class="hidden lg:block" style="margin-top: var(--sp-4)">
        <table class="w-full text-left" style="font-size: var(--fs-sm)">
          <thead>
            <tr
              class="border-b border-white/10 uppercase tracking-wider text-slate-500"
              style="font-size: var(--fs-xs)"
            >
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
                <span
                  class="whitespace-nowrap rounded-full border border-white/10 bg-white/5 text-slate-300"
                  style="padding: 2px var(--sp-2); font-size: var(--fs-xs)"
                >
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
                <span
                  class="whitespace-nowrap rounded-full border"
                  style="padding: 2px var(--sp-2); font-size: var(--fs-xs)"
                  :class="STATUS_BADGE[row.status] || 'border-white/10 text-slate-400'"
                >
                  {{ REGISTER_STATUS_MAP[row.status] || row.status }}
                </span>
              </td>
              <td class="py-3 pr-3 text-slate-500" style="font-size: var(--fs-xs)">
                {{ fmtTime(row.createTime) }}
              </td>
              <td class="py-3">
                <div class="flex flex-wrap gap-1">
                  <button
                    v-for="(label, st) in REGISTER_STATUS_MAP"
                    :key="st"
                    @click="changeRegisterStatus(row, Number(st))"
                    :disabled="row.status === Number(st)"
                    class="whitespace-nowrap rounded-md border border-white/10 bg-white/5 text-slate-300 transition hover:bg-white/15 hover:text-white disabled:cursor-default disabled:opacity-40"
                    style="padding: 2px var(--sp-2); font-size: var(--fs-xs)"
                  >{{ label }}</button>
                </div>
              </td>
            </tr>
          </tbody>
        </table>
      </div>

      <!-- ── 窄屏(<lg):卡片式,替代 760px 横向滚动表格 ── -->
      <div class="grid gap-f3 lg:hidden" style="margin-top: var(--sp-4)">
        <div
          v-for="row in registers"
          :key="row.id"
          class="rounded-lg border border-white/5 bg-white/[0.03]"
          style="padding: var(--sp-4)"
        >
          <!-- 标题行:姓名 + 业务线/状态徽章 -->
          <div class="flex flex-wrap items-center gap-2">
            <span class="font-medium text-white" style="font-size: var(--fs-base)">
              {{ row.name }}
            </span>
            <span
              class="whitespace-nowrap rounded-full border border-white/10 bg-white/5 text-slate-300"
              style="padding: 2px var(--sp-2); font-size: var(--fs-xs)"
            >
              {{ BUSINESS_MAP[row.businessLine] || row.businessLine }}
            </span>
            <span
              class="whitespace-nowrap rounded-full border"
              style="padding: 2px var(--sp-2); font-size: var(--fs-xs)"
              :class="STATUS_BADGE[row.status] || 'border-white/10 text-slate-400'"
            >
              {{ REGISTER_STATUS_MAP[row.status] || row.status }}
            </span>
          </div>

          <!-- 字段区:标签 + 值,窄屏纵向,长内容自动换行 -->
          <dl
            class="grid gap-x-f4 gap-y-1 sm:grid-cols-2"
            style="font-size: var(--fs-sm); margin-top: var(--sp-3)"
          >
            <div class="flex min-w-0 gap-2">
              <dt class="shrink-0 text-slate-500">学号</dt>
              <dd class="min-w-0 break-words text-slate-300">{{ row.studentId || '—' }}</dd>
            </div>
            <div class="flex min-w-0 gap-2">
              <dt class="shrink-0 text-slate-500">学院</dt>
              <dd class="min-w-0 break-words text-slate-300">
                {{ row.college || '—' }}<span v-if="row.major"> / {{ row.major }}</span>
              </dd>
            </div>
            <div class="flex min-w-0 gap-2">
              <dt class="shrink-0 text-slate-500">联系</dt>
              <dd class="min-w-0 break-words text-slate-300">
                <span v-if="row.phone">{{ row.phone }}</span>
                <span v-if="row.wechat" class="text-slate-500"> ({{ row.wechat }})</span>
                <span v-if="!row.phone && !row.wechat">—</span>
              </dd>
            </div>
            <div class="flex min-w-0 gap-2">
              <dt class="shrink-0 text-slate-500">提交</dt>
              <dd class="min-w-0 text-slate-400">{{ fmtTime(row.createTime) }}</dd>
            </div>
          </dl>

          <!-- 操作:触摸目标放大到 44px -->
          <div
            class="flex flex-wrap gap-2 border-t border-white/5"
            style="margin-top: var(--sp-3); padding-top: var(--sp-3)"
          >
            <button
              v-for="(label, st) in REGISTER_STATUS_MAP"
              :key="st"
              @click="changeRegisterStatus(row, Number(st))"
              :disabled="row.status === Number(st)"
              class="tap-target flex flex-1 items-center justify-center whitespace-nowrap rounded-md border border-white/10 bg-white/5 text-slate-300 transition hover:bg-white/15 hover:text-white disabled:cursor-default disabled:opacity-40"
              style="padding: var(--sp-2) var(--sp-3); font-size: var(--fs-xs)"
            >{{ label }}</button>
          </div>
        </div>
      </div>

      <!-- 空态 / 加载态(两种布局共用) -->
      <p
        v-if="!registerLoading && registers.length === 0"
        class="text-center text-slate-500"
        style="font-size: var(--fs-sm); padding: var(--sp-10) 0"
      >暂无报名数据</p>
      <p
        v-if="registerLoading"
        class="text-center text-slate-400"
        style="font-size: var(--fs-sm); padding: var(--sp-10) 0"
      >加载中…</p>

      <div
        v-if="registerTotalPages > 1"
        class="flex items-center justify-end gap-f3"
        style="margin-top: var(--sp-4); font-size: var(--fs-sm)"
      >
        <button
          :disabled="registerPage <= 1"
          @click="registerPage--; loadRegister()"
          class="tap-target flex items-center rounded-lg border border-white/10 bg-white/5 text-slate-300 transition hover:bg-white/10 disabled:opacity-40"
          style="padding: var(--sp-2) var(--sp-3)"
        >← Prev</button>
        <span class="text-slate-400">{{ registerPage }} / {{ registerTotalPages }}</span>
        <button
          :disabled="registerPage >= registerTotalPages"
          @click="registerPage++; loadRegister()"
          class="tap-target flex items-center rounded-lg border border-white/10 bg-white/5 text-slate-300 transition hover:bg-white/10 disabled:opacity-40"
          style="padding: var(--sp-2) var(--sp-3)"
        >Next →</button>
      </div>
    </div>

    <!-- ─── 反馈管理 ─── -->
    <div
      v-else
      class="glass-card rounded-xl"
      style="margin-top: var(--sp-5); padding: var(--sp-5)"
    >
      <div class="flex flex-wrap items-center gap-f3">
        <select
          v-model="feedbackFilter.status"
          @change="feedbackPage = 1; loadFeedback()"
          class="min-w-0 flex-1 rounded-lg border border-white/10 bg-[#101a2e] text-white outline-none focus:border-accent/60 sm:flex-none"
          style="padding: var(--sp-2) var(--sp-3); min-height: var(--tap)"
        >
          <option value="">全部状态</option>
          <option value="0">未处理</option>
          <option value="1">已处理</option>
        </select>
        <span
          class="w-full text-slate-400 sm:ml-auto sm:w-auto"
          style="font-size: var(--fs-sm)"
        >共 {{ feedbackTotal }} 条</span>
        <button
          @click="doExportFeedback"
          :disabled="exporting || feedbackTotal === 0"
          class="tap-target flex shrink-0 items-center whitespace-nowrap rounded-full border border-white/15 bg-white/5 text-slate-300 transition hover:bg-white/10 hover:text-white disabled:cursor-not-allowed disabled:opacity-40"
          style="padding: var(--sp-2) var(--sp-4); font-size: var(--fs-sm)"
        >Export</button>
      </div>

      <p
        v-if="feedbackError"
        class="text-danger"
        style="font-size: var(--fs-sm); margin-top: var(--sp-3)"
      >{{ feedbackError }}</p>

      <div class="grid gap-f3" style="margin-top: var(--sp-4)">
        <!-- 窄屏纵向堆叠(按钮占满宽),sm 起左右分栏 -->
        <div
          v-for="row in feedbacks"
          :key="row.id"
          class="flex flex-col gap-f3 rounded-lg border border-white/5 bg-white/[0.03] sm:flex-row sm:items-start sm:justify-between"
          style="padding: var(--sp-4)"
          :class="{ 'opacity-60': row.status === 1 }"
        >
          <div class="min-w-0 flex-1">
            <div
              class="flex flex-wrap items-center gap-2"
              style="font-size: var(--fs-xs)"
            >
              <span class="font-medium text-white">{{ row.name }}</span>
              <span
                class="min-w-0 break-all rounded-full border border-white/10 bg-white/5 text-slate-400"
                style="padding: 2px var(--sp-2)"
              >来源:{{ row.page }}</span>
              <span
                class="whitespace-nowrap rounded-full border"
                style="padding: 2px var(--sp-2)"
                :class="row.status === 1 ? 'border-emerald-400/30 bg-emerald-500/15 text-emerald-300' : 'border-amber-400/30 bg-amber-500/15 text-amber-300'"
              >{{ row.status === 1 ? '已处理' : '未处理' }}</span>
              <span class="whitespace-nowrap text-slate-500">{{ fmtTime(row.createTime) }}</span>
            </div>
            <p
              class="break-words leading-relaxed text-slate-300"
              style="font-size: var(--fs-sm); margin-top: var(--sp-2)"
            >{{ row.content }}</p>
          </div>
          <button
            @click="toggleFeedbackRead(row)"
            class="tap-target flex w-full shrink-0 items-center justify-center whitespace-nowrap rounded-full border border-white/15 bg-white/5 text-slate-300 transition hover:bg-white/15 hover:text-white sm:w-auto"
            style="padding: var(--sp-2) var(--sp-4); font-size: var(--fs-xs)"
          >{{ row.status === 1 ? '标记未处理' : '标记已处理' }}</button>
        </div>
        <p
          v-if="!feedbackLoading && feedbacks.length === 0"
          class="text-center text-slate-500"
          style="font-size: var(--fs-sm); padding: var(--sp-10) 0"
        >
          暂无反馈数据
        </p>
        <p
          v-if="feedbackLoading"
          class="text-center text-slate-400"
          style="font-size: var(--fs-sm); padding: var(--sp-10) 0"
        >加载中…</p>
      </div>

      <div
        v-if="feedbackTotalPages > 1"
        class="flex items-center justify-end gap-f3"
        style="margin-top: var(--sp-4); font-size: var(--fs-sm)"
      >
        <button
          :disabled="feedbackPage <= 1"
          @click="feedbackPage--; loadFeedback()"
          class="tap-target flex items-center rounded-lg border border-white/10 bg-white/5 text-slate-300 transition hover:bg-white/10 disabled:opacity-40"
          style="padding: var(--sp-2) var(--sp-3)"
        >← Prev</button>
        <span class="text-slate-400">{{ feedbackPage }} / {{ feedbackTotalPages }}</span>
        <button
          :disabled="feedbackPage >= feedbackTotalPages"
          @click="feedbackPage++; loadFeedback()"
          class="tap-target flex items-center rounded-lg border border-white/10 bg-white/5 text-slate-300 transition hover:bg-white/10 disabled:opacity-40"
          style="padding: var(--sp-2) var(--sp-3)"
        >Next →</button>
      </div>
    </div>
  </div>
</template>
