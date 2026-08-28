import request from '@/config/axios'

export interface CategoryVO {
  id: number
  name: string
  sort: number
  status: number
  createTime: Date
}

export const getCategoryList = (params?: any) => {
  return request.get({ url: '/mall/category/list', params })
}

export const getCategoryPage = (params: any) => {
  return request.get({ url: '/mall/category/page', params })
}

export const getCategory = (id: number) => {
  return request.get({ url: '/mall/category/get?id=' + id })
}

export const createCategory = (data: CategoryVO) => {
  return request.post({ url: '/mall/category/create', data })
}

export const updateCategory = (data: CategoryVO) => {
  return request.put({ url: '/mall/category/update', data })
}

export const deleteCategory = async (id: number) => {
  return await request.delete({ url: '/mall/category/delete?id=' + id })
}
