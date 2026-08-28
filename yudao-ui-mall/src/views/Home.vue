<template>
  <div class="home-page">
    <!-- Banner 区域 -->
    <div class="banner">
      <div class="banner-content">
        <h1>欢迎来到简易商城</h1>
        <p>精选好物，品质生活，从这里开始</p>
      </div>
    </div>

    <div class="content-wrapper">
      <!-- 左侧分类 -->
      <aside class="sidebar">
        <h3 class="sidebar-title">
          <el-icon><Menu /></el-icon>
          商品分类
        </h3>
        <div class="category-list">
          <div
            class="category-item"
            :class="{ active: activeCategory === null }"
            @click="selectCategory(null)"
          >
            <el-icon><Grid /></el-icon>
            全部商品
          </div>
          <div
            v-for="cat in categories"
            :key="cat.id"
            class="category-item"
            :class="{ active: activeCategory === cat.id }"
            @click="selectCategory(cat.id)"
          >
            <el-icon><Goods /></el-icon>
            {{ cat.name }}
          </div>
        </div>
      </aside>

      <!-- 右侧商品列表 -->
      <section class="main-section">
        <div class="section-header">
 <h2>{{ currentCategoryName }}</h2>
 <el-input
   v-model="searchKeyword"
   placeholder="搜索商品名称"
   clearable
   :prefix-icon="Search"
   style="width: 240px"
   @keyup.enter="handleSearch"
   @clear="handleSearch"
 />
 <span class="product-count">共 {{ total }} 件商品</span>
 </div>

        <el-row :gutter="20" v-loading="loading">
          <el-col
            v-for="product in products"
            :key="product.id"
            :xs="12"
            :sm="8"
            :md="6"
            :lg="6"
          >
            <div class="product-card" @click="goDetail(product.id)">
              <div class="product-image">
                <el-image
                  :src="product.coverUrl || placeholderImg"
                  fit="cover"
                  class="product-img"
                >
                  <template #error>
                    <div class="image-placeholder">
                      <el-icon :size="48"><Picture /></el-icon>
                    </div>
                  </template>
                </el-image>
              </div>
              <div class="product-info">
                <div class="product-name">{{ product.name }}</div>
                <div class="product-price">
                  <span class="price-symbol">¥</span>
                  <span class="price-value">{{ product.price }}</span>
                </div>
                <div class="product-bottom">
                  <span class="stock">库存: {{ product.stock }}</span>
                  <el-button
                    type="primary"
                    size="small"
                    @click.stop="handleAddCart(product)"
                  >
                    加入购物车
                  </el-button>
                </div>
              </div>
            </div>
          </el-col>
        </el-row>

        <!-- 空状态 -->
        <el-empty v-if="!loading && products.length === 0" description="暂无商品" />

        <!-- 分页 -->
        <div class="pagination" v-if="total > pageSize">
          <el-pagination
            v-model:current-page="pageNo"
            v-model:page-size="pageSize"
            :total="total"
            layout="prev, pager, next"
            @current-change="fetchProducts"
          />
        </div>
      </section>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Search } from '@element-plus/icons-vue'
import { getCategoryList, getProductPage } from '../api/mall'
import { useCartStore } from '../store/cart'

const router = useRouter()
const cartStore = useCartStore()

const categories = ref([])
const products = ref([])
const total = ref(0)
const loading = ref(false)
const activeCategory = ref(null)
const pageNo = ref(1)
const pageSize = ref(12)
const placeholderImg = ''
const searchKeyword = ref('')

const currentCategoryName = computed(() => {
  if (!activeCategory.value) return '全部商品'
  const cat = categories.value.find(c => c.id === activeCategory.value)
  return cat ? cat.name : '全部商品'
})

const fetchCategories = async () => {
  try {
    const data = await getCategoryList()
    categories.value = data
  } catch (e) {
    console.error('获取分类失败:', e)
  }
}

const fetchProducts = async () => {
  loading.value = true
  try {
    const params = {
      pageNo: pageNo.value,
      pageSize: pageSize.value
    }
    if (activeCategory.value) {
      params.categoryId = activeCategory.value
    }
    if (searchKeyword.value.trim()) {
      params.name = searchKeyword.value.trim()
    }
    const data = await getProductPage(params)
    products.value = data.list
    total.value = data.total
  } catch (e) {
    console.error('获取商品失败:', e)
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  pageNo.value = 1
  fetchProducts()
}

const selectCategory = (id) => {
  activeCategory.value = id
  pageNo.value = 1
  fetchProducts()
}

const goDetail = (id) => {
  router.push(`/product/${id}`)
}

const handleAddCart = async (product) => {
  if (product.stock <= 0) {
    ElMessage.warning('库存不足')
    return
  }
  const success = await cartStore.addToCart(product.id, 1)
  if (success) {
    ElMessage.success('已加入购物车')
  }
}

onMounted(() => {
  fetchCategories()
  fetchProducts()
})
</script>

<style scoped>
.home-page {
  width: 100%;
}

.banner {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 12px;
  padding: 48px 36px;
  margin-bottom: 24px;
  color: #fff;
}

.banner-content h1 {
  font-size: 32px;
  margin-bottom: 12px;
}

.banner-content p {
  font-size: 16px;
  opacity: 0.9;
}

.content-wrapper {
  display: flex;
  gap: 24px;
}

.sidebar {
  width: 220px;
  flex-shrink: 0;
  background: #fff;
  border-radius: 12px;
  padding: 20px;
  height: fit-content;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06);
}

.sidebar-title {
  font-size: 16px;
  font-weight: 600;
  margin-bottom: 16px;
  display: flex;
  align-items: center;
  gap: 8px;
  color: #303133;
}

.category-list {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.category-item {
  padding: 10px 14px;
  border-radius: 8px;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 14px;
  color: #606266;
  transition: all 0.2s;
}

.category-item:hover {
  background: #ecf5ff;
  color: #409eff;
}

.category-item.active {
  background: #409eff;
  color: #fff;
}

.main-section {
  flex: 1;
  background: #fff;
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06);
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding-bottom: 16px;
  border-bottom: 1px solid #ebeef5;
}

.section-header h2 {
  font-size: 20px;
  font-weight: 600;
}

.product-count {
  color: #909399;
  font-size: 14px;
}

.product-card {
  background: #fff;
  border: 1px solid #ebeef5;
  border-radius: 10px;
  overflow: hidden;
  margin-bottom: 20px;
  cursor: pointer;
  transition: all 0.3s;
}

.product-card:hover {
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.12);
  transform: translateY(-2px);
}

.product-image {
  width: 100%;
  aspect-ratio: 1;
  background: #f5f7fa;
  overflow: hidden;
}

.product-img {
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
  padding: 14px;
}

.product-name {
  font-size: 14px;
  color: #303133;
  margin-bottom: 10px;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  height: 40px;
  line-height: 20px;
}

.product-price {
  margin-bottom: 12px;
}

.price-symbol {
  font-size: 14px;
  color: #f56c6c;
}

.price-value {
  font-size: 22px;
  font-weight: 700;
  color: #f56c6c;
}

.product-bottom {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.stock {
  font-size: 12px;
  color: #909399;
}

.pagination {
  display: flex;
  justify-content: center;
  margin-top: 24px;
}
</style>
