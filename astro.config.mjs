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
});
