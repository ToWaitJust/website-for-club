# 06 · 社团项目专属开发文档

> 版本:v1.0 ｜ 状态:环境就绪 ｜ 日期:2026-08-19
> 本文档是本项目的标准开发手册(参照 ThalX《开发文档》结构:原则 → 流程 → 易错点)。
> 配套:docs/01-PRD ~ 05-ROADMAP、docs/YUDAO_BACKEND.md、docs/DEPLOY.md

---

## 第一部分:本地环境速查(本机已就绪)

| 项 | 位置 / 配置 | 启动方式 |
|----|------------|----------|
| JDK | `D:\dev-tools\java`(21.0.4) | - |
| Maven | `D:\dev-tools\maven`(3.9.16,已配阿里云镜像) | 见下方「Maven 调用」 |
| MySQL | 服务 `MySQL80`,库 `yudao_club`(root/123456) | **管理员** PowerShell:`net start MySQL80` |
| Redis | `D:\dev-tools\redis\redis-server.exe` | `D:\dev-tools\redis\redis-server.exe` |
| 后端工程 | `D:\社团网站\project_20260819_112915\yudao-backend`(yudao-boot-mini 精简版) | `mvn spring-boot:run -pl yudao-server` |
| 门户工程 | `D:\社团网站\project_20260819_112915\projects`(Astro) | `npm run dev` / `npm run build` |
| 后端地址 | http://localhost:48080 ｜ 登录 admin / admin123 | |
| 管理后台 | `yudao-backend\yudao-ui`(Vue3,可选) | `npm run dev` |

### Maven 调用(必须用 PowerShell,不要用 Git Bash)

> 踩坑记录:Git Bash 下 `M2_HOME=D:\dev-tools\maven` 的反斜杠会被 shell 拼坏,报
> `ClassNotFoundException: org.codehaus.plexus.classworlds.launcher.Launcher`。**必须用 PowerShell**。

```powershell
# 每次新终端先设置(或写入 $PROFILE)
$env:M2_HOME = "D:\dev-tools\maven"
$env:MAVEN_HOME = "D:\dev-tools\maven"
$env:JAVA_HOME = "D:\dev-tools\java"
$env:Path = "D:\dev-tools\maven\bin;D:\dev-tools\java\bin;" + $env:Path
[Console]::OutputEncoding = [System.Text.Encoding]::UTF8   # 中文日志不乱码
```

### 数据库说明

- 库名 `yudao_club`(2026-08-19 新建,初始化自 `yudao-backend\sql\mysql\ruoyi-vue-pro.sql`,48 表)
- 本地 MySQL root 密码已重置为 `123456`(旧密码遗失)
- 连接串在 `yudao-server/src/main/resources/application-local.yaml`:`jdbc:mysql://127.0.0.1:3306/yudao_club`

---

## 第二部分:后端开发流程

### 2.1 工程结构(yudao-boot-mini,只保留 system + infra)

```
yudao-backend/
├── yudao-server          # 启动模块(聚合)
├── yudao-module-system   # 系统管理(用户/角色/菜单/字典…)
├── yudao-module-infra    # 基础设施(文件/日志/代码生成…)
├── yudao-framework       # 框架层
├── yudao-dependencies    # 依赖版本管理
├── sql/mysql/            # 初始化 SQL
└── yudao-ui/             # 管理后台前端(Vue3)
```

### 2.2 新建业务模块 club(标准流程)

1. **模块骨架**:参考现有 `yudao-module-infra` 的 pom 与包结构,新建 `yudao-module-club`(包名 `cn.iocoder.yudao.module.club`)
2. **表结构**:按 `docs/04-API-CONTRACT.md` §4 建 4 张表(`club_business_line / club_notice / club_register / club_feedback`),DDL 直接在本机 `yudao_club` 库执行(测试库自执行,不堆积给他人)
3. **代码生成**:启动后管理后台 → 开发工具 → 代码生成,导入表 → 一键生成前后端
4. **公开接口**:报名/反馈 Controller 加 `@PermitAll` + SaToken 放行 + `@RateLimiter`(详见 docs/04 §1.3)
5. **编译节奏**(用户约定):**改完一定数量再编译,全量改完先测试,最后才打包**(打包 `mvn clean package` 耗时,不轻易触发)

### 2.3 编译 / 启动 / 验证

```powershell
# 编译(改代码后必执行,连带依赖模块)
mvn install -pl yudao-module-club -am -DskipTests

# 启动(依赖已 install 后,不带 -am)
mvn spring-boot:run -pl yudao-server

# 验证(未登录返回 401 属正常)
Invoke-WebRequest -Uri "http://localhost:48080/admin-api/system/dict-data/simple-list" -UseBasicParsing
```

