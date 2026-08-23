// ─── 站点配置(唯一数据源)────────────────────────────────────────────
// 文案基调:无限进步 · 绝对生产力 · 创造价值;英文为视觉语言,中文承载信息

export interface BusinessLine {
  slug: string;
  name: string;
  icon: string;
  accent: string;
  tagline: string;
  desc: string;
  features: string[];
  path: string;
  registerOpen: boolean;
}

export interface Notice {
  title: string;
  date: string;
  content: string;
}

export const site = {
  brand: 'AI+CLUB',
  brandZh: 'AI+社团',
  sloganEn: 'Infinite Progress · Absolute Productivity',
  sloganZh: '创造价值，经营未来',
  school: '福州大学',
  apiBase: '/yudao-api',
  mockApi: true, // 无后端时设为 true 走模拟数据;部署联调时改为 false
  contact: {
    qqGroup: '待填写',
    wechat: '待填写',
    email: '待填写',
  },
  nav: [
    { label: 'Home', href: '/', match: '^/$' },
    { label: 'AI+IT', href: '/business/ai-it/', match: '^/business/ai-it' },
    { label: 'AI+Biz', href: '/business/ai-biz/', match: '^/business/ai-biz' },
    { label: 'AI+Embed', href: '/business/ai-embed/', match: '^/business/ai-embed' },
    { label: 'Admin', href: '/admin/', match: '^/admin' },
  ],
};

export const businessLines: BusinessLine[] = [
  {
    slug: 'ai-it',
    name: 'AI+IT',
    icon: '💻',
    accent: '#3b82f6',
    tagline: 'From Zero to One',
    desc: 'Java 全栈 + AI 工程化,8 阶段体系,200 小时内完成从入门到实战项目',
    features: ['系统学习规划', '全栈实战项目', 'AI 工程化进阶'],
    path: '/business/ai-it/',
    registerOpen: true,
  },
  {
    slug: 'ai-biz',
    name: 'AI+Biz',
    icon: '📈',
    accent: '#a78bfa',
    tagline: 'AI × Business Innovation',
    desc: '聚焦 AI 在商业、运营、产品场景的应用,培养 AI 原生的业务思维与实践能力',
    features: ['AI 商业案例', '业务流程重构', '产品与运营实战'],
    path: '/business/ai-biz/',
    registerOpen: true,
  },
  {
    slug: 'ai-embed',
    name: 'AI+Embed',
    icon: '🔧',
    accent: '#34d399',
    tagline: 'AI × Embedded Systems',
    desc: '嵌入式开发 + 边缘 AI,让智能走进真实世界,软硬结合完成智能硬件项目',
    features: ['嵌入式基础', '边缘 AI 推理', '智能硬件项目'],
    path: '/business/ai-embed/',
    registerOpen: true,
  },
];

export const notices: Notice[] = [
  {
    title: '2026 Fall Recruitment Open',
    date: '2026-08-19',
    content:
      '三条业务线(AI+IT / AI+业务 / AI+嵌入式)招新通道已开放,点击对应业务线进入报名页。',
  },
];
