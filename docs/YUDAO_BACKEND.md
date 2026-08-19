# yudao 后端设计与落地指南

> 目标:在 yudao 上搭建社团运营后台,为门户提供报名/反馈 API。
> 面向:正在学习 yudao 的技术组同学,本文档是"边学边做"的完整路径。

---

## 一、环境与裁剪

| 项 | 建议 | 说明 |
|----|------|------|
| 版本 | **yudao-boot 单体版**(非 cloud) | 2C4G 服务器,单体足够 |
| JDK | 17+(yudao 3.x 要求) | |
| 数据库 | MySQL 8 | |
| 缓存 | Redis 7 | yudao 强依赖(会话/缓存) |
| 前端 | yudao-ui-admin(Vue3) | 管理后台界面 |

**裁剪**:`yudao-server/pom.xml` 中只保留 `yudao-module-system` 与 `yudao-module-infra` 依赖,删掉 `yudao-module-pay` / `mall` / `crm` / `erp` / `workflow` / `mp` 等模块依赖。启动后只出现"系统管理 + 基础设施"菜单,内存占用显著下降。

---

## 二、表结构设计(club 模块)

在 yudao 中新建模块 `yudao-module-club`,先建 4 张表(后续可按需扩展):

```sql
-- 业务线
CREATE TABLE club_business_line (
  id            BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
  slug          VARCHAR(50)  NOT NULL COMMENT '标识: ai-it / ai-biz / ai-embed',
  name          VARCHAR(50)  NOT NULL COMMENT '业务线名称',
  tagline       VARCHAR(200) NULL COMMENT '一句话简介',
  content_md    MEDIUMTEXT   NULL COMMENT '简介 Markdown(冗余,用于后台编辑)',
  register_open TINYINT      NOT NULL DEFAULT 1 COMMENT '报名是否开放',
  sort          INT          NOT NULL DEFAULT 0,
  creator       VARCHAR(64)  DEFAULT '',
  create_time   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updater       VARCHAR(64)  DEFAULT '',
  update_time   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  deleted       BIT          NOT NULL DEFAULT 0,
  UNIQUE KEY uk_slug (slug)
) ENGINE=InnoDB COMMENT='业务线';

-- 公告
CREATE TABLE club_notice (
  id          BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
  title       VARCHAR(200) NOT NULL,
  content     TEXT         NULL,
  publish_time DATETIME    NULL,
  status      TINYINT      NOT NULL DEFAULT 1 COMMENT '1=发布 0=下架',
  creator     VARCHAR(64)  DEFAULT '',
  create_time DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updater     VARCHAR(64)  DEFAULT '',
  update_time DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  deleted     BIT          NOT NULL DEFAULT 0
) ENGINE=InnoDB COMMENT='公告';

-- 报名记录
CREATE TABLE club_register (
  id            BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
  business_line VARCHAR(50)  NOT NULL COMMENT '业务线 slug',
  name          VARCHAR(50)  NOT NULL,
  student_id    VARCHAR(20)  NULL,
  college       VARCHAR(50)  NULL,
  major         VARCHAR(50)  NULL,
  phone         VARCHAR(20)  NULL,
  wechat        VARCHAR(50)  NULL,
  motivation    VARCHAR(1000) NULL,
  status        TINYINT      NOT NULL DEFAULT 0 COMMENT '0=待处理 1=已联系 2=已录取 3=未录取',
  creator       VARCHAR(64)  DEFAULT '',
  create_time   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updater       VARCHAR(64)  DEFAULT '',
  update_time   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  deleted       BIT          NOT NULL DEFAULT 0,
  KEY idx_line (business_line)
) ENGINE=InnoDB COMMENT='报名记录';

-- 反馈记录
CREATE TABLE club_feedback (
  id          BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
  page        VARCHAR(50)  NOT NULL COMMENT '来源页面',
  name        VARCHAR(50)  NOT NULL,
  content     VARCHAR(2000) NOT NULL,
  status      TINYINT      NOT NULL DEFAULT 0 COMMENT '0=待处理 1=已读',
  creator     VARCHAR(64)  DEFAULT '',
  create_time DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updater     VARCHAR(64)  DEFAULT '',
  update_time DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  deleted     BIT          NOT NULL DEFAULT 0,
  KEY idx_page (page)
) ENGINE=InnoDB COMMENT='反馈记录';
```

> 说明:`creator` / `create_time` / `deleted` 等字段是 yudao 的统一规范(Do 基类含这些字段),代码生成器会按此处理。`club_register.status` 便于运营标记联系进度,配合 yudao 的字典功能做下拉。

