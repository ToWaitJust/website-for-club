# AI+CLUB 社团门户

> 福州大学 AI+社团 集成式门户网站

三条业务线（**AI+IT / AI+业务 / AI+嵌入式**）的信息展示、报名、反馈与服务入口，配套 Yudao 运营管理后台。

## 技术栈

| 层 | 技术 |
|----|------|
| 前端框架 | [Astro](https://astro.build) 5 + [Vue](https://vuejs.org) 3 (Islands) + [Tailwind CSS](https://tailwindcss.com) 3 |
| 后端 | Spring Boot 3 (Yudao 框架) |
| 数据库 | MySQL 8.0 |
| 缓存 | Redis 5 |
| 构建 | npm / Maven |

## 目录结构

```
projects/                    # 门户前端 (Astro 工程)
├── src/
│   ├── pages/               # 路由页面
│   ├── components/          # 组件 (Nav / Hero / BizCard / AdminLogin …)
│   ├── layouts/             # 布局壳
│   ├── lib/                 # 工具函数 (API 客户端)
│   ├── data/site.ts         # 站点配置 (唯一数据源)
│   └── styles/global.css    # 设计 Token + Tailwind 入口
├── scripts/                 # 工具脚本 (预览代理 …)
├── public/                  # 静态资源
├── docs/                    # 工程文档
├── archive/                 # 迁移前的静态版 (参考)
├── yudao-backend/           # 后端 (单仓库)
└── astro.config.mjs
```

## 快速开始

### 前置依赖

- Node.js 18+
- JDK 21 + Maven 3.9
- MySQL 8.0 (本地默认连 `yudao_club`,亦可指向服务器) 
- Redis 5
- (可选) `.env` 文件提供数据库/Redis 连接信息(用于指向服务器,见下方「密钥处理」)

### 启动 (本地开发)

**1. 数据库**

```powershell
# 管理员 PowerShell
net start MySQL80
```

**2. Redis**

```powershell
D:\dev-tools\redis\redis-server.exe
```

**3. 后端**

```powershell
# 使用 .env 中的连接配置启动(推荐,见「密钥处理」)
cd yudao-backend
.\start-server.ps1

# 或使用本地默认配置直接启动
cd yudao-backend
mvn spring-boot:run -pl yudao-server -Dspring-boot.run.arguments="--server.port=48080"
```

> 后端地址: http://localhost:48080
> 默认管理账号: admin / admin123
> 社团管理账号: clubadmin / admin123

**4. 前端开发服务器**

```powershell
npm run dev
```

> 预览地址: http://localhost:4321

### 生产构建 + 代理预览

```powershell
npm run build            # 输出到 dist/
npm run preview:proxy    # 静态文件 + API 代理,http://localhost:4321
```

## 可用脚本

| 命令 | 说明 |
|------|------|
| `npm run dev` | Astro 开发服务器 (热重载) |
| `npm run build` | 生产构建 → `dist/` |
| `npm run preview` | Astro 原生预览 (不含 API 代理) |
| `npm run preview:proxy` | 预览 + API 代理 (后端联调必用) |

## 密钥处理 (避免泄露到仓库)

> 数据库/Redis 的连接信息属于敏感信息,**绝不提交到 Git**。

- 后端 `application-local.yaml` 里 MySQL/Redis 连接使用**环境变量占位符**(`${MYSQL_URL:默认值}` 等),仓库内**不含任何服务器真实密钥**。
- 真实连接信息放在 `yudao-backend/.env`(**已被 .gitignore 忽略**),通过 `yudao-backend/start-server.ps1` 启动时读入环境变量。
- 提交前可用配置模板从 `yudao-backend\.env.example` 复制得到。
- 如需指向服务器,按需在 `.env` 覆盖:
  - `MYSQL_URL` / `MYSQL_USERNAME` / `MYSQL_PASSWORD`
  - `REDIS_HOST` / `REDIS_PORT` / `REDIS_DATABASE` / `REDIS_USERNAME` / `REDIS_PASSWORD`

> ⚠️ 服务器默认不开放外网直连安全组时,本地无法 `telnet` 到数据库/Redis 端口,需在云控制台放行来源 IP,或把后端部署到同一服务器/内网。

## 架构

```
访问者 → Astro 静态站点 (CDN / Nginx)
  ├── 浏览: 首页 / 业务线详情 / 长文档
  └── 提交: 报名 & 反馈 → POST /yudao-api/admin-api/club/register|feedback
                          → 反向代理 → Yudao 后端 → MySQL

运营 → /admin/ 管理后台 (clubadmin 登录)
  ├── 公告 / 报名记录 / 反馈记录
  └── 数据同样存于 yudao_club 库
```

## 文档

| 文档 | 内容 |
|------|------|
| [docs/01-PRD.md](docs/01-PRD.md) | 需求规格 |
| [docs/02-TECH-DESIGN.md](docs/02-TECH-DESIGN.md) | 技术设计 |
| [docs/03-DESIGN-SPEC.md](docs/03-DESIGN-SPEC.md) | 视觉规范 |
| [docs/04-API-CONTRACT.md](docs/04-API-CONTRACT.md) | 接口契约 |
| [docs/05-ROADMAP.md](docs/05-ROADMAP.md) | 实施计划 |
| [docs/06-DEV-GUIDE.md](docs/06-DEV-GUIDE.md) | 开发手册 (环境/流程/易错点) |
| [docs/DEPLOY.md](docs/DEPLOY.md) | 部署手册 |
| [docs/YUDAO_BACKEND.md](docs/YUDAO_BACKEND.md) | Yudao 后端实操 |

## 许可证

MIT