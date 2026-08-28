<template>
  <div class="app-container">
    <!-- 顶部导航 -->
    <header class="header">
      <div class="header-content">
        <div class="logo" @click="$router.push('/')">
          <el-icon :size="28" color="#409eff"><ShoppingCart /></el-icon>
          <span class="logo-text">简易商城</span>
        </div>
        <div class="nav-links">
          <el-button type="primary" link @click="$router.push('/')">
            <el-icon><HomeFilled /></el-icon> 首页
          </el-button>
          <el-button type="primary" link @click="$router.push('/cart')">
            <el-icon><ShoppingCart /></el-icon> 购物车
            <el-badge v-if="cartCount > 0" :value="cartCount" class="cart-badge" />
          </el-button>
          <el-button type="primary" link @click="$router.push('/order')">
            <el-icon><Document /></el-icon> 我的订单
          </el-button>
        </div>
      </div>
    </header>

    <!-- 主内容区 -->
    <main class="main-content">
      <router-view />
    </main>

    <!-- 底部 -->
    <footer class="footer">
      <p>© 2026 简易商城 - 毕业设计演示项目</p>
    </footer>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useCartStore } from './store/cart'

const cartStore = useCartStore()
const cartCount = computed(() => cartStore.totalCount)
</script>

<style>
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

body {
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif;
  background-color: #f5f7fa;
  color: #303133;
}

.app-container {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

.header {
  background: #fff;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  position: sticky;
  top: 0;
  z-index: 100;
}

.header-content {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 24px;
  height: 64px;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.logo {
  display: flex;
  align-items: center;
  gap: 10px;
  cursor: pointer;
}

.logo-text {
  font-size: 22px;
  font-weight: 700;
  background: linear-gradient(135deg, #409eff, #67c23a);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.nav-links {
  display: flex;
  gap: 8px;
  align-items: center;
}

.cart-badge {
  margin-left: 4px;
}

.main-content {
  flex: 1;
  max-width: 1200px;
  width: 100%;
  margin: 0 auto;
  padding: 24px;
}

.footer {
  background: #fff;
  padding: 20px;
  text-align: center;
  color: #909399;
  font-size: 14px;
  border-top: 1px solid #ebeef5;
}
</style>
