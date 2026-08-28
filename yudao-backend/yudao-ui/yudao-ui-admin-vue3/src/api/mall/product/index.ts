import request from '@/config/axios'

export interface ProductVO {
  id: number
  categoryId: number
  name: string
  coverUrl: string
  detailUrls: string
  price: number
  stock: number
  sales: number
  description: string
  status: number
  createTime: Date
}

export const getProductPage = (params: any) => {
  return request.get({ url: '/mall/product/page', params })
}

export const getProductList = (params?: any) => {
  return request.get({ url: '/mall/product/list', params })
}

export const getProduct = (id: number) => {
  return request.get({ url: '/mall/product/get?id=' + id })
}

export const createProduct = (data: ProductVO) => {
  return request.post({ url: '/mall/product/create', data })
}

export const updateProduct = (data: ProductVO) => {
  return request.put({ url: '/mall/product/update', data })
}

export const deleteProduct = async (id: number) => {
  return await request.delete({ url: '/mall/product/delete?id=' + id })
}
