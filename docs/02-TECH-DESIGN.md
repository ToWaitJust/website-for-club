# 02 · 技术设计文档

> 版本:v1.0 ｜ 状态:评审稿 ｜ 日期:2026-08-19
> 配套:01-PRD、03-设计规范、04-接口契约、05-实施计划

---

## 1. 总体架构(两段式)

```
                        ┌──────────────────── 服务器 (2C4G) ───────────────┐
访问者/运营 ──HTTPS──▶ Nginx                                            │
                        ├── /              → Astro 构建的静态门户(dist/)   │
                        ├── /yudao-api/*   → 反代 yudao-server:48080      │
                        ├── /admin/*       → yudao-ui-admin 构建产物      │
                        └── /healthz       → 探针                        │
                        yudao-server(JVM) ──▶ MySQL 8 / Redis 7          │
                        └──────────────────────────────────────────────────┘
```

**决策记录**:

| 决策点 | 结论 | 理由 |
|--------|------|------|
| 门户技术栈 | Astro + Vue islands + Tailwind | 内容站性能顶级;Vue 技能与 yudao-ui-admin 复用;静态输出部署不变 |
| 门户形态 | 全静态构建(SSG) | 内容几乎不变,静态最快;无 SSR 需求 |
| 后端 | yudao-boot 单体版(裁剪) | 团队学习路线核心;自带权限/代码生成/导出 |
| 数据库 | MySQL 8(后台)+ 无门户侧 DB | 门户不直连数据库,一切经 API |
| 部署 | Nginx + systemd 单机 | 2C4G 足够,复杂度最低 |

---

## 2. Astro 门户设计

### 2.1 工程结构

```
portal/                          # Astro 工程(本仓库重构目标)
├── src/
│   ├── pages/
│   │   ├── index.astro          # 首页
│   │   ├── 404.astro
│   │   └── business/
│   │       ├── [slug]/          # 动态路由:业务线主页(slug=ai-it|ai-biz|ai-embed)
│   │       │   ├── index.astro
│   │       │   ├── learning-plan.astro   # 仅 ai-it 挂载(Astro 可条件导出)
│   │       │   └── register.astro
│   ├── layouts/
│   │   ├── BaseLayout.astro     # 全局壳:导航/页脚/字体/SEO
│   │   └── DocLayout.astro      # 长文档布局(学习规划/运营方案)
│   ├── components/
│   │   ├── Nav.astro
│   │   ├── Footer.astro
│   │   ├── Hero.astro           # 全屏 Hero + 粒子/网格背景
│   │   ├── BizCard.astro        # 业务线玻璃卡片(3D tilt)
│   │   ├── NoticeList.astro
│   │   ├── RegisterForm.vue     # 报名表单(Vue 岛)
│   │   ├── FeedbackBox.vue      # 反馈组件(Vue 岛)
│   │   └── Mermaid.astro        # Mermaid 流程图封装
│   ├── content/
│   │   ├── business/            # Content Collection:业务线
│   │   │   ├── ai-it.md
│   │   │   ├── ai-biz.md
│   │   │   └── ai-embed.md
│   │   └── docs/                # Content Collection:长文档
│   │       ├── learning-plan.md # 学习规划(原 HTML 转 md,保留 mermaid 代码块)
│   │       └── operation.md     # 运营方案
│   ├── data/
│   │   └── site.ts              # 站点配置(导航/公告/业务线元信息/API 地址)
│   ├── styles/
│   │   └── global.css           # Tailwind + 设计 token
│   └── lib/
│       ├── api.ts               # yudao API 封装(报名/反馈/二期公告)
│       └── mermaid.ts           # Mermaid 渲染初始化
├── public/                      # 静态资源(logo/图片)
├── astro.config.mjs
├── tailwind.config.mjs
└── package.json
```

### 2.2 关键机制

| 机制 | 实现 | 说明 |
|------|------|------|
| 内容管理 | Astro Content Collections | `business/*.md` 带 frontmatter(slug/name/accent/registerOpen);类型安全,构建期校验 |
| 动态路由 | `[slug].astro` + `getStaticPaths()` | 新增业务线 = 加一个 md 文件,页面自动生成 |
| 交互岛 | Vue 组件 + `client:load` | 只有报名/反馈/动效是 JS,其余页面零 JS |
| 数据源 | `src/data/site.ts` | 导航/公告/apiBase 集中配置;二期可改为启动时拉 yudao API |
| 样式 | Tailwind + CSS 变量 | 三业务线品牌色通过 `data-accent` 属性切换 |
| 图表 | Mermaid(CDN/本地化) | 长文档流程图;阅读页缩放组件保留 |

### 2.3 与现有静态门户的映射

