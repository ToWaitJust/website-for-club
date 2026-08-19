# 04 · API 接口契约

> 版本:v1.0 ｜ 状态:评审稿 ｜ 日期:2026-08-19
> 前端(Astro 门户)与后端(yudao club 模块)以此契约为准,双方独立开发,互不阻塞。
> 门户侧封装:`src/lib/api.ts`;后端侧实现:yudao-module-club。

---

## 1. 通用约定

### 1.1 地址

- 门户与后台同域反代:门户请求 `/yudao-api/...`,Nginx 重写为后端 `/admin-api/...`
- 后端基础路径:`/admin-api`(yudao 默认)
- 门户侧 apiBase 配置在 `src/data/site.ts`(一期默认 `/yudao-api`)

### 1.2 统一响应格式(所有接口)

```json
成功: { "code": 0, "data": {...}, "msg": "" }
失败: { "code": 400, "data": null, "msg": "错误描述" }
```

| code | 含义 |
|------|------|
| 0 | 成功 |
| 400 | 参数校验失败(msg 为给用户看的中文) |
| 401 | 未登录/登录过期 |
| 403 | 无权限 |
| 429 | 触发限流 |
| 500 | 服务器错误 |

### 1.3 鉴权

| 接口 | 鉴权 | 说明 |
|------|------|------|
| 报名 / 反馈(公开) | 无(@PermitAll + SaToken 放行) | 门户匿名调用 |
| 公告/业务线查询(二期) | 无 | 门户拉取展示数据 |
| 后台管理接口 | 登录 + 权限 | yudao 自动处理 |

### 1.4 限流

- 报名/反馈:`@RateLimiter`,单 IP 10 秒 5 次
- 超限返回 `{"code":429,"msg":"操作太频繁,请稍后再试"}`

---

## 2. 公开接口

### 2.1 提交报名

```
POST /admin-api/club/register
Content-Type: application/json
```

请求体:

| 字段 | 类型 | 必填 | 约束 |
|------|------|------|------|
| businessLine | string | 是 | 枚举 ai-it / ai-biz / ai-embed |
| name | string | 是 | 1~50 字 |
| studentId | string | 是 | 1~20 字(学号) |
| college | string | 是 | 1~50 字 |
| major | string | 否 | ≤50 字 |
| phone | string | 是 | ≤20 字 |
| wechat | string | 否 | ≤50 字 |
| motivation | string | 否 | ≤1000 字 |

```json
请求示例:
{
  "businessLine": "ai-it",
  "name": "张三",
  "studentId": "2025xxxxxx",
  "college": "计算机与大数据学院",
  "major": "软件工程",
  "phone": "13800000000",
  "wechat": "zhangsan",
  "motivation": "想学全栈和 AI 工程化"
}

成功:
{ "code": 0, "data": { "id": 1 }, "msg": "" }

失败:
{ "code": 400, "data": null, "msg": "请填写姓名" }
```

后端逻辑:校验 → 写 `club_register` → 返回自增 id。业务要求:允许重复报名(不做唯一约束),运营在后台去重。

### 2.2 提交反馈

```
POST /admin-api/club/feedback
Content-Type: application/json
```

| 字段 | 类型 | 必填 | 约束 |
|------|------|------|------|
| page | string | 是 | 来源页标识,≤50 字(如 home / ai-it / ai-biz / ai-embed / docs-learning-plan) |
| name | string | 是 | 1~50 字 |
| content | string | 是 | 5~2000 字 |

```json
请求:
{ "page": "ai-it", "name": "李四", "content": "建议增加周末 workshop" }

成功:
{ "code": 0, "data": null, "msg": "" }
```

### 2.3 查询公告列表(二期)

```
GET /admin-api/club/notice/list?pageNo=1&pageSize=10
```

```json
成功:
{ "code": 0, "data": { "list": [ { "id": 1, "title": "招新启动", "content": "...", "publishTime": "2026-08-19 10:00:00" } ], "total": 1 }, "msg": "" }
```

仅返回 `status=1`(已发布)且 `publishTime <= now` 的公告,按时间倒序。

### 2.4 查询业务线列表(二期)

```
GET /admin-api/club/business-line/list
```

```json
成功:
{ "code": 0, "data": [ { "id": 1, "slug": "ai-it", "name": "AI+IT", "tagline": "...", "contentMd": "...", "registerOpen": true, "accent": "#2563eb" } ], "msg": "" }
```

---

## 3. 后台管理接口

由 yudao 代码生成器产出标准 CRUD,此处只列关键差异:

