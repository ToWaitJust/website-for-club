// ─── Astro 中间件:本地联调 API 代理 ─────────────────────────────────
// 作用:dev/preview 模式下,将 /yudao-api/* 请求转发到本机 yudao 后端(48080),
//      使前端统一走 /yudao-api 前缀,与生产 Nginx 反代行为一致。
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
    // 去掉 /yudao-api 前缀后转发到后端(后端路由是 /admin-api/...)
    const targetPath = url.pathname.replace(/^\/yudao-api/, '') + url.search;
    const target = new URL(targetPath, BACKEND);
    // 显式构造转发头:确保 tenant-id / Authorization / Content-Type 完整送达
    const headers = new Headers();
    headers.set('tenant-id', context.request.headers.get('tenant-id') || '1');
    const auth = context.request.headers.get('authorization');
    if (auth) headers.set('Authorization', auth);
    const ctype = context.request.headers.get('content-type');
    if (ctype) headers.set('Content-Type', ctype);
    // 其余业务头(如 token 类自定义头)一并转发
    context.request.headers.forEach((value, key) => {
      const k = key.toLowerCase();
      if (!['host', 'content-length', 'connection', 'tenant-id', 'authorization', 'content-type'].includes(k)) {
        headers.set(key, value);
      }
    });
    const init: RequestInit = {
      method: context.request.method,
      headers,
      redirect: 'follow',
    };
    if (context.request.method !== 'GET' && context.request.method !== 'HEAD') {
      init.body = await context.request.arrayBuffer();
    }
    const upstream = await fetch(target.toString(), init);
    console.log(`[proxy] ${context.request.method} ${targetPath} -> ${upstream.status} tenant=${headers.get('tenant-id')} auth=${auth ? 'yes' : 'no'} ctype=${ctype}`);
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
