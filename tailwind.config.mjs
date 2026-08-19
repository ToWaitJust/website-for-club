/** @type {import('tailwindcss').Config} */
export default {
  content: ['./src/**/*.{astro,html,js,jsx,md,mdx,svelte,ts,tsx,vue}'],
  theme: {
    extend: {
      colors: {
        ink: {
          DEFAULT: '#f5f5f7',
          soft: '#a1a1aa',
        },
        muted: '#71717a',
        bg: '#0a0f1e',
        surface: 'rgba(255,255,255,0.05)',
        line: 'rgba(255,255,255,0.10)',
        accent: {
          DEFAULT: '#3b82f6',
          soft: '#a78bfa',
        },
        'ai-it': '#3b82f6',
        'ai-biz': '#a78bfa',
        'ai-embed': '#34d399',
        success: '#34d399',
        danger: '#f87171',
        warning: '#fbbf24',
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
        card: '0 1px 2px rgba(0,0,0,.4), 0 8px 24px rgba(0,0,0,.35)',
        'card-hover':
          '0 2px 4px rgba(0,0,0,.45), 0 16px 48px rgba(0,0,0,.5), 0 0 0 1px rgba(255,255,255,.06)',
      },
      animation: {
        'fade-up': 'fadeUp 0.6s ease both',
        float: 'float 8s ease-in-out infinite',
        'pulse-slow': 'pulseSlow 6s ease-in-out infinite',
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
        pulseSlow: {
          '0%, 100%': { opacity: '0.5' },
          '50%': { opacity: '0.9' },
        },
      },
    },
  },
  plugins: [],
};
