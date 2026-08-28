<template>
  <div class="detail-page" v-loading="loading">
    <el-page-header @back="goBack" content="商品详情" class="page-header" />

    <div class="product-container" v-if="product">
      <!-- 左侧图片 -->
      <div class="product-gallery">
        <div class="main-image">
          <el-image
            :src="product.coverUrl"
            fit="cover"
            class="img-large"
          >
            <template #error>
              <div class="image-placeholder">
                <el-icon :size="80"><Picture /></el-icon>
              </div>
            </template>
          </el-image>
        </div>
      </div>

      <!-- 右侧信息 -->
      <div class="product-info">
        <h1 class="product-title">{{ product.name }}</h1>

        <div class="price-section">
          <span class="price-label">售价</span>
          <span class="price-symbol">¥</span>
          <span class="price-value">{{ product.price }}</span>
        </div>

        <div class="info-list">
          <div class="info-item">
            <span class="label">库存</span>
            <span class="value">{{ product.stock }} 件</span>
          </div>
          <div class="info-item">
            <span class="label">销量</span>
            <span class="value">{{ product.sales || 0 }} 件</span>
          </div>
        </div>

        <div class="quantity-section">
          <span class="label">购买数量</span>
          <el-input-number
            v-model="quantity"
            :min="1"
            :max="product.stock"
            size="large"
          />
        </div>

        <div class="action-buttons">
          <el-button
            type="primary"
            size="large"
            class="btn-cart"
            :disabled="product.stock <= 0"
            @click="handleAddCart"
          >
            <el-icon><ShoppingCart /></el-icon>
            加入购物车
          </el-button>
          <el-button
            type="danger"
            size="large"
            class="btn-buy"
            :disabled="product.stock <= 0"
            @click="handleBuyNow"
          >
            立即购买
          </el-button>
        </div>

        <div class="description-section">
          <h3 class="desc-title">
            <el-icon><Document /></el-icon>
            商品描述
          </h3>
          <div class="desc-content">
            {{ product.description || '暂无描述' }}
          </div>
        </div>
      </div>
    </div>

    <el-empty v-else-if="!loading" description="商品不存在或已下架" />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getProductDetail, createOrder } from '../api/mall'
import { useCartStore } from '../store/cart'

const route = useRoute()
const router = useRouter()
const cartStore = useCartStore()

const product = ref(null)
const loading = ref(false)
const quantity = ref(1)

const fetchDetail = async () => {
  loading.value = true
  try {
    const data = await getProductDetail(route.params.id)
    product.value = data
  } catch (e) {
    console.error('获取商品详情失败:', e)
  } finally {
    loading.value = false
  }
}

const goBack = () => {
  router.push('/')
}

const handleAddCart = async () => {
  const success = await cartStore.addToCart(product.value.id, quantity.value)
  if (success) {
    ElMessage.success('已加入购物车')
  }
}

const handleBuyNow = async () => {
  try {
    const orderId = await createOrder({
      items: [{ productId: product.value.id, count: quantity.value }]
    })
    ElMessage.success('下单成功！')
    router.push('/order')
  } catch (e) {
    ElMessage.error(e.message || '下单失败')
  }
}

onMounted(() => {
  fetchDetail()
})
</script>

<style scoped>
.detail-page {
  background: #fff;
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06);
}

.page-header {
  margin-bottom: 24px;
  padding-bottom: 16px;
  border-bottom: 1px solid #ebeef5;
}

.product-container {
  display: flex;
  gap: 40px;
}

.product-gallery {
  width: 400px;
  flex-shrink: 0;
}

.main-image {
  width: 100%;
  aspect-ratio: 1;
  border-radius: 12px;
  overflow: hidden;
  background: #f5f7fa;
}

.img-large {
  width: 100%;
  height: 100%;
}

.image-placeholder {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #c0c4cc;
}

.product-info {
  flex: 1;
}

.product-title {
  font-size: 24px;
  font-weight: 600;
  margin-bottom: 16px;
  color: #303133;
}

.price-section {
  background: linear-gradient(135deg, #fff0f0, #ffe8e8);
  padding: 20px 24px;
  border-radius: 10px;
  margin-bottom: 24px;
  display: flex;
  align-items: baseline;
  gap: 10px;
}

.price-label {
  font-size: 14px;
  color: #909399;
}

.price-symbol {
  font-size: 18px;
  color: #f56c6c;
  font-weight: 600;
}

.price-value {
  font-size: 36px;
  font-weight: 700;
  color: #f56c6c;
}

.info-list {
  display: flex;
  gap: 40px;
  margin-bottom: 24px;
  padding-bottom: 20px;
  border-bottom: 1px solid #ebeef5;
}

.info-item {
  display: flex;
  align-items: center;
  gap: 8px;
}

.info-item .label {
  color: #909399;
  font-size: 14px;
}

.info-item .value {
  color: #303133;
  font-size: 14px;
  font-weight: 500;
}

.quantity-section {
  display: flex;
  align-items: center;
  gap: 16px;
  margin-bottom: 28px;
}

.quantity-section .label {
  color: #606266;
  font-size: 14px;
}

.action-buttons {
  display: flex;
  gap: 16px;
  margin-bottom: 32px;
}

.btn-cart {
  min-width: 160px;
}

.btn-buy {
  min-width: 160px;
}

.description-section {
  border-top: 1px solid #ebeef5;
  padding-top: 20px;
}

.desc-title {
  font-size: 16px;
  font-weight: 600;
  margin-bottom: 12px;
  display: flex;
  align-items: center;
  gap: 8px;
}

.desc-content {
  color: #606266;
  line-height: 1.8;
  font-size: 14px;
}
</style>
