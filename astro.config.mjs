// @ts-check
import { defineConfig } from 'astro/config';
import vue from '@astrojs/vue';
import tailwind from '@astrojs/tailwind';

// https://astro.build/config
export default defineConfig({
  site: 'https://club.example.com',
  integrations: [
    vue(),
    tailwind({
      applyBaseStyles: false, // 由 src/styles/global.css 自行引入 Tailwind
    }),
  ],
  // 本地代理:把 /yudao-api 前缀替换为 /admin-api 转发到 yudao 后端(48080)。
  // 与生产 nginx(location /yudao-api/ → proxy_pass .../admin-api/)语义一致:
  // 前端路径统一写 /yudao-api/xxx,【不要再拼 /admin-api】,否则双前缀 401。
  server: {
    proxy: {
      '/yudao-api': {
        target: 'http://localhost:48080',
        changeOrigin: true,
        rewrite: (p) => '/admin-api' + p.replace(/^\/yudao-api/, ''),
      },
    },
  },
  preview: {
    proxy: {
      '/yudao-api': {
        target: 'http://localhost:48080',
        changeOrigin: true,
        rewrite: (p) => '/admin-api' + p.replace(/^\/yudao-api/, ''),
      },
    },
  },
});
