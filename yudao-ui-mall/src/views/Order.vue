<template>
  <div class="order-page">
    <h2 class="page-title">
      <el-icon><Document /></el-icon>
      我的订单
    </h2>

    <!-- 状态筛选 -->
    <div class="filter-tabs">
      <el-tabs v-model="activeStatus" @tab-change="handleTabChange">
        <el-tab-pane label="全部订单" name="all" />
        <el-tab-pane label="待付款" name="0" />
        <el-tab-pane label="待发货" name="1" />
        <el-tab-pane label="已完成" name="2" />
        <el-tab-pane label="已取消" name="3" />
      </el-tabs>
    </div>

    <div class="order-list" v-loading="loading">
      <div
        v-for="order in orders"
        :key="order.id"
        class="order-card"
      >
        <!-- 订单头部 -->
        <div class="order-header">
          <div class="header-left">
            <span class="order-time">{{ formatTime(order.createTime) }}</span>
            <span class="order-no">订单号：{{ order.orderNo }}</span>
          </div>
          <div class="header-right">
            <el-tag :type="getStatusType(order.status)" effect="light">
              {{ getStatusText(order.status) }}
            </el-tag>
          </div>
        </div>

        <!-- 订单内容 -->
        <div class="order-body">
          <div class="order-items">
            <div class="order-item" v-for="item in order.items" :key="item.id">
              <div class="item-image">
                <el-image
                  :src="item.productCoverUrl"
                  fit="cover"
                  class="img-thumb"
                >
                  <template #error>
                    <div class="img-placeholder">
                      <el-icon><Picture /></el-icon>
                    </div>
                  </template>
                </el-image>
              </div>
              <div class="item-info">
                <div class="item-name">{{ item.productName }}</div>
                <div class="item-price">¥{{ item.price }} × {{ item.count }}</div>
              </div>
              <div class="item-subtotal">¥{{ item.totalAmount }}</div>
            </div>
          </div>
          <div class="order-summary">
            <div class="summary-row">
              <span>共 {{ order.itemCount }} 件商品</span>
            </div>
            <div class="summary-total">
              <span>实付：</span>
              <span class="total-amount">¥{{ order.totalAmount }}</span>
            </div>
          </div>
        </div>

        <!-- 订单操作 -->
        <div class="order-footer">
          <el-button
            v-if="order.status === 0"
            type="primary"
            size="small"
            @click="handlePay(order)"
          >
            立即付款
          </el-button>
          <el-button
            v-if="order.status === 0 || order.status === 1"
            size="small"
            @click="handleCancel(order.id)"
          >
            取消订单
          </el-button>
          <el-button
            v-if="order.status === 1"
            type="success"
            size="small"
            @click="handleConfirm(order.id)"
          >
            确认收货
          </el-button>
          <el-button
            v-if="order.status === 2 || order.status === 3"
            size="small"
            type="danger"
            text
            @click="handleDelete(order.id)"
          >
            删除订单
          </el-button>
          <el-button
            type="primary"
            size="small"
            text
            @click="viewDetail(order)"
          >
            查看详情
          </el-button>
        </div>
      </div>

      <el-empty v-if="!loading && orders.length === 0" description="暂无订单" />
    </div>

    <!-- 分页 -->
    <div class="pagination" v-if="total > pageSize">
      <el-pagination
        v-model:current-page="pageNo"
        v-model:page-size="pageSize"
        :total="total"
        layout="prev, pager, next"
        @current-change="fetchOrders"
      />
    </div>

    <!-- 订单详情弹窗 -->
    <el-dialog v-model="detailVisible" title="订单详情" width="600px">
      <div v-if="currentOrder" class="order-detail">
        <div class="detail-row">
          <span class="label">订单号：</span>
          <span class="value">{{ currentOrder.orderNo }}</span>
        </div>
        <div class="detail-row">
          <span class="label">订单状态：</span>
          <el-tag :type="getStatusType(currentOrder.status)" effect="light">
            {{ getStatusText(currentOrder.status) }}
          </el-tag>
        </div>
        <div class="detail-row">
          <span class="label">下单时间：</span>
          <span class="value">{{ formatTime(currentOrder.createTime) }}</span>
        </div>
        <div class="detail-row">
          <span class="label">订单金额：</span>
          <span class="value price">¥{{ currentOrder.totalAmount }}</span>
        </div>
        <div class="detail-section">
          <h4>商品明细</h4>
          <div
            v-for="item in currentOrder.items"
            :key="item.id"
            class="detail-item"
          >
            <span>{{ item.productName }}</span>
            <span>¥{{ item.price }} × {{ item.count }}</span>
          </div>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getOrderPage, getOrderDetail, cancelOrder, payOrder, confirmReceipt, deleteOrder } from '../api/mall'

const activeStatus = ref('all')
const orders = ref([])
const total = ref(0)
const loading = ref(false)
const pageNo = ref(1)
const pageSize = ref(10)
const detailVisible = ref(false)
const currentOrder = ref(null)

const statusMap = {
  0: { text: '待付款', type: 'warning' },
  1: { text: '待发货', type: 'primary' },
  2: { text: '已完成', type: 'success' },
  3: { text: '已取消', type: 'info' }
}

