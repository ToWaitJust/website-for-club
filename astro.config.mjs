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
  vite: {
    server: {
      proxy: {
        // 本地联调:前端统一走 /yudao-api,代理到本机 yudao 后端(48080)
        // 生产环境由 Nginx 同路径反代,见 docs/DEPLOY.md
        '/yudao-api': {
          target: 'http://localhost:48080',
          changeOrigin: true,
        },
      },
    },
  },
});