### 2.4 门户联调

- 门户报名/反馈走 `/yudao-api/admin-api/club/register|feedback`(Nginx 反代;本地开发在浏览器 devtools 里把 `site.apiBase` 指到 `http://localhost:48080`)
- 门户公告二期改走 `GET /admin-api/club/notice/list`

---

## 第三部分:易错点(本机实测)

| # | 现象 | 原因 | 解决 |
|---|------|------|------|
| 1 | Git Bash 跑 mvn 报 `Launcher ClassNotFoundException` | `M2_HOME` 反斜杠被 bash 拼坏 | **用 PowerShell** 并设置三个 env(见上) |
| 2 | `net start MySQL80` 报系统错误 5(拒绝访问) | 服务启动需要管理员权限 | 管理员 PowerShell 执行;或 `Start-Process net start MySQL80 -Verb RunAs` |
| 3 | 普通进程启动 mysqld 报 `datadir Permission denied` | `C:\ProgramData\MySQL\...\Data` 默认仅管理员可写 | 管理员 `icacls "…\Data\*" /grant 16637:F /T /C /Q`(已执行,本机已可直启) |
| 4 | mysqld 报 `ibdata1 must be writable` | **残留 mysqld 实例占用文件**(skip 模式遗留双实例) | `Get-Process mysqld \| Stop-Process -Force` 清干净再启动 |
| 5 | `--skip-grant-tables` 启动后连不上 TCP | 该模式默认禁用网络(port 0) | 加 `--shared-memory`(客户端 `mysql --protocol=memory -uroot`) |
| 6 | 重置 root 密码后立刻被拒 | skip 模式下 ALTER USER 需先 `FLUSH PRIVILEGES` | `FLUSH PRIVILEGES; ALTER USER 'root'@'localhost' IDENTIFIED BY 'xxx'; FLUSH PRIVILEGES;` |
| 7 | MySQL SOURCE 导入中文路径 SQL 报 `Failed to open file`(路径乱码) | mysql 客户端读文件用 GBK 代码页 | SQL 复制到无中文路径(如 `D:\dev-tools\tmp\`)再 SOURCE |
| 8 | 改代码重启后仍是旧行为 | 只 compile 未 install | `mvn install -pl <模块> -am -DskipTests` 后再启动 |
| 9 | 编译下载依赖慢 | 默认中央仓库 | settings.xml 已加阿里云镜像(`D:\dev-tools\maven\conf\settings.xml`) |
| 10 | 新模块 Controller 接口全 404(`请求地址不存在`) | **Controller 的 `@RequestMapping` 写了 `/admin-api` 前缀**。yudao 框架 `WebProperties` 会给 `**.controller.admin.**` 包自动加 `/admin-api` 前缀,写重复了 | Controller 里只写 `/club/xxx`,不要带 `/admin-api`(对照 `yudao-module-system` 的 `@RequestMapping("/system/auth")` 写法) |
| 11 | club 接口返回 200 但 body 是 `code:404` | yudao 对未注册路由统一返回 HTTP 200 + body code=404,`curl -w %{http_code}` 会误判"成功" | 判断接口成功要看 body 里的 `code==0`,不能只看 HTTP 状态码 |
| 12 | `mvn package` 后 fat jar 里仍是旧模块 jar | `package`(非 clean)时若源码无变化,repackage 可能沿用旧依赖缓存 | 改完依赖模块后:**先 `mvn install -pl <模块> -am -DskipTests`,再 `mvn clean package -pl yudao-server -DskipTests`**(clean 必须带) |
| 13 | 验证新模块是否被加载/路由是否注册 | 黑盒测试慢 | 临时加 `ApplicationRunner` 探针类打印 `beanDefinitionNames` 和 `RequestMappingHandlerMapping.getHandlerMethods()`,定位后删除(本次定位"前缀重复"就靠它) |

---

## 第四部分:文档地图

| 文档 | 用途 |
|------|------|
| `docs/01-PRD.md` | 需求规格(做什么) |
| `docs/02-TECH-DESIGN.md` | 技术设计(怎么做) |
| `docs/03-DESIGN-SPEC.md` | 门户视觉规范 |
| `docs/04-API-CONTRACT.md` | 接口契约(前后端对齐) |
| `docs/05-ROADMAP.md` | 实施计划与验收 |
| `docs/06-DEV-GUIDE.md` | **本文档:本机环境 + 开发流程 + 易错点** |
| `docs/YUDAO_BACKEND.md` | yudao 实操(建表/代码生成/公开接口) |
| `docs/DEPLOY.md` | 部署手册(服务器) |
