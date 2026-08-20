# 03 · UI/UX 设计规范

> 版本:v1.0 ｜ 状态:评审稿 ｜ 日期:2026-08-19
> 目标:门户视觉对标 Linear / Apple 产品页——极简、玻璃拟态、克制的高级感。
> 本规范是开发依据,组件实现必须遵循 token,不自行发明颜色/字号。

---

## 1. 视觉定位

| 关键词 | 说明 |
|--------|------|
| 极简 | 大面积留白,一屏一个焦点 |
| 科技感 | 深色 Hero + 动态网格/粒子,渐变文字点缀 |
| 玻璃拟态 | 卡片毛玻璃背景(backdrop-blur),细边框(1px,低透明度) |
| 克制动效 | 入场淡入上移 0.6s;悬停位移 4px;不使用无意义弹跳 |

**明暗策略**:Hero 区深色(深蓝黑渐变),内容区浅色(白/浅灰)。不做全局暗色模式(一期)。

---

## 2. 设计 Token

### 2.1 色彩

```
中性色:
  --ink:        #1d1d1f    主文字
  --ink-2:      #3a3a3c    次级文字
  --muted:      #86868b    弱文字/说明
  --bg:         #f5f5f7    页面背景
  --surface:    #ffffff    卡片背景
  --line:       rgba(0,0,0,.08)   边框
  --line-strong:rgba(0,0,0,.16)   强调边框

品牌主色(社团):
  --accent:     #2563eb    主蓝(比原 #0071e3 更"数字感")
  --accent-2:   #7c3aed    辅助紫(渐变副色)

业务线品牌色(卡片/详情页主色):
  ai-it:    #2563eb  蓝   (技术与工程)
  ai-biz:   #7c3aed  紫   (商业与创新)
  ai-embed: #059669  绿   (硬件与落地)

语义色:
  success: #16a34a / danger: #dc2626 / warning: #d97706
```

### 2.2 字体

```
中文:Noto Sans SC(400/500/700),系统回退 PingFang SC / Microsoft YaHei
展示:Space Grotesk(英文/数字,用于 Hero 标题、数字强调)
等宽:JetBrains Mono(代码、学号等)
字号阶梯:12 / 14 / 16 / 20 / 28 / 36 / 48(px)
行高:正文 1.6,标题 1.15
```

### 2.3 布局与形状

```
容器:max-width 1120px,左右 padding 24px
圆角:sm 8 / md 12 / lg 16 / xl 24(px)
阴影:
  card:    0 1px 2px rgba(0,0,0,.04), 0 8px 24px rgba(0,0,0,.06)
  hover:   0 2px 4px rgba(0,0,0,.05), 0 16px 48px rgba(0,0,0,.10)
断点:768px(平板)/ 480px(手机);导航在 768px 收起为汉堡
间距:4 的倍数(4/8/12/16/24/32/48/64/96)

### 2.4 流式尺寸 Token(rpx / clamp)★ 一期新增

> 所有尺寸(字号/间距/定位/光斑)一律走下面这套 token,**禁止在组件里写死 px**。
> 实现见 `src/styles/global.css`(变量源) + `tailwind.config.mjs`(映射)。

**rpx 单位**(装饰性按比例缩放,小程序 750 语义):
```
--rpx: 移动端  calc(min(100vw, 500px) / 750)          ← 视口≤500px 严格等比
       桌面端  clamp(0.667px, 0.1117px + 0.0723vw, 1.5px)  ← ≥768px 缓增,封顶 1.5px(防 4K 过大)
