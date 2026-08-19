import { defineMiddleware } from 'astro:middleware';

export const onRequest = defineMiddleware(async (context, next) => {
  // 临时测试:所有请求都返回标记,验证 middleware 是否被加载
  return new Response('MIDDLEWARE-ALIVE path=' + context.url.pathname, { status: 200 });
});
