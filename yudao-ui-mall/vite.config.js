import { defineConfig, loadEnv } from 'vite'
import vue from '@vitejs/plugin-vue'

export default defineConfig(({ mode }) => {
  const env = loadEnv(mode, process.cwd(), '')
  return {
    plugins: [vue()],
    server: {
      port: 3000,
      proxy: {
        '/admin-api': {
          target: env.VITE_API_TARGET || 'http://localhost:48080',
          changeOrigin: true
        }
      }
    }
  }
})