---

## 三、API 契约(门户 → 后端)

门户前端已按以下契约开发,后端照此实现即可,前端无需改动。

### 3.1 提交报名

```
POST /admin-api/club/register
Content-Type: application/json
公开接口(匿名可调,见第四节)

请求:
{
  "businessLine": "ai-it",      // 业务线 slug,必填
  "name": "张三",                // 必填
  "studentId": "2025xxxxxx",
  "college": "计算机与大数据学院",
  "major": "软件工程",
  "phone": "13800000000",
  "wechat": "zhangsan",
  "motivation": "想学全栈"
}

成功响应(HTTP 200):
{ "code": 0, "data": { "id": 1 }, "msg": "" }

失败响应:
{ "code": 400, "data": null, "msg": "请填写姓名" }
```

### 3.2 提交反馈

```
POST /admin-api/club/feedback
Content-Type: application/json
公开接口(匿名可调)

请求:
{ "page": "ai-it", "name": "李四", "content": "建议增加...", }

成功响应:
{ "code": 0, "data": null, "msg": "" }
```

> 门户侧 apiBase 配置在 `content/site.json` 的 `site.apiBase`(默认 `/yudao-api`),由 Nginx 反代到后端,见 `docs/DEPLOY.md`。若直连后端域名,改这个字段即可。

---

## 四、公开接口配置(yudao 侧)

门户是匿名请求,需要绕过 yudao 的登录校验:

1. **Controller 方法加注解**:

```java
@PostMapping("/register")
@PermitAll   // 关键:匿名可访问
public CommonResult<Long> createRegister(@Valid @RequestBody ClubRegisterSaveReqVO createReqVO) { ... }

@PostMapping("/feedback")
@PermitAll
public CommonResult<Boolean> createFeedback(@Valid @RequestBody ClubFeedbackSaveReqVO createReqVO) { ... }
```

2. **SaToken 放行**:在 `security` 配置或 `application.yaml` 的 `sa-token` 拦截配置中,将 `/admin-api/club/register`、`/admin-api/club/feedback` 加入免认证列表(与 `/admin-api/system/auth/login` 同级处理)。

3. **字段校验**(`@Valid` + `@NotNull` 等):`businessLine`、`name` 必填,`content` 长度校验,与前端组件校验一致。

---

## 五、CORS 配置

门户与后台若不同域(如门户 `club.example.com`,后端 `api.example.com`),需要放开跨域。yudao 中配置:

```yaml
yudao:
  security:
    # 允许的跨域来源
    cors-allowed-origins:
      - https://club.example.com
```

若门户与后端同域(Nginx 反代 `/yudao-api`),则无需 CORS 配置——**推荐同域方案,少一类问题**。

---

## 六、代码生成器使用步骤(第一次做,之后照抄)

1. 在 yudao-ui-admin 后台「开发工具 → 代码生成」中,导入上面 4 张表
2. 生成配置:包名 `cn.iocoder.yudao.module.club`,模块名 `club`,前端类型 Vue3
3. 一键生成 → 得到 `yudao-module-club` 后端代码 + `club` 前端页面
4. 手动补两处:
   - `ClubRegisterController.createRegister` / `ClubFeedbackController.createFeedback` 加 `@PermitAll`(见第四节)
   - 去掉生成的列表中"创建人"字段,或保留均可
5. 重启 yudao-server,菜单「系统管理 → 菜单管理」刷新,将 club 菜单分配给运营角色
6. 后台新增"运营"角色,只勾选 club 相关菜单 + 系统必要的个人信息菜单

---

## 七、运营后台菜单规划(建议)

```
社团运营
├── 业务线管理        # club_business_line CRUD
├── 公告管理          # club_notice CRUD
├── 报名管理          # club_register 列表 + 状态流转 + 导出
└── 反馈管理          # club_feedback 列表 + 标记已读 + 导出
```

导出用 yudao 自带的 `@ExcelExport` 能力,运营可直接下载报名名单 CSV/Excel。

---

## 八、联调自检清单

- [ ] `POST /admin-api/club/register` 匿名可调,数据落库
- [ ] 重复学号是否允许(业务决定,默认允许,后续可加唯一约束)
- [ ] 报名/反馈接口限流(yudao 支持 `@RateLimiter`,防止刷数据)
- [ ] 运营后台可见报名/反馈列表,可导出
- [ ] 门户公告改为从 `GET /admin-api/club/notice/list` 读取(二期可做,一期用 site.json)
