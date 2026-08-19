/** @type {import('tailwindcss').Config} */
export default {
  content: ['./src/**/*.{astro,html,js,jsx,md,mdx,svelte,ts,tsx,vue}'],
  theme: {
    extend: {
      colors: {
        ink: {
          DEFAULT: '#1d1d1f',
          soft: '#3a3a3c',
        },
        muted: '#86868b',
        bg: '#f5f5f7',
        surface: '#ffffff',
        accent: {
          DEFAULT: '#2563eb',
          soft: '#7c3aed',
        },
        'ai-it': '#2563eb',
        'ai-biz': '#7c3aed',
        'ai-embed': '#059669',
        success: '#16a34a',
        danger: '#dc2626',
        warning: '#d97706',
      },
      fontFamily: {
        sans: [
          '-apple-system',
          'SF Pro Display',
          'Helvetica Neue',
          'PingFang SC',
          'Microsoft YaHei',
          'sans-serif',
        ],
        display: ['Space Grotesk', '-apple-system', 'PingFang SC', 'sans-serif'],
        mono: ['JetBrains Mono', 'SF Mono', 'Consolas', 'monospace'],
      },
      borderRadius: {
        sm: '8px',
        md: '12px',
        lg: '16px',
        xl: '24px',
      },
      boxShadow: {
        card: '0 1px 2px rgba(0,0,0,.04), 0 8px 24px rgba(0,0,0,.06)',
        'card-hover': '0 2px 4px rgba(0,0,0,.05), 0 16px 48px rgba(0,0,0,.10)',
      },
      animation: {
        'fade-up': 'fadeUp 0.6s ease both',
        float: 'float 8s ease-in-out infinite',
      },
      keyframes: {
        fadeUp: {
          from: { opacity: '0', transform: 'translateY(16px)' },
          to: { opacity: '1', transform: 'translateY(0)' },
        },
        float: {
          '0%, 100%': { transform: 'translateY(0)' },
          '50%': { transform: 'translateY(-12px)' },
        },
      },
    },
  },
  plugins: [],
};
