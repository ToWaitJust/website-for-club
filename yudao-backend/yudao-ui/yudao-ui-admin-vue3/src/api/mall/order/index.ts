import request from '@/config/axios'

export interface OrderItemVO {
  id: number
  orderNo: string
  productId: number
  productName: string
  productCoverUrl: string
  price: number
  count: number
  totalAmount: number
}

export interface OrderVO {
  id: number
  orderNo: string
  userId: number
  totalAmount: number
  status: number
  payTime: Date
  cancelTime: Date
  cancelReason: string
  createTime: Date
  items?: OrderItemVO[]
}

export const getOrderPage = (params: any) => {
  return request.get({ url: '/mall/order/page', params })
}

export const getOrder = (id: number) => {
  return request.get({ url: '/mall/order/get?id=' + id })
}

export const updateOrderStatus = (data: { id: number; status: number }) => {
  return request.put({ url: '/mall/order/update-status', data })
}