const getStatusText = (status) => statusMap[status]?.text || '未知'
const getStatusType = (status) => statusMap[status]?.type || 'info'

const formatTime = (time) => {
  if (!time) return '-'
  const d = new Date(time)
  return d.toLocaleString('zh-CN')
}

const fetchOrders = async () => {
  loading.value = true
  try {
    const params = {
      pageNo: pageNo.value,
      pageSize: pageSize.value
    }
    if (activeStatus.value !== 'all') {
      params.status = parseInt(activeStatus.value)
    }
    const data = await getOrderPage(params)
    orders.value = data.list.map(order => ({
      ...order,
      items: order.items || [],
      itemCount: (order.items || []).length
    }))
    total.value = data.total
  } catch (e) {
    console.error('获取订单失败:', e)
  } finally {
    loading.value = false
  }
}

const handleTabChange = () => {
  pageNo.value = 1
  fetchOrders()
}

const handlePay = async (order) => {
  try {
    await ElMessageBox.confirm(`确认支付订单 ¥${order.totalAmount} 吗？`, '支付确认', {
      type: 'info'
    })
    await payOrder(order.id)
    ElMessage.success('支付成功！')
    fetchOrders()
  } catch (e) {
    // 用户取消或支付失败
  }
}

const handleCancel = async (id) => {
  try {
    await ElMessageBox.confirm('确定要取消该订单吗？', '提示', {
      type: 'warning'
    })
    await cancelOrder(id)
    ElMessage.success('订单已取消')
    fetchOrders()
  } catch (e) {
    // 用户取消
  }
}

const handleConfirm = async (id) => {
  try {
    await ElMessageBox.confirm('确认收货吗？', '提示', {
      type: 'success'
    })
    await confirmReceipt(id)
    ElMessage.success('已确认收货')
    fetchOrders()
  } catch (e) {
    // 用户取消或确认失败
  }
}

const handleDelete = async (id) => {
  try {
    await ElMessageBox.confirm('删除后订单将从您的列表中移除，管理员仍可查看。确定删除吗？', '删除订单', {
      type: 'warning',
      confirmButtonText: '确定删除',
      cancelButtonText: '取消'
    })
    await deleteOrder(id)
    ElMessage.success('订单已删除')
    fetchOrders()
  } catch (e) {
    // 用户取消
  }
}

const viewDetail = async (order) => {
  try {
    const detail = await getOrderDetail(order.id)
    currentOrder.value = detail
    detailVisible.value = true
  } catch (e) {
    ElMessage.error('获取详情失败')
  }
}

onMounted(() => {
  fetchOrders()
})
</script>

<style scoped>
.order-page {
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

.filter-tabs {
  margin-bottom: 20px;
  border-bottom: 1px solid #ebeef5;
}

.order-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
  min-height: 300px;
}

.order-card {
  border: 1px solid #ebeef5;
  border-radius: 10px;
  overflow: hidden;
  transition: box-shadow 0.2s;
}

.order-card:hover {
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
}

.order-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 20px;
  background: #f5f7fa;
}

.header-left {
  display: flex;
  gap: 20px;
  font-size: 13px;
  color: #909399;
}

.order-body {
  display: flex;
  padding: 20px;
}

.order-items {
  flex: 1;
}

.order-item {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 8px 0;
}

.item-image {
  width: 60px;
  height: 60px;
  border-radius: 6px;
  overflow: hidden;
  background: #f5f7fa;
  flex-shrink: 0;
}

.img-thumb {
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

.item-info {
  flex: 1;
}

.item-name {
  font-size: 14px;
  color: #303133;
  margin-bottom: 4px;
}

.item-price {
  font-size: 13px;
  color: #909399;
}

.item-subtotal {
  font-size: 14px;
  color: #f56c6c;
  font-weight: 500;
}

.order-summary {
  width: 160px;
  text-align: right;
  display: flex;
  flex-direction: column;
  justify-content: center;
  gap: 8px;
  border-left: 1px solid #ebeef5;
  padding-left: 20px;
  margin-left: 20px;
}

.summary-row {
  font-size: 13px;
  color: #606266;
}

.summary-total {
  font-size: 14px;
  color: #303133;
}

.total-amount {
  color: #f56c6c;
  font-size: 20px;
  font-weight: 700;
}

.order-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  padding: 12px 20px;
  border-top: 1px solid #ebeef5;
  background: #fafafa;
}

.pagination {
  display: flex;
  justify-content: center;
  margin-top: 24px;
}

.order-detail {
  padding: 10px 0;
}

.detail-row {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 8px 0;
  font-size: 14px;
}

.detail-row .label {
  color: #909399;
  width: 80px;
  flex-shrink: 0;
}

.detail-row .value {
  color: #303133;
}

.detail-row .price {
  color: #f56c6c;
  font-weight: 600;
  font-size: 16px;
}

.detail-section {
  margin-top: 16px;
  padding-top: 16px;
  border-top: 1px solid #ebeef5;
}

.detail-section h4 {
  margin-bottom: 12px;
  font-size: 15px;
}

.detail-item {
  display: flex;
  justify-content: space-between;
  padding: 6px 0;
  font-size: 13px;
  color: #606266;
}
</style>
