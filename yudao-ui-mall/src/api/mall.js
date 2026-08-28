import axios from 'axios'
import { ElMessage, ElLoading } from 'element-plus'

let loadingInstance = null
let loadingCount = 0

const showLoading = () => {
  if (loadingCount === 0) {
    loadingInstance = ElLoading.service({
      lock: true,
      text: '加载中...',
      background: 'rgba(255, 255, 255, 0.7)'
    })
  }
  loadingCount++
}

const hideLoading = () => {
  loadingCount--
  if (loadingCount <= 0) {
    loadingCount = 0
    if (loadingInstance) {
      loadingInstance.close()
      loadingInstance = null
    }
  }
}

const request = axios.create({
  baseURL: '/admin-api/mall/app',
  timeout: 15000,
  headers: {
    'tenant-id': '1',
    'user-id': '1'
  }
})

// 请求拦截器
request.interceptors.request.use(
  config => {
    // 显示 loading（可以通过配置关闭）
    if (config.loading !== false) {
      showLoading()
    }
    return config
  },
  error => {
    hideLoading()
    return Promise.reject(error)
  }
)

// 响应拦截器
request.interceptors.response.use(
  response => {
    hideLoading()
    const res = response.data
    if (res.code !== 0) {
      ElMessage.error(res.msg || '请求失败')
      return Promise.reject(new Error(res.msg || '请求失败'))
    }
    return res.data
  },
  error => {
    hideLoading()
    let message = '网络请求失败，请稍后重试'
    if (error.response) {
      const { status, data } = error.response
      if (status === 401) {
        message = '登录已过期，请重新登录'
      } else if (status === 403) {
        message = '没有权限访问该资源'
      } else if (status === 404) {
        message = '请求的资源不存在'
      } else if (status >= 500) {
        message = data?.msg || '服务器错误，请稍后重试'
      } else if (data?.msg) {
        message = data.msg
      }
    } else if (error.code === 'ECONNABORTED') {
      message = '请求超时，请检查网络'
    }
    ElMessage.error(message)
    return Promise.reject(error)
  }
)

// ========== 分类 API ==========
export const getCategoryList = () => request.get('/category/list', { loading: false })

// ========== 商品 API ==========
export const getProductPage = (params) => request.get('/product/page', { params })
export const getProductDetail = (id) => request.get('/product/detail', { params: { id } })

// ========== 购物车 API ==========
export const getCartList = () => request.get('/cart/list')
export const addCart = (data) => request.post('/cart/add', data)
export const updateCartCount = (data) => request.put('/cart/update-count', data)
export const deleteCart = (id) => request.delete('/cart/delete', { params: { id } })

// ========== 订单 API ==========
export const createOrder = (data) => request.post('/order/create', data)
export const getOrderPage = (params) => request.get('/order/page', { params })
export const getOrderDetail = (id) => request.get('/order/detail', { params: { id } })
export const cancelOrder = (id) => request.put('/order/cancel', null, { params: { id } })
export const payOrder = (id) => request.put('/order/pay', null, { params: { id } })
export const confirmReceipt = (id) => request.put('/order/confirm', null, { params: { id } })
export const deleteOrder = (id) => request.delete('/order/delete', { params: { id } })

// ========== 统计 API ==========
// 统计接口路径不在 /app 下，使用独立请求实例
const statsRequest = axios.create({
  baseURL: '/admin-api/mall',
  timeout: 15000,
  headers: {
    'tenant-id': '1'
  }
})

statsRequest.interceptors.response.use(
  response => {
    const res = response.data
    if (res.code !== 0) {
      ElMessage.error(res.msg || '请求失败')
      return Promise.reject(new Error(res.msg || '请求失败'))
    }
    return res.data
  },
  error => {
    ElMessage.error('获取统计数据失败')
    return Promise.reject(error)
  }
)

export const getStatistics = () => statsRequest.get('/statistics/overview')

export default request
