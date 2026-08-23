// ─── Mock 数据(无后端时使用)──────────────────────────────────────────
// 当 site.mockApi === true 时,api.ts 的 request 函数会走这里,
// 不发实际 HTTP 请求,直接返回模拟数据。
// 部署联调时将 site.mockApi 改为 false 即可切换到真实接口。

const MOCK_DELAY = 600;

interface MockRegister {
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

interface MockFeedback {
  id: number;
  page: string;
  name: string;
  content: string;
  status: number;
  createTime: number;
}

const mockRegisters: MockRegister[] = [
  { id: 1, businessLine: 'ai-it', name: '张三', studentId: '20250001', college: '计算机与大数据学院', major: '软件工程', phone: '13800000001', wechat: 'zhangsan', motivation: '想学全栈和 AI 工程化', status: 0, createTime: Date.now() - 86400000 * 3 },
  { id: 2, businessLine: 'ai-biz', name: '李四', studentId: '20250002', college: '经济与管理学院', major: '工商管理', phone: '13800000002', wechat: 'lisi', motivation: '对 AI 商业应用感兴趣', status: 1, createTime: Date.now() - 86400000 * 2 },
  { id: 3, businessLine: 'ai-embed', name: '王五', studentId: '20250003', college: '物理与信息工程学院', major: '电子信息', phone: '13800000003', wechat: '', motivation: '想做智能硬件', status: 2, createTime: Date.now() - 86400000 },
  { id: 4, businessLine: 'ai-it', name: '赵六', studentId: '20250004', college: '数学与计算机学院', major: '计算机科学与技术', phone: '13800000004', wechat: 'zhaoliu', motivation: '', status: 3, createTime: Date.now() - 3600000 * 6 },
  { id: 5, businessLine: 'ai-biz', name: '钱七', studentId: '20250005', college: '经济与管理学院', major: '市场营销', phone: '13800000005', wechat: 'qianqi', motivation: '希望学习 AI 运营', status: 0, createTime: Date.now() - 3600000 * 2 },
];

const mockFeedbacks: MockFeedback[] = [
  { id: 1, page: 'home', name: '同学A', content: '网站做得很好看,希望能多一些线下活动。', status: 0, createTime: Date.now() - 86400000 },
  { id: 2, page: 'ai-it', name: '同学B', content: '学习规划很详细,请问有配套的视频教程吗?', status: 1, createTime: Date.now() - 86400000 * 2 },
  { id: 3, page: 'ai-biz', name: '同学C', content: '建议增加一些实际商业案例分析的内容,理论部分可以精简一些。', status: 0, createTime: Date.now() - 3600000 * 5 },
];

function parseQuery(path: string): URLSearchParams {
  const idx = path.indexOf('?');
  return new URLSearchParams(idx >= 0 ? path.slice(idx + 1) : '');
}

function paginate<T>(list: T[], qs: URLSearchParams): { list: T[]; total: number } {
  const pageNo = Number(qs.get('pageNo') || 1);
  const pageSize = Number(qs.get('pageSize') || 20);
  const start = (pageNo - 1) * pageSize;
  return { list: list.slice(start, start + pageSize), total: list.length };
}

export async function mockRequest<T>(
  path: string,
  options: { method?: string; body?: unknown } = {}
): Promise<T> {
  await new Promise((r) => setTimeout(r, MOCK_DELAY));
  const method = options.method || 'GET';
  const cleanPath = path.replace(/^\/yudao-api/, '');

  // 登录
  if (cleanPath.includes('/system/auth/login') && method === 'POST') {
    return {
      userId: 1,
      accessToken: 'mock-token-' + Date.now(),
      refreshToken: 'mock-refresh',
      expiresTime: Date.now() + 7200000,
    } as T;
  }

  // 报名提交
  if (
    cleanPath.includes('/club/register') &&
    !cleanPath.includes('page') &&
    !cleanPath.includes('update-status') &&
    method === 'POST'
  ) {
    const body = options.body as Record<string, string>;
    const id = Math.max(0, ...mockRegisters.map((r) => r.id)) + 1;
    mockRegisters.unshift({
      id,
      businessLine: body?.businessLine || 'ai-it',
      name: body?.name || '',
      studentId: body?.studentId || '',
      college: body?.college || '',
      major: body?.major || '',
      phone: body?.phone || '',
      wechat: body?.wechat || '',
      motivation: body?.motivation || '',
      status: 0,
      createTime: Date.now(),
    });
    return { id } as T;
  }

  // 报名分页
  if (cleanPath.includes('/club/register/page') && method === 'GET') {
    const qs = parseQuery(cleanPath);
    let list = [...mockRegisters];
    const bl = qs.get('businessLine');
    const st = qs.get('status');
    if (bl) list = list.filter((r) => r.businessLine === bl);
    if (st !== null && st !== '') list = list.filter((r) => r.status === Number(st));
    return paginate(list, qs) as T;
  }

  // 报名状态更新
  if (cleanPath.includes('/club/register/update-status') && method === 'PUT') {
    const body = options.body as { id: number; status: number };
    const rec = mockRegisters.find((r) => r.id === body?.id);
    if (rec) rec.status = body.status;
    return true as T;
  }

  // 反馈提交
  if (
    cleanPath.includes('/club/feedback') &&
    !cleanPath.includes('page') &&
    !cleanPath.includes('update-status') &&
    method === 'POST'
  ) {
    const body = options.body as Record<string, string>;
    const id = Math.max(0, ...mockFeedbacks.map((f) => f.id)) + 1;
    mockFeedbacks.unshift({
      id,
      page: body?.page || '',
      name: body?.name || '',
      content: body?.content || '',
      status: 0,
      createTime: Date.now(),
    });
    return null as T;
  }

  // 反馈分页
  if (cleanPath.includes('/club/feedback/page') && method === 'GET') {
    const qs = parseQuery(cleanPath);
    let list = [...mockFeedbacks];
    const st = qs.get('status');
    if (st !== null && st !== '') list = list.filter((f) => f.status === Number(st));
    return paginate(list, qs) as T;
  }

  // 反馈状态更新
  if (cleanPath.includes('/club/feedback/update-status') && method === 'PUT') {
    const body = options.body as { id: number; status: number };
    const rec = mockFeedbacks.find((f) => f.id === body?.id);
    if (rec) rec.status = body.status;
    return true as T;
  }

  return null as T;
}
