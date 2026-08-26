// ─── yudao API 封装 ──────────────────────────────────────────────────
// 契约依据: docs/04-API-CONTRACT.md
// yudao 统一响应: { code, data, msg },code === 0 为成功
// 路径约定:统一走 /yudao-api 前缀,【不要再拼 /admin-api】——
//   dev(vite proxy)与生产(nginx)都会自动补上 /admin-api 前缀。
//   ✗ 错误示例:/yudao-api/admin-api/system/auth/login → 后端收到
//     /admin-api/admin-api/... 双前缀 → Sa-Token 401「账号未登录」
//   ✓ 正确示例:/yudao-api/system/auth/login → 后端 /admin-api/system/auth/login

const TENANT_ID = '1';

interface ApiResult<T> {
  code: number;
  data: T;
  msg: string;
}

async function request<T>(
  path: string,
  options: { method?: string; body?: unknown; token?: string } = {}
): Promise<T> {
  const { method = 'GET', body, token } = options;
  const headers: Record<string, string> = { 'tenant-id': TENANT_ID };
  if (body !== undefined) headers['Content-Type'] = 'application/json';
  if (token) headers['Authorization'] = `Bearer ${token}`;
  let res: Response;
  try {
    res = await fetch(path, {
      method,
      headers,
      body: body !== undefined ? JSON.stringify(body) : undefined,
    });
  } catch {
    throw new Error('网络错误,请稍后再试');
  }
  const result = (await res.json().catch(() => null)) as ApiResult<T> | null;
  if (result && result.code === 0) {
    return result.data;
  }
  // 登录态失效:清除本地 token
  if (result && result.code === 401) {
    clearToken();
  }
  throw new Error((result && result.msg) || '请求失败,请稍后再试');
}

// ─── 公开接口(门户表单)───────────────────────────────────────────────
export async function post<T>(path: string, body: unknown): Promise<T> {
  return request<T>(path, { method: 'POST', body });
}

// ─── token 管理(浏览器 localStorage)──────────────────────────────────
const TOKEN_KEY = 'club_admin_token';
export function getToken(): string | null {
  try {
    return typeof localStorage !== 'undefined' ? localStorage.getItem(TOKEN_KEY) : null;
  } catch {
    return null;
  }
}
export function setToken(token: string) {
  try {
    localStorage.setItem(TOKEN_KEY, token);
  } catch {
    /* ignore */
  }
}
export function clearToken() {
  try {
    localStorage.removeItem(TOKEN_KEY);
  } catch {
    /* ignore */
  }
}

// ─── 登录接口(yudao system 模块)─────────────────────────────────────
export interface LoginResult {
  userId: number;
  accessToken: string;
  refreshToken: string;
  expiresTime: number;
}

export async function login(username: string, password: string): Promise<LoginResult> {
  return request<LoginResult>('/yudao-api/system/auth/login', {
    method: 'POST',
    body: { username, password },
  });
}

// ─── 管理端接口(需登录,自动携带 token)───────────────────────────────

export interface PageResult<T> {
  list: T[];
  total: number;
}

export interface RegisterRecord {
  id: number;
  businessLine: string;
  name: string;
  studentId: string;
  college: string;
  major: string;
  phone: string;
  wechat: string;
  motivation: string;
  status: number;
  createTime: number;
}

/** 报名状态: 0=待处理 1=已联系 2=已录取 3=未录取 */
export const REGISTER_STATUS_MAP: Record<number, string> = {
  0: '待处理',
  1: '已联系',
  2: '已录取',
  3: '未录取',
};

export async function getRegisterPage(params: {
  pageNo?: number;
  pageSize?: number;
  businessLine?: string;
  status?: number | '';
}): Promise<PageResult<RegisterRecord>> {
  const token = getToken();
  if (!token) throw new Error('未登录');
  const qs = new URLSearchParams();
  qs.set('pageNo', String(params.pageNo || 1));
  qs.set('pageSize', String(params.pageSize || 20));
  if (params.businessLine) qs.set('businessLine', params.businessLine);
  if (params.status !== undefined && params.status !== null && params.status !== '') {
    qs.set('status', String(params.status));
  }
  return request<PageResult<RegisterRecord>>(`/yudao-api/club/register/page?${qs}`, {
    token,
  });
}

export async function updateRegisterStatus(id: number, status: number): Promise<boolean> {
  const token = getToken();
  if (!token) throw new Error('未登录');
  return request<boolean>('/yudao-api/club/register/update-status', {
    method: 'PUT',
    body: { id, status },
    token,
  });
}

export interface FeedbackRecord {
  id: number;
  page: string;
  name: string;
  content: string;
  status: number;
  createTime: number;
}

export async function getFeedbackPage(params: {
  pageNo?: number;
  pageSize?: number;
  status?: number | '';
}): Promise<PageResult<FeedbackRecord>> {
  const token = getToken();
  if (!token) throw new Error('未登录');
  const qs = new URLSearchParams();
  qs.set('pageNo', String(params.pageNo || 1));
  qs.set('pageSize', String(params.pageSize || 20));
  if (params.status !== undefined && params.status !== null && params.status !== '') {
    qs.set('status', String(params.status));
  }
  return request<PageResult<FeedbackRecord>>(`/yudao-api/club/feedback/page?${qs}`, {
    token,
  });
}

export async function updateFeedbackStatus(id: number, status: number): Promise<boolean> {
  const token = getToken();
  if (!token) throw new Error('未登录');
  return request<boolean>('/yudao-api/club/feedback/update-status', {
    method: 'PUT',
    body: { id, status },
    token,
  });
}
