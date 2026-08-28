<template>
  <div class="not-found-page">
    <div class="content">
      <div class="error-code">404</div>
      <p class="error-text">页面走丢了</p>
      <el-button type="primary" @click="$router.push('/')">返回首页</el-button>
    </div>
  </div>
</template>

<script setup>
</script>

<style scoped>
.not-found-page {
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: 70vh;
}

.content {
  text-align: center;
}

.error-code {
  font-size: 120px;
  font-weight: 800;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  line-height: 1;
}

.error-text {
  font-size: 20px;
  color: #909399;
  margin: 16px 0 32px;
}
</style>
