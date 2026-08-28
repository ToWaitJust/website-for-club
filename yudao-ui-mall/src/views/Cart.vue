<template>
  <div class="cart-page">
    <h2 class="page-title">
      <el-icon><ShoppingCart /></el-icon>
      我的购物车
    </h2>

    <div class="cart-container" v-loading="cartStore.loading">
      <!-- 购物车列表 -->
      <template v-if="cartStore.list.length > 0">
        <div class="cart-header">
          <el-checkbox
            :model-value="isAllSelected"
            @change="handleSelectAll"
          >
            全选
          </el-checkbox>
          <span class="header-name">商品</span>
          <span class="header-price">单价</span>
          <span class="header-quantity">数量</span>
          <span class="header-subtotal">小计</span>
          <span class="header-action">操作</span>
        </div>

        <div class="cart-list">
          <div
            v-for="item in cartStore.list"
            :key="item.id"
            class="cart-item"
          >
            <el-checkbox
              :model-value="item.selected"
              @change="cartStore.toggleSelect(item.id)"
            />
            <div class="item-image" @click="goDetail(item.productId)">
              <el-image
                :src="item.productCoverUrl"
                fit="cover"
                class="img-small"
              >
                <template #error>
                  <div class="img-placeholder">
                    <el-icon><Picture /></el-icon>
                  </div>
                </template>
              </el-image>
            </div>
            <div class="item-name" @click="goDetail(item.productId)">
              {{ item.productName }}
            </div>
            <div class="item-price">¥{{ item.price }}</div>
            <div class="item-quantity">
              <el-input-number
                v-model="item.count"
                :min="1"
                :max="item.stock || 99"
                size="small"
                @change="handleCountChange(item)"
              />
            </div>
            <div class="item-subtotal">
              ¥{{ (item.price * item.count).toFixed(2) }}
            </div>
            <div class="item-action">
              <el-button type="danger" link @click="handleDelete(item.id)">
                删除
              </el-button>
            </div>
          </div>
        </div>

        <!-- 结算栏 -->
        <div class="checkout-bar">
          <div class="checkout-left">
            <el-checkbox
              :model-value="isAllSelected"
              @change="handleSelectAll"
            >
              全选
            </el-checkbox>
            <el-button type="text" @click="clearSelected">
              删除选中
            </el-button>
          </div>
          <div class="checkout-right">
            <span class="selected-tip">
              已选 <em>{{ selectedCount }}</em> 件商品
            </span>
            <span class="total-tip">
              合计：<span class="total-price">¥{{ selectedTotal.toFixed(2) }}</span>
            </span>
            <el-button
              type="danger"
              size="large"
              :disabled="selectedCount === 0"
              @click="handleCheckout"
            >
              去结算
            </el-button>
          </div>
        </div>
      </template>

      <!-- 空购物车 -->
      <el-empty v-else description="购物车空空如亦~">
        <el-button type="primary" @click="$router.push('/')">
          去逛逛
        </el-button>
      </el-empty>
    </div>
  </div>
</template>

<script setup>
import { computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useCartStore } from '../store/cart'
import { createOrder } from '../api/mall'

const router = useRouter()
const cartStore = useCartStore()

const isAllSelected = computed(() => {
  return cartStore.list.length > 0 && cartStore.list.every(item => item.selected)
})

const selectedCount = computed(() => {
  return cartStore.selectedItems.reduce((sum, item) => sum + item.count, 0)
})

const selectedTotal = computed(() => {
  return cartStore.selectedItems.reduce((sum, item) => sum + item.price * item.count, 0)
})

const handleSelectAll = (val) => {
  cartStore.toggleSelectAll(val)
}

const handleCountChange = (item) => {
  cartStore.updateCount(item.id, item.count)
}

const handleDelete = async (id) => {
  try {
    await ElMessageBox.confirm('确定要删除该商品吗？', '提示', {
      type: 'warning'
    })
    await cartStore.removeItem(id)
    ElMessage.success('删除成功')
  } catch (e) {
    // 用户取消
  }
}

