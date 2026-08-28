<template>
  <ContentWrap>
    <el-form :model="queryParams" ref="queryFormRef" :inline="true" label-width="68px">
      <el-form-item label="商品名称" prop="name">
        <el-input v-model="queryParams.name" placeholder="请输入商品名称" clearable @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="商品分类" prop="categoryId">
        <el-select v-model="queryParams.categoryId" placeholder="请选择分类" clearable>
          <el-option v-for="item in categoryList" :key="item.id" :label="item.name" :value="item.id" />
        </el-select>
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="请选择状态" clearable>
          <el-option label="上架" :value="0" />
          <el-option label="下架" :value="1" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button @click="handleQuery"><Icon icon="ep:search" class="mr-5px" /> 搜索</el-button>
        <el-button @click="resetQuery"><Icon icon="ep:refresh" class="mr-5px" /> 重置</el-button>
        <el-button type="primary" plain @click="openForm('create')" v-hasPermi="['mall:product:create']">
          <Icon icon="ep:plus" class="mr-5px" /> 新增
        </el-button>
      </el-form-item>
    </el-form>
  </ContentWrap>

  <ContentWrap>
    <el-table v-loading="loading" :data="list">
      <el-table-column label="商品图" width="80" align="center">
        <template #default="scope">
          <el-image v-if="scope.row.coverUrl" :src="scope.row.coverUrl" style="width: 40px; height: 40px" fit="cover" />
          <span v-else>-</span>
        </template>
      </el-table-column>
      <el-table-column prop="name" label="商品名称" min-width="150" show-overflow-tooltip />
      <el-table-column label="分类" width="120" align="center">
        <template #default="scope">
          {{ getCategoryName(scope.row.categoryId) }}
        </template>
      </el-table-column>
      <el-table-column label="售价" width="100" align="center">
        <template #default="scope">
          <span style="color: #f56c6c">¥{{ scope.row.price }}</span>
        </template>
      </el-table-column>
      <el-table-column prop="stock" label="库存" width="80" align="center" />
      <el-table-column prop="sales" label="销量" width="80" align="center" />
      <el-table-column label="状态" width="100" align="center">
        <template #default="scope">
          <el-tag v-if="scope.row.status === 0" type="success">上架</el-tag>
          <el-tag v-else type="info">下架</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="创建时间" width="180" align="center">
        <template #default="scope">
          <span>{{ formatDate(scope.row.createTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" width="180" fixed="right">
        <template #default="scope">
          <el-button link type="primary" @click="openForm('update', scope.row.id)" v-hasPermi="['mall:product:update']">修改</el-button>
          <el-button link type="danger" @click="handleDelete(scope.row.id)" v-hasPermi="['mall:product:delete']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    <Pagination :total="total" v-model:page="queryParams.pageNo" v-model:limit="queryParams.pageSize" @pagination="getList" />
  </ContentWrap>

  <ProductForm ref="formRef" @success="getList" />
</template>

<script lang="ts" setup>
import { formatDate } from '@/utils/formatTime'
import * as ProductApi from '@/api/mall/product'
import * as CategoryApi from '@/api/mall/category'
import ProductForm from './ProductForm.vue'

defineOptions({ name: 'MallProduct' })

const message = useMessage()
const { t } = useI18n()

const loading = ref(true)
const total = ref(0)
const list = ref([])
const categoryList = ref<any[]>([])
const queryParams = reactive({
  pageNo: 1,
  pageSize: 10,
  name: undefined,
  categoryId: undefined,
  status: undefined
})
const queryFormRef = ref()

const getCategoryName = (categoryId: number) => {
  const category = categoryList.value.find((item) => item.id === categoryId)
  return category ? category.name : '-'
}

const getList = async () => {
  loading.value = true
  try {
    const data = await ProductApi.getProductPage(queryParams)
    list.value = data.list
    total.value = data.total
  } finally {
    loading.value = false
  }
}

const getCategoryList = async () => {
  const data = await CategoryApi.getCategoryList()
  categoryList.value = data
}

const handleQuery = () => {
  queryParams.pageNo = 1
  getList()
}

const resetQuery = () => {
  queryFormRef.value.resetFields()
  handleQuery()
}

const formRef = ref()
const openForm = (type: string, id?: number) => {
  formRef.value.open(type, id, categoryList.value)
}

const handleDelete = async (id: number) => {
  try {
    await message.delConfirm()
    await ProductApi.deleteProduct(id)
    message.success(t('common.delSuccess'))
    await getList()
  } catch {}
}

onMounted(async () => {
  await getCategoryList()
  await getList()
})
</script>
