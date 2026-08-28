<template>
  <Dialog v-model="dialogVisible" :title="dialogTitle" width="600px">
    <el-form ref="formRef" v-loading="formLoading" :model="formData" :rules="formRules" label-width="80px">
      <el-row :gutter="20">
        <el-col :span="24">
          <el-form-item label="商品分类" prop="categoryId">
            <el-select v-model="formData.categoryId" placeholder="请选择分类" style="width: 100%">
              <el-option v-for="item in categoryList" :key="item.id" :label="item.name" :value="item.id" />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="24">
          <el-form-item label="商品名称" prop="name">
            <el-input v-model="formData.name" placeholder="请输入商品名称" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="售价" prop="price">
            <el-input-number v-model="formData.price" :min="0" :precision="2" controls-position="right" style="width: 100%" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="库存" prop="stock">
            <el-input-number v-model="formData.stock" :min="0" controls-position="right" style="width: 100%" />
          </el-form-item>
        </el-col>
        <el-col :span="24">
          <el-form-item label="封面图" prop="coverUrl">
            <el-input v-model="formData.coverUrl" placeholder="请输入封面图URL" />
          </el-form-item>
        </el-col>
        <el-col :span="24">
          <el-form-item label="详情图" prop="detailUrls">
            <el-input v-model="formData.detailUrls" type="textarea" placeholder="多个URL用逗号分隔" />
          </el-form-item>
        </el-col>
        <el-col :span="24">
          <el-form-item label="描述" prop="description">
            <el-input v-model="formData.description" type="textarea" placeholder="请输入商品描述" />
          </el-form-item>
        </el-col>
        <el-col :span="24">
          <el-form-item label="上架状态" prop="status">
            <el-radio-group v-model="formData.status">
              <el-radio :value="0">上架</el-radio>
              <el-radio :value="1">下架</el-radio>
            </el-radio-group>
          </el-form-item>
        </el-col>
      </el-row>
    </el-form>
    <template #footer>
      <el-button type="primary" @click="submitForm">确 定</el-button>
      <el-button @click="dialogVisible = false">取 消</el-button>
    </template>
  </Dialog>
</template>

<script lang="ts" setup>
import * as ProductApi from '@/api/mall/product'
import { FormRules } from 'element-plus'

defineOptions({ name: 'MallProductForm' })

const message = useMessage()

const dialogVisible = ref(false)
const dialogTitle = ref('')
const formLoading = ref(false)
const formType = ref('')
const categoryList = ref<any[]>([])
const formData = ref({
  id: undefined,
  categoryId: undefined,
  name: '',
  coverUrl: '',
  detailUrls: '',
  price: 0,
  stock: 0,
  description: '',
  status: 0
})
const formRules = reactive<FormRules>({
  categoryId: [{ required: true, message: '分类不能为空', trigger: 'change' }],
  name: [{ required: true, message: '商品名称不能为空', trigger: 'blur' }],
  price: [{ required: true, message: '售价不能为空', trigger: 'blur' }],
  stock: [{ required: true, message: '库存不能为空', trigger: 'blur' }],
  status: [{ required: true, message: '上架状态不能为空', trigger: 'blur' }]
})
const formRef = ref()

const open = async (type: string, id?: number, categories?: any[]) => {
  dialogVisible.value = true
  dialogTitle.value = type === 'create' ? '新增商品' : '修改商品'
  formType.value = type
  if (categories) {
    categoryList.value = categories
  }
  resetForm()
  if (id) {
    formLoading.value = true
    try {
      const data = await ProductApi.getProduct(id)
      formData.value = data
    } finally {
      formLoading.value = false
    }
  }
}
defineExpose({ open })

const emit = defineEmits(['success'])

const submitForm = async () => {
  if (!formRef.value) return
  const valid = await formRef.value.validate()
  if (!valid) return
  formLoading.value = true
  try {
    const data = formData.value as unknown as ProductApi.ProductVO
    if (formType.value === 'create') {
      await ProductApi.createProduct(data)
      message.success('新增成功')
    } else {
      await ProductApi.updateProduct(data)
      message.success('修改成功')
    }
    dialogVisible.value = false
    emit('success')
  } finally {
    formLoading.value = false
  }
}

const resetForm = () => {
  formData.value = {
    id: undefined,
    categoryId: undefined,
    name: '',
    coverUrl: '',
    detailUrls: '',
    price: 0,
    stock: 0,
    description: '',
    status: 0
  }
  formRef.value?.resetFields()
}
</script>