| 现状(静态) | 迁移到 |
|-----------|--------|
| `index.html` | `src/pages/index.astro` |
| `business/ai-it/index.html` 等 | `src/pages/business/[slug]/index.astro`(content 驱动) |
| `business/ai-it/register.html` 等 | `src/pages/business/[slug]/register.astro` |
| `business/ai-it/learning-plan.html` | `src/content/docs/learning-plan.md` + DocLayout |
| `ops/index.html` | `src/content/docs/operation.md` + DocLayout(路径 /docs/operation/) |
| `assets/js/register.js` | `src/components/RegisterForm.vue` |
| `assets/js/feedback.js` | `src/components/FeedbackBox.vue` |
| `content/site.json` | `src/data/site.ts` |
| `assets/css/main.css` | Tailwind + `global.css` |
| `scripts/dev-server.js` | `astro dev` / `astro preview` |

### 2.4 构建与运行

```bash
npm create astro@latest portal -- --template minimal --typescript strict
cd portal
npm install astro @astrojs/vue @astrojs/tailwind @astrojs/mdx vue tailwindcss
npm run build          # 产出 dist/ → Nginx root
npm run dev            # 本地开发
```

---

## 3. yudao 后端设计

### 3.1 裁剪方案(四层)

| 层 | 操作 | 具体 |
|----|------|------|
| 模块 | `yudao-server/pom.xml` 只保留 system + infra + club | 剔除 pay/mall/crm/erp/workflow/mp/bpm 等依赖 |
| 功能 | 生产环境关闭 codegen、swagger | `application-prod.yaml`: `yudao.codegen.enable=false`、springdoc 关闭 |
| 中间件 | 关闭 MQ / XXL-JOB(不用定时任务) | yaml 注释对应 starter;Redis 保留(登录会话强依赖) |
| 界面 | 菜单管理删除无用菜单 | 数据库驱动,后台直接删 |

**裁切红线**:不动 yudao 核心源码;`system` + `infra` 视为基础设施保留;裁切终点是"最小可用",不再深挖。

### 3.2 club 模块

包结构(代码生成器产出):

```
yudao-module-club
├── controller/admin/club/      # BusinessLine / Notice / Register / Feedback 四个 Controller
├── dal/dataobject/club/        # 4 个 DO
├── dal/mysql/club/             # 4 个 Mapper
├── service/club/               # Service + 实现
├── controller/admin/club/vo/   # VO(ReqVO/RespVO)
└── api/club/                   # 供门户调用的公开 API(二期可拆)
```

表结构见 `04-API-CONTRACT.md`(字段级契约)。

### 3.3 公开接口鉴权

- 报名/反馈:Controller 方法加 `@PermitAll`;SaToken 配置排除这两个路径(参考 yudao 登录接口的放行方式)
- 限流:`@RateLimiter`(如单 IP 每 10 秒 5 次),防脚本刷报名

---

## 4. 部署架构

```
Nginx(443)
 ├── root: /var/www/portal        ← astro build 产物
 ├── /yudao-api/ → 127.0.0.1:48080/admin-api/(rewrite 去前缀)
 ├── /admin/ → /var/www/admin     ← yudao-ui-admin 构建产物
 └── /healthz → 200

systemd: yudao.service(JVM -Xmx768m, Restart=always)
MySQL 8(127.0.0.1) / Redis 7(127.0.0.1, 无密码仅本机)
备份:mysqldump + portal 目录 tar,每日 cron,保留 14 天
```

详见 `docs/DEPLOY.md`(已含完整 nginx 配置与 systemd 单元)。

---

## 5. 演进路线(二期,不阻塞一期)

1. **公告动态化**:`src/data/site.ts` 的公告改为构建时(或前端启动时)拉 `GET /admin-api/club/notice/list`;运营后台改公告即时生效
2. **业务线配置动态化**:`GET /admin-api/club/business-line/list`,后台可编辑简介 md
3. **SEO**:Astro 原生支持 meta/OG;补 sitemap.xml
4. **更多业务线**:content 加 md + 后台加记录,两端零代码

---

## 6. 风险与对策

| 风险 | 对策 |
|------|------|
| Astro 学习成本(技术组) | 语法平缓;文档 03 提供组件规范;先做首页原型再铺开 |
| yudao 裁剪后启动报错 | 裁切按"删依赖→编译→启动"小步走;保留 system/infra 不动 |
| 长文档 HTML→md 丢失交互 | 学习规划/运营方案保留 Mermaid 代码块 + 缩放组件;富交互 SVG 若无法 md 化,以 iframe 嵌原 HTML 兜底 |
| 报名数据被刷 | @RateLimiter + 后台校验 + 字段长度限制 |