| 接口 | 说明 |
|------|------|
| `GET /admin-api/club/register/page` | 报名分页(支持 businessLine/status 筛选) |
| `PUT /admin-api/club/register/update-status` | 状态流转(0待处理/1已联系/2已录取/3未录取) |
| `GET /admin-api/club/register/export-excel` | 报名导出(带筛选条件) |
| `GET /admin-api/club/feedback/page` | 反馈分页(支持 page 筛选) |
| `PUT /admin-api/club/feedback/update-status` | 标记已读 |
| `GET /admin-api/club/feedback/export-excel` | 反馈导出 |
| `POST|PUT|DELETE /admin-api/club/notice/...` | 公告 CRUD(二期对门户生效) |
| `POST|PUT|DELETE /admin-api/club/business-line/...` | 业务线 CRUD(二期对门户生效) |

---

## 4. 数据库表(与接口对应)

```sql
-- 业务线
CREATE TABLE club_business_line (
  id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
  slug VARCHAR(50) NOT NULL,
  name VARCHAR(50) NOT NULL,
  tagline VARCHAR(200) NULL,
  content_md MEDIUMTEXT NULL,
  register_open TINYINT NOT NULL DEFAULT 1,
  sort INT NOT NULL DEFAULT 0,
  creator VARCHAR(64) DEFAULT '', create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updater VARCHAR(64) DEFAULT '', update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  deleted BIT NOT NULL DEFAULT 0,
  UNIQUE KEY uk_slug (slug)
) ENGINE=InnoDB COMMENT='业务线';

-- 公告
CREATE TABLE club_notice (
  id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
  title VARCHAR(200) NOT NULL,
  content TEXT NULL,
  publish_time DATETIME NULL,
  status TINYINT NOT NULL DEFAULT 1,
  creator VARCHAR(64) DEFAULT '', create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updater VARCHAR(64) DEFAULT '', update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  deleted BIT NOT NULL DEFAULT 0
) ENGINE=InnoDB COMMENT='公告';

-- 报名
CREATE TABLE club_register (
  id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
  business_line VARCHAR(50) NOT NULL,
  name VARCHAR(50) NOT NULL,
  student_id VARCHAR(20) NULL,
  college VARCHAR(50) NULL,
  major VARCHAR(50) NULL,
  phone VARCHAR(20) NULL,
  wechat VARCHAR(50) NULL,
  motivation VARCHAR(1000) NULL,
  status TINYINT NOT NULL DEFAULT 0,
  creator VARCHAR(64) DEFAULT '', create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updater VARCHAR(64) DEFAULT '', update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  deleted BIT NOT NULL DEFAULT 0,
  KEY idx_line (business_line), KEY idx_status (status)
) ENGINE=InnoDB COMMENT='报名记录';

-- 反馈
CREATE TABLE club_feedback (
  id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
  page VARCHAR(50) NOT NULL,
  name VARCHAR(50) NOT NULL,
  content VARCHAR(2000) NOT NULL,
  status TINYINT NOT NULL DEFAULT 0,
  creator VARCHAR(64) DEFAULT '', create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updater VARCHAR(64) DEFAULT '', update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  deleted BIT NOT NULL DEFAULT 0,
  KEY idx_page (page)
) ENGINE=InnoDB COMMENT='反馈记录';
```

> `deleted` 字段(yudao 逻辑删除规范)为 0 表示正常;后端所有查询必须带 `deleted=0` 条件(代码生成器自动处理)。

---

## 5. 前端封装约定(`src/lib/api.ts`)

```ts
// 统一入口,返回 data 或抛错(错误信息取 result.msg)
async function post<T>(path: string, body: unknown): Promise<T>

export const api = {
  register: (payload: RegisterPayload) => post('/admin-api/club/register', payload),
  feedback: (payload: FeedbackPayload) => post('/admin-api/club/feedback', payload),
  // 二期
  notices: () => get('/admin-api/club/notice/list', { params }),
  businessLines: () => get('/admin-api/club/business-line/list'),
}
```

- 成功(code===0):返回 `data`
- 失败:控制台 warn + 抛错,UI 展示 `msg`
- 网络异常:UI 提示"网络错误,请稍后再试"

---

## 6. 联调自检清单

- [ ] 报名匿名可提交,`businessLine` 三值均可用
- [ ] 必填缺失/超长返回 400 与中文 msg
- [ ] 连续 6 次提交触发 429
- [ ] 后台列表/筛选/状态流转/导出 Excel 全通
- [ ] 反馈从 home / 三个业务线 / 文档页提交,page 字段正确
- [ ] Nginx `/yudao-api/` 重写后接口可用(CORS 无报错)
