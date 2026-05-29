import flowbite from 'flowbite/plugin'

export default {
  content: [
    './index.html',
    './src/**/*.{vue,js}',
    './node_modules/flowbite/**/*.js',
    './node_modules/flowbite-vue/**/*.{js,vue}',
  ],
  theme: {
    extend: {
      colors: {
        skyglass: {
          50: '#f4fbff',
          100: '#e7f6ff',
          200: '#ccecff',
          300: '#a9dcfb',
          400: '#79c4f2',
          500: '#4ca8e5',
          600: '#2b88c7',
          700: '#216c9f',
          800: '#1f5b83',
          900: '#1d4c6e',
        },
      },
      boxShadow: {
        soft: '0 18px 50px rgba(74, 126, 156, 0.12)',
      },
      fontFamily: {
        sans: ['Inter', 'ui-sans-serif', 'system-ui', 'sans-serif'],
      },
    },
  },
  plugins: [flowbite],
}
