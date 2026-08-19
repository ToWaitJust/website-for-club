# 项目说明 · AI+社团门户

> 仓库:本项目(portal) ｜ 技术栈:纯静态 HTML/CSS/JS + 数据文件 ｜ 配套:yudao 后端(独立仓库)

## 项目概述

社团集成式门户,承载三条业务线(**AI+IT / AI+业务 / AI+嵌入式**)的信息展示、报名、反馈与服务入口。

架构为**两段式**:

- **门户(本仓库)**:面向访问者,纯静态站点,由 Nginx 托管。内容与代码分离,导航/公告/业务线卡片全部由 `content/site.json` 数据驱动。
- **运营后台(另建 yudao 工程)**:面向运营,管理公告、报名、反馈数据。门户通过 yudao 公开 API 提交报名与反馈。

## 目录结构

```
portal/                          # 目标:重构为 Astro 工程(见 docs/02-TECH-DESIGN.md)
├── index.html                   # 首页(当前静态版,待迁移)
├── business/                    # 三业务线(当前静态版,待迁移)
├── ops/index.html               # 运营方案(待迁移)
├── assets/                      # 样式与组件(待迁移)
├── content/                     # 数据文件(site.json + 业务线 md,迁移后进 src/content)
├── scripts/dev-server.js        # 本地预览(迁移后由 astro dev 替代)
└── docs/                        # 工程文档体系(唯一事实来源)
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
| 了解需求全貌 | 读 `docs/01-PRD.md` |
| 了解技术方案 | 读 `docs/02-TECH-DESIGN.md` |
| 前端视觉规范 | 读 `docs/03-DESIGN-SPEC.md` |
| 前后端接口对齐 | 读 `docs/04-API-CONTRACT.md` |
| 阶段与验收 | 读 `docs/05-ROADMAP.md` |
| 部署上线 | 读 `docs/DEPLOY.md` |
| yudao 实操 | 读 `docs/YUDAO_BACKEND.md` |

## 长期约束

- **门户目标栈**:Astro + Vue islands + Tailwind,静态构建输出(见 02/03)。
- 动态数据(报名/反馈)一律走 yudao API,门户不直连数据库。
- 长文档(学习规划/运营方案)以 Markdown + Mermaid 形式维护。
- 新增业务线 = content 加 md + site.ts 加配置 + 后台加记录,核心代码零改动。
- 当前仓库仍是迁移前的静态版本,迁移映射关系见 `docs/02-TECH-DESIGN.md §2.3`。
