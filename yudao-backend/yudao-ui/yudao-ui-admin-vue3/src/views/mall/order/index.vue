<template>
  <ContentWrap>
    <el-form :model="queryParams" ref="queryFormRef" :inline="true" label-width="68px">
      <el-form-item label="订单号" prop="orderNo">
        <el-input v-model="queryParams.orderNo" placeholder="请输入订单号" clearable @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="订单状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="请选择状态" clearable>
          <el-option label="待付款" :value="0" />
          <el-option label="已完成" :value="1" />
          <el-option label="已取消" :value="2" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button @click="handleQuery"><Icon icon="ep:search" class="mr-5px" /> 搜索</el-button>
        <el-button @click="resetQuery"><Icon icon="ep:refresh" class="mr-5px" /> 重置</el-button>
      </el-form-item>
    </el-form>
  </ContentWrap>

  <ContentWrap>
    <el-table v-loading="loading" :data="list">
      <el-table-column prop="orderNo" label="订单号" min-width="180" />
      <el-table-column prop="userId" label="用户ID" width="100" align="center" />
      <el-table-column label="订单金额" width="120" align="center">
        <template #default="scope">
          <span style="color: #f56c6c; font-weight: bold">¥{{ scope.row.totalAmount }}</span>
        </template>
      </el-table-column>
      <el-table-column label="订单状态" width="100" align="center">
        <template #default="scope">
          <el-tag v-if="scope.row.status === 0" type="warning">待付款</el-tag>
          <el-tag v-else-if="scope.row.status === 1" type="success">已完成</el-tag>
          <el-tag v-else-if="scope.row.status === 2" type="info">已取消</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="下单时间" width="180" align="center">
        <template #default="scope">
          <span>{{ formatDate(scope.row.createTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" width="200" fixed="right">
        <template #default="scope">
          <el-button link type="primary" @click="openDetail(scope.row.id)" v-hasPermi="['mall:order:query']">详情</el-button>
          <el-button v-if="scope.row.status === 0" link type="danger" @click="handleCancel(scope.row.id)" v-hasPermi="['mall:order:update']">取消订单</el-button>
        </template>
      </el-table-column>
    </el-table>
    <Pagination :total="total" v-model:page="queryParams.pageNo" v-model:limit="queryParams.pageSize" @pagination="getList" />
  </ContentWrap>

  <OrderDetail ref="detailRef" />
</template>

<script lang="ts" setup>
import { formatDate } from '@/utils/formatTime'
import * as OrderApi from '@/api/mall/order'
import OrderDetail from './OrderDetail.vue'

defineOptions({ name: 'MallOrder' })

const message = useMessage()
const { t } = useI18n()

const loading = ref(true)
const total = ref(0)
const list = ref([])
const queryParams = reactive({
  pageNo: 1,
  pageSize: 10,
  orderNo: undefined,
  status: undefined
})
const queryFormRef = ref()

const getList = async () => {
  loading.value = true
  try {
    const data = await OrderApi.getOrderPage(queryParams)
    list.value = data.list
    total.value = data.total
  } finally {
    loading.value = false
  }
}

const handleQuery = () => {
  queryParams.pageNo = 1
  getList()
}

const resetQuery = () => {
  queryFormRef.value.resetFields()
  handleQuery()
}

const detailRef = ref()
const openDetail = (id: number) => {
  detailRef.value.open(id)
}

const handleCancel = async (id: number) => {
  try {
    await message.confirm('确定要取消该订单吗？')
    await OrderApi.updateOrderStatus({ id: id, status: 2 })
    message.success('取消成功')
    await getList()
  } catch {}
}

onMounted(() => {
  getList()
})
</script>
