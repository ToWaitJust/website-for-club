# 项目说明 · AI+社团门户

> 仓库:本项目(portal) ｜ 技术栈:纯静态 HTML/CSS/JS + 数据文件 ｜ 配套:yudao 后端(独立仓库)

## 项目概述

社团集成式门户,承载三条业务线(**AI+IT / AI+业务 / AI+嵌入式**)的信息展示、报名、反馈与服务入口。

架构为**两段式**:

- **门户(本仓库)**:面向访问者,纯静态站点,由 Nginx 托管。内容与代码分离,导航/公告/业务线卡片全部由 `content/site.json` 数据驱动。
- **运营后台(另建 yudao 工程)**:面向运营,管理公告、报名、反馈数据。门户通过 yudao 公开 API 提交报名与反馈。

## 目录结构

```
portal/
├── index.html                   # 首页:三业务线入口 + 公告
├── business/
│   ├── ai-it/                   # AI+IT 业务线
│   │   ├── index.html           # 业务线主页(渲染 content/business/ai-it.md)
│   │   ├── learning-plan.html   # 学习规划长文档(独立视觉体系)
│   │   └── register.html        # 报名页
│   ├── ai-biz/                  # AI+业务(同构模板)
│   └── ai-embed/                # AI+嵌入式(同构模板)
├── ops/index.html               # 运营方案内部文档
├── assets/
│   ├── css/main.css             # 门户统一样式
│   ├── css/nav.css              # 文档页专用导航样式
│   └── js/
│       ├── app.js               # 导航/数据加载/轻量 Markdown 渲染
│       ├── register.js          # 报名组件 → yudao API
│       └── feedback.js          # 反馈组件 → yudao API
├── content/
│   ├── site.json                # 站点配置:导航/业务线/公告/API 地址
│   └── business/*.md            # 业务线简介内容
├── scripts/dev-server.js        # 本地预览(生产用 Nginx)
└── docs/                        # 架构 / yudao / 部署文档
```

## 数据流

```
访问者 → Nginx(门户静态文件)
   ├── 阅读: HTML + content/*.md + site.json
   └── 提交: register.js / feedback.js
            → POST {apiBase}/admin-api/club/register|feedback
            → Nginx 反代 → yudao 服务 → MySQL

运营 → yudao 管理后台(独立域名/路径 /admin)
   ├── 公告 / 业务线配置 / 报名记录 / 反馈记录
   └── 数据存 MySQL,门户经 API 读取
```

## 新增一条业务线(如 AI+硬件)

1. 门户:复制 `business/ai-it/` 目录为 `business/ai-hardware/`,改页面标题与 `--line-accent` 颜色,新增 `content/business/ai-hardware.md`
2. 数据:在 `content/site.json` 的 `businessLines` 加一条(卡片/导航/报名入口自动出现)
3. 后台:在 yudao 中用代码生成器为 `business_line` 表加一条记录(或直接 SQL 插入)
4. 部署:`nginx -s reload` + 更新静态文件,后台服务无感

## 常见任务

| 任务 | 操作 |
|------|------|
| 改公告 | 编辑 `content/site.json` 的 `notices` 数组(或接入 yudao 公告 API 后改后台) |
| 改业务线简介 | 编辑 `content/business/<slug>.md` |
| 开放/关闭报名 | 改 `site.json` 中 `registerOpen` 字段(后续由后台接口下发) |
| 本地预览 | `npm run dev` → http://localhost:5000 |
| 上线 | 静态文件同步到服务器 Nginx 目录,详见 `docs/DEPLOY.md` |

## 长期约束

- 门户保持零构建、零前端框架,内容数据化。
- 动态数据(报名/反馈)一律走 yudao API,门户不直连数据库。
- 长文档(学习规划/运营方案)保留独立视觉体系,只挂统一导航。
