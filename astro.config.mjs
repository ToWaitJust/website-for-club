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
  // 本地代理:把 /yudao-api 前缀转发到 yudao 后端(48080),dev 与 preview 均生效
  server: {
    proxy: {
      '/yudao-api': {
        target: 'http://localhost:48080',
        changeOrigin: true,
        rewrite: (p) => p.replace(/^\/yudao-api/, ''),
      },
    },
  },
  preview: {
    proxy: {
      '/yudao-api': {
        target: 'http://localhost:48080',
        changeOrigin: true,
        rewrite: (p) => p.replace(/^\/yudao-api/, ''),
      },
    },
  },
});
