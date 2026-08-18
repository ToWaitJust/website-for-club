## 项目概述

Infinite Productivity — 团队效率与 AI+IT 学习运营平台。纯静态多页面站点，通过自定义 Node.js HTTP 服务器提供文件服务与 URL 重写。

## 技术栈

- **前端**：原生 HTML5 + CSS3 + Vanilla JS（无框架、无构建步骤）
- **服务端**：Node.js 自定义 HTTP 静态文件服务器（`server.js`）
- **包管理器**：pnpm
- **运行时**：Node.js 24

## 目录结构

```
/workspace/projects/
├── .coze                  # 项目配置（平台入口）
├── .preview               # 预览端口声明（expose_port=5000）
├── server.js              # Node.js 静态文件服务器（含 URL 重写）
├── index.html             # 首页 — Infinite Productivity 主入口
├── ai-learning.html       # 团队 AI+IT 学习规划页
├── operation.html         # 运营方案页（孵化营 & AI+IT 社团）
├── feedback.js            # 反馈信箱组件（共享表单）
├── styles/
│   └── main.css           # 首页公共样式
├── scripts/
│   ├── build.sh           # dev 构建（安装依赖）
│   ├── run.sh             # dev 启动（从 .preview 读端口）
│   ├── deploy_build.sh    # deploy 构建
│   └── deploy_run.sh      # deploy 启动
├── package.json
└── pnpm-lock.yaml
```

## 关键入口 / 核心模块

- **server.js**：HTTP 服务器，监听 `0.0.0.0`，端口从 `DEPLOY_RUN_PORT` 环境变量读取（fallback 5000）；支持 URL 重写（`/ai-learning` → `ai-learning.html`，`/operation` → `operation.html`）；MIME 类型映射完整；提供 `POST /api/feedback` 接口，接收 `{name, content, page}` 并写入数据库。
- **index.html**：SPA 风格的首页，含 Landing 视图和主应用视图切换。
- **ai-learning.html**：独立的团队 AI+IT 学习规划展示页。
- **feedback.js**：反馈信箱共享组件，在每个页面底部渲染表单并提交到 `/api/feedback`。
- **数据库 `feedback` 表**：存储反馈数据，包含 `name`、`content`、`page`（来源页面）、`created_at`。

## 运行与预览

- **预览**：`preview_enable = "enabled"`，通过 `scripts/build.sh` + `scripts/run.sh` 启动。
- **端口**：从 `.preview` 的 `expose_port` 读取，通过 `DEPLOY_RUN_PORT` 环境变量传递给 `server.js`。
- **部署**：`deploy.profile.kind = "service"`，`deploy.profile.flavor = "web"`，通过 `scripts/deploy_build.sh` + `scripts/deploy_run.sh` 启动。

## 用户偏好与长期约束

- 纯原生 HTML/CSS/JS，不引入前端框架。
- 各页面样式以内联 `<style>` 为主，公共样式在 `styles/main.css`。

## 常见问题和预防

- `server.js` 端口由环境变量 `DEPLOY_RUN_PORT` 控制，dev 脚本通过 `.preview` 设置该变量；部署环境由平台注入。
- URL 重写规则在 `server.js` 的 `REWRITES` 对象中维护，新增页面需同步添加。
- 静态站点无 SPA fallback，缺失文件返回 404。
- 反馈数据存储在 Coze 内置数据库（PostgreSQL）的 `feedback` 表中，字段：`id`、`name`、`content`、`page`、`created_at`。
- 数据库连接：开发环境通过 `coze-coding-dev-sdk` 的 `getDb()` 获取 Drizzle 客户端；部署环境通过 `COZE_SUPABASE_URL` 环境变量使用 Supabase SDK。
