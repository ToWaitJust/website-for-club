/** @type {import('tailwindcss').Config} */
export default {
  content: ['./src/**/*.{astro,html,js,jsx,md,mdx,svelte,ts,tsx,vue}'],
  theme: {
    // 断点:补 xs(小屏手机)与 3xl(超宽屏),其余沿用默认
    screens: {
      xs: '375px',
      sm: '640px',
      md: '768px',
      lg: '1024px',
      xl: '1280px',
      '2xl': '1536px',
      '3xl': '1920px',
    },
    extend: {
      // ─── 流式字号:引用 global.css 的 --fs-* 单一真源 ───
      fontSize: {
        'fluid-eyebrow': ['var(--fs-eyebrow)', { lineHeight: '1.4' }],
        'fluid-xs': ['var(--fs-xs)', { lineHeight: '1.5' }],
        'fluid-sm': ['var(--fs-sm)', { lineHeight: '1.6' }],
        'fluid-base': ['var(--fs-base)', { lineHeight: '1.7' }],
        'fluid-h3': ['var(--fs-h3)', { lineHeight: '1.4' }],
        'fluid-h2': ['var(--fs-h2)', { lineHeight: '1.3' }],
        'fluid-d2': ['var(--fs-d2)', { lineHeight: '1.15' }],
        'fluid-d1': ['var(--fs-d1)', { lineHeight: '1.05' }],
      },
      // ─── 流式间距:p-f6 / mt-f10 / gap-f5 / top-f4 等均可用 ───
      spacing: {
        f1: 'var(--sp-1)',
        f2: 'var(--sp-2)',
        f3: 'var(--sp-3)',
        f4: 'var(--sp-4)',
        f5: 'var(--sp-5)',
        f6: 'var(--sp-6)',
        f8: 'var(--sp-8)',
        f10: 'var(--sp-10)',
        f12: 'var(--sp-12)',
        f16: 'var(--sp-16)',
        f20: 'var(--sp-20)',
        nav: 'var(--nav-h)',
        'safe-t': 'var(--safe-t)',
        'safe-b': 'var(--safe-b)',
      },
      // ─── 装饰光斑尺寸 ───
      width: {
        'orb-sm': 'var(--orb-sm)',
        'orb-md': 'var(--orb-md)',
        'orb-lg': 'var(--orb-lg)',
      },
      height: {
        'orb-sm': 'var(--orb-sm)',
        'orb-md': 'var(--orb-md)',
        'orb-lg': 'var(--orb-lg)',
        nav: 'var(--nav-h)',
      },
      maxWidth: {
        site: 'var(--container-max)',
      },
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
