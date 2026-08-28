<template>
  <Dialog v-model="dialogVisible" title="订单详情" width="800px">
    <el-descriptions :column="2" border v-loading="loading">
      <el-descriptions-item label="订单号">{{ orderData.orderNo }}</el-descriptions-item>
      <el-descriptions-item label="用户ID">{{ orderData.userId }}</el-descriptions-item>
      <el-descriptions-item label="订单金额">
        <span style="color: #f56c6c; font-weight: bold">¥{{ orderData.totalAmount }}</span>
      </el-descriptions-item>
      <el-descriptions-item label="订单状态">
        <el-tag v-if="orderData.status === 0" type="warning">待付款</el-tag>
        <el-tag v-else-if="orderData.status === 1" type="success">已完成</el-tag>
        <el-tag v-else-if="orderData.status === 2" type="info">已取消</el-tag>
      </el-descriptions-item>
      <el-descriptions-item label="下单时间">{{ orderData.createTime ? formatDate(orderData.createTime) : '-' }}</el-descriptions-item>
      <el-descriptions-item label="付款时间">{{ orderData.payTime ? formatDate(orderData.payTime) : '-' }}</el-descriptions-item>
      <el-descriptions-item label="取消时间">{{ orderData.cancelTime ? formatDate(orderData.cancelTime) : '-' }}</el-descriptions-item>
      <el-descriptions-item label="取消原因">{{ orderData.cancelReason || '-' }}</el-descriptions-item>
    </el-descriptions>

    <el-divider content-position="left">订单明细</el-divider>

    <el-table :data="orderData.items" border style="width: 100%">
      <el-table-column label="商品图" width="80" align="center">
        <template #default="scope">
          <el-image v-if="scope.row.productCoverUrl" :src="scope.row.productCoverUrl" style="width: 40px; height: 40px" fit="cover" />
          <span v-else>-</span>
        </template>
      </el-table-column>
      <el-table-column prop="productName" label="商品名称" min-width="150" show-overflow-tooltip />
      <el-table-column label="单价" width="100" align="center">
        <template #default="scope">
          <span>¥{{ scope.row.price }}</span>
        </template>
      </el-table-column>
      <el-table-column prop="count" label="数量" width="80" align="center" />
      <el-table-column label="小计" width="120" align="center">
        <template #default="scope">
          <span style="color: #f56c6c">¥{{ scope.row.totalAmount }}</span>
        </template>
      </el-table-column>
    </el-table>

    <template #footer>
      <el-button @click="dialogVisible = false">关 闭</el-button>
    </template>
  </Dialog>
</template>

<script lang="ts" setup>
import { formatDate } from '@/utils/formatTime'
import * as OrderApi from '@/api/mall/order'

defineOptions({ name: 'MallOrderDetail' })

const dialogVisible = ref(false)
const loading = ref(false)
const orderData = ref<any>({})

const open = async (id: number) => {
  dialogVisible.value = true
  loading.value = true
  try {
    orderData.value = await OrderApi.getOrder(id)
  } finally {
    loading.value = false
  }
}
defineExpose({ open })
</script>