```
用途:图标/徽章等"想随屏缩放"的元素,如 `width: calc(96 * var(--rpx))`,并配 `min-width:42px` 兜底。

**流式字号阶梯**(全部 clamp,随视口平滑伸缩):
```
--fs-eyebrow: clamp(11px, 10.6px + 0.1vw, 13px)
--fs-xs:      clamp(12px, 11.82px + 0.05vw, 12.5px)
--fs-sm:      clamp(13px, 12.65px + 0.09vw, 14px)
--fs-base:    clamp(15px, 14.65px + 0.09vw, 16px)
--fs-h3:      clamp(17px, 15.94px + 0.28vw, 20px)
--fs-h2:      clamp(20px, 17.18px + 0.75vw, 28px)
--fs-d2:      clamp(30px, 23.66px + 1.69vw, 48px)
--fs-d1:      clamp(40px, 27.32px + 3.38vw, 76px)   ← Hero 主标题(原 text-6xl 在 375px 会溢出)
```
Tailwind 映射:`text-fluid-eyebrow/xs/sm/base/h3/h2/d2/d1`。

**流式间距阶梯**:
```
--sp-1:4px  --sp-2:8px  --sp-3:12px  --sp-4:16px
--sp-5: clamp(20px,18.59px+0.375vw,24px)   --sp-6: clamp(24px,21.18px+0.75vw,32px)
--sp-8: clamp(32px,26.37px+1.5vw,48px)     --sp-10:clamp(40px,31.55px+2.25vw,64px)
--sp-12:clamp(48px,36.73px+3vw,80px)       --sp-16:clamp(64px,47.1px+4.5vw,112px)
--sp-20:clamp(80px,57.46px+6vw,144px)
```
Tailwind 映射:`p-f1 ~ p-f20 / m-f* / gap-f*`。

**布局 / 安全区 / 光斑**:
```
--nav-h:      clamp(52px, 47.8px + 1.13vw, 64px)   → Tailwind `h-nav`
--container-max: 1120px(≥1920px 升 1280px)         → `.container-site` max-width
--container-px:  clamp(16px, 10.37px + 1.5vw, 32px) → `.container-site` 左右 padding
--safe-t/r/b/l:  env(safe-area-inset-*, 0px)        → 刘海/圆角屏安全区
--orb-sm/md/lg:  clamp(160~340px / 180~380px / 200~440px) → 装饰光斑尺寸
--tap:        44px                                 → 最小触控目标(`.tap-target`)
```

```

---

## 3. 组件规范

### 3.1 导航 Nav

- 固定顶部,h=56px;背景 `rgba(255,255,255,.8)` + `backdrop-blur(20px)`;底部 1px 细线
- 左:logo(渐变方块 + "AI+社团");右:首页 / 三业务线 / 运营后台(↗ 角标)
- 当前页高亮:文字品牌色 + font-weight 600

### 3.2 Hero(首页首屏)

- 深色渐变背景:`linear-gradient(180deg,#0b1220 0%,#101b33 55%,#f5f5f7 100%)`,高度 92vh
- 叠加效果:Canvas 网格线 + 缓慢粒子(纯装饰,`prefers-reduced-motion` 时关闭)
- 内容:社团名(48px,白,Space Grotesk)+ 副标题(20px,蓝白渐变)+ 一句 Slogan(16px,灰白)
- 底部:三张业务线卡片轻微上浮,进入内容区

### 3.3 业务线卡片 BizCard

- 白底玻璃卡片:`background: rgba(255,255,255,.65)` + `backdrop-blur(24px)` + 1px 细边
- 结构:图标(品牌色浅底圆角块)→ 名称(20px)→ tagline(品牌色 14px)→ 描述(14px 灰)→ 特性标签(浅底 pill)→ "进入业务线 →"
- 悬停:`translateY(-4px)` + 阴影增强 + 图标轻微放大;可加 3D tilt(±6°,一期可选)
- 右上角状态徽章:报名中(绿底)/ 待开放(灰底)
- 三张卡片顶部 4px 品牌色条,与 hover 阴影颜色一致

### 3.4 公告 NoticeList

- 白卡片列表,每条:标题(600)+ 日期(12px 灰)+ 内容(14px)
- 空态:"暂无公告"

### 3.5 按钮

```
主按钮:品牌色(跟随页面 accent)实心,圆角 999px,高度 44px,padding 0 24px
  悬停:透明度 .92 / 位移 0;active:scale(.97)
次按钮:透明底 + 1.5px 描边 + accent 文字
链接按钮:纯文字 accent,悬停下划线
```

### 3.6 表单(报名/反馈,Vue 组件)

- 容器:白卡 + lg 圆角 + card 阴影,max-width 640px
- 字段:label(14px 600)+ 输入框(44px,1.5px 边框,聚焦时边框 accent + 3px 同色 15% 光环)
- 必填红星;报名字段:姓名*/学号*/学院*/专业/手机*/微信/动机(textarea)
- 提交:主按钮全宽(移动端)/ 右对齐(桌面);提交中 disabled + "提交中…"
- 反馈:姓名* + 内容*(≥5 字),同表单规范

### 3.7 长文档页(DocLayout)

- 顶部:返回业务线链接 + 文档标题;正文容器 max-width 860px
- 标题层级:h1 28px / h2 20px / h3 16px,均 600;h1 下加 1px 分隔线
- 表格:1px 细边,表头浅底 600;代码块:JetBrains Mono + 深底 `#0b1220` 白字圆角
- 引用块:左侧 3px accent 条 + 浅底
- 流程图(Mermaid):居中,自带缩放控件(+/-/重置),30%~300%

### 3.8 页脚 Footer

- 顶部 1px 分隔线;社团名 + 版权;联系方式(QQ 群/微信/邮箱);字号 12px 灰
- 预留运营后台入口链接(仅运营可见可选)