const clearSelected = async () => {
  if (cartStore.selectedItems.length === 0) {
    ElMessage.warning('请先选择商品')
    return
  }
  try {
    await ElMessageBox.confirm('确定删除选中的商品吗？', '提示', {
      type: 'warning'
    })
    for (const item of cartStore.selectedItems) {
      await cartStore.removeItem(item.id)
    }
    ElMessage.success('删除成功')
  } catch (e) {
    // 用户取消
  }
}

const goDetail = (id) => {
  router.push(`/product/${id}`)
}

const handleCheckout = async () => {
  if (cartStore.selectedItems.length === 0) {
    ElMessage.warning('请先选择商品')
    return
  }
  try {
    const items = cartStore.selectedItems.map(item => ({
      productId: item.productId,
      count: item.count
    }))
    const orderId = await createOrder({ items })
    // 移除已结算的商品
    for (const item of cartStore.selectedItems) {
      await cartStore.removeItem(item.id)
    }
    ElMessage.success('下单成功！')
    router.push('/order')
  } catch (e) {
    ElMessage.error(e.message || '下单失败')
  }
}

onMounted(() => {
  cartStore.fetchCart()
})
</script>

<style scoped>
.cart-page {
  background: #fff;
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06);
}

.page-title {
  font-size: 22px;
  font-weight: 600;
  margin-bottom: 20px;
  display: flex;
  align-items: center;
  gap: 10px;
}

.cart-container {
  min-height: 400px;
}

.cart-header {
  display: flex;
  align-items: center;
  padding: 16px 20px;
  background: #f5f7fa;
  border-radius: 8px;
  font-size: 14px;
  color: #606266;
  font-weight: 500;
}

.header-name {
  flex: 1;
  margin-left: 40px;
}

.header-price {
  width: 120px;
  text-align: center;
}

.header-quantity {
  width: 140px;
  text-align: center;
}

.header-subtotal {
  width: 120px;
  text-align: center;
}

.header-action {
  width: 80px;
  text-align: center;
}

.cart-list {
  padding: 10px 0;
}

.cart-item {
  display: flex;
  align-items: center;
  padding: 16px 20px;
  border-bottom: 1px solid #ebeef5;
  transition: background 0.2s;
}

.cart-item:hover {
  background: #fafafa;
}

.item-image {
  width: 80px;
  height: 80px;
  border-radius: 8px;
  overflow: hidden;
  background: #f5f7fa;
  margin: 0 16px;
  cursor: pointer;
  flex-shrink: 0;
}

.img-small {
  width: 100%;
  height: 100%;
}

.img-placeholder {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #c0c4cc;
}

.item-name {
  flex: 1;
  font-size: 14px;
  color: #303133;
  cursor: pointer;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.item-name:hover {
  color: #409eff;
}

.item-price {
  width: 120px;
  text-align: center;
  color: #606266;
  font-size: 14px;
}

.item-quantity {
  width: 140px;
  display: flex;
  justify-content: center;
}

.item-subtotal {
  width: 120px;
  text-align: center;
  color: #f56c6c;
  font-weight: 600;
  font-size: 16px;
}

.item-action {
  width: 80px;
  text-align: center;
}

.checkout-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 20px;
  background: #f5f7fa;
  border-radius: 8px;
  margin-top: 16px;
}

.checkout-left {
  display: flex;
  align-items: center;
  gap: 16px;
}

.checkout-right {
  display: flex;
  align-items: center;
  gap: 20px;
}

.selected-tip {
  font-size: 14px;
  color: #606266;
}

.selected-tip em {
  color: #409eff;
  font-style: normal;
  font-weight: 600;
  margin: 0 4px;
}

.total-tip {
  font-size: 14px;
  color: #606266;
}

.total-price {
  color: #f56c6c;
  font-size: 22px;
  font-weight: 700;
  margin-left: 4px;
}
</style>
