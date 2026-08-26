// ─── Astro 中间件:本地联调 API 代理 ─────────────────────────────────
// 作用:dev/preview 模式下,将 /yudao-api/* 请求转发到本机 yudao 后端(48080),
//      并把 /yudao-api 前缀替换为 /admin-api(后端路由是 /admin-api/...),
//      与生产 Nginx 反代(location /yudao-api/ → proxy_pass .../admin-api/)
//      语义一致。
// 路径约定:前端统一写 /yudao-api/xxx,【不要再拼 /admin-api】;
//      若拼了,后端会收到 /admin-api/admin-api/... 双前缀 → Sa-Token 401「账号未登录」。
// 注意:门户为静态构建(SSG),本中间件不会进入生产产物;
//      生产环境由 Nginx 将 /yudao-api 反代到后端(见 docs/DEPLOY.md)。
import { defineMiddleware } from 'astro:middleware';

const BACKEND = 'http://localhost:48080';

export const onRequest = defineMiddleware(async (context, next) => {
  const url = new URL(context.request.url);
  if (!url.pathname.startsWith('/yudao-api/')) {
    return next();
  }
  try {
    // 把 /yudao-api 前缀替换为 /admin-api 后转发到后端
    const targetPath = url.pathname.replace(/^\/yudao-api/, '/admin-api') + url.search;
    const target = new URL(targetPath, BACKEND);
    // 显式构造转发头:确保 tenant-id / Authorization / Content-Type 完整送达
    const headers = new Headers();
    headers.set('tenant-id', context.request.headers.get('tenant-id') || '1');
    const auth = context.request.headers.get('authorization');
    if (auth) headers.set('Authorization', auth);
    const ctype = context.request.headers.get('content-type');
    if (ctype) headers.set('Content-Type', ctype);
    const init: RequestInit = {
      method: context.request.method,
      headers,
      redirect: 'follow',
    };
    if (context.request.method !== 'GET' && context.request.method !== 'HEAD') {
      init.body = await context.request.arrayBuffer();
    }
    const upstream = await fetch(target.toString(), init);
    return new Response(upstream.body, {
      status: upstream.status,
      headers: upstream.headers,
    });
  } catch {
    return new Response(JSON.stringify({ code: 503, msg: '后端服务不可达(需先启动 yudao-server:48080)', data: null }), {
      status: 200,
      headers: { 'Content-Type': 'application/json' },
    });
  }
});