---

## 4. 动效规范

| 场景 | 参数 |
|------|------|
| 页面入场 | opacity 0→1 + translateY(16px→0),0.6s ease,`prefers-reduced-motion` 关闭 |
| 卡片悬停 | translateY(-4px),0.3s cubic-bezier(.25,.1,.25,1) |
| 滚动入场 | IntersectionObserver,元素进入视口 15% 触发,stagger 80ms |
| Hero 粒子 | 速度 0.2~0.6px/frame,透明度 0.1~0.4,仅装饰 |
| 3D tilt | rotateX/Y ≤ 6°,transition 0.2s,鼠标离开回正(一期可选) |

**原则**:动效必须有意义(引导视线/反馈状态),不堆砌;所有动画尊重 `prefers-reduced-motion`。

---

## 5. 响应式要点(适配落地规约)★ 一期新增

> 核心原则:**一套代码,流式伸缩,不写死 px**。所有尺寸来自 §2.4 的 token。

### 5.1 必须遵循

| 维度 | 做法 | 反例(禁止) |
|------|------|-----------|
| 字号 | `text-fluid-*`(映射 --fs-*) | `text-6xl` / `text-[40px]`(小屏溢出) |
| 间距 | `p-f* / m-f* / gap-f*` | `pt-32` / `px-20` 魔法数 |
| 首屏顶部留白 | `.below-nav` 工具类(自动跟随 --nav-h) | 手写 `pt-32`(导航一改就错位) |
| 绝对/固定定位 | `var(--sp-*)` + `%` + `translate` | `left-[40px] top-[80px]` 写死 |
| 装饰光斑 | `--orb-*` + `blur(clamp(...))` | `h-80 w-80` / `w-[36rem]`(大屏失控) |
| 容器 | `.container-site`(max-width + --container-px) | 裸 `max-w-3xl` 不带水平 padding |
| 视口高度 | `min-h-dvh`(非 `vh`,防移动端地址栏跳动) | `min-h-screen` |
| 导航高度 | `h-nav` | `h-14` / `h-16` |
| 图标/徽章缩放 | `calc(N * var(--rpx))` + `min-width` 兜底 | 固定 `w-24` |
| iOS 输入框 | `font-size:16px`(≥768 用 --fs-sm)防聚焦缩放 | 小于 16px |

### 5.2 防横向溢出三板斧

1. `html, body { overflow-x: clip }`(已落 global.css;勿改 `hidden`,会破坏 sticky)。
2. 导航在移动端 `.no-scrollbar` 横向滚动(5 项不再挤爆 375px)。
3. 数据表格在 `<lg` 改用**卡片布局**(已落 AdminPanel 报名/反馈列表);禁止 `min-w-[760px]` 横向滚动条。

### 5.3 断点(Tailwind `screens`)

```
xs 375 / sm 640 / md 768 / lg 1024 / xl 1280 / 2xl 1536 / 3xl 1920
```
- `md`(768px):三卡片→单列;Hero 字号 `--fs-d1` 自动从 76→40 收敛;导航→横向滚动(非汉堡,一期简化)。
- 桌面宽屏(>768px):`--rpx` 切缓增模式,装饰/图标温和放大但有 1.5px 封顶。

### 5.4 安全区(刘海/圆角屏)

- `BaseLayout` 已设 `viewport-fit=cover`;需贴边的导航/页脚用 `var(--safe-t/r/b/l)` 偏移。
- 触控目标统一 `.tap-target`(≥44px),移动端可点性达标。

### 5.5 自测矩阵(交付前过一遍)

| 宽度 | 检查项 |
|------|--------|
| 320 / 375 | 无横向滚动条;导航可横滑;Hero 标题不换行溢出;表单单列 |
| 768 | 卡片转单列临界点;导航横向滚动顺滑 |
| 1440 | 桌面缓增生效;光斑/图标温和放大不突兀 |
| 2560(4K) | `--rpx` 封顶 1.5px;容器居中不拉伸 |

---

## 6. 三业务线差异化

同一套组件,只换 `data-accent` 品牌色 + 图标 + 文案:

| 业务线 | 品牌色 | 图标 | 文案基调 |
|--------|--------|------|----------|
| AI+IT | 蓝 #2563eb | 💻 | 工程、系统、从零到一 |
| AI+业务 | 紫 #7c3aed | 📈 | 场景、创新、商业落地 |
| AI+嵌入式 | 绿 #059669 | 🔧 | 硬件、实物、边缘智能 |

> 技术实现:根元素 `data-accent` 属性 → Tailwind 用 CSS 变量派生,组件零改动。
