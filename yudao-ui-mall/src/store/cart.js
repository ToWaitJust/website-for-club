import { defineStore } from 'pinia'
import { getCartList, addCart, updateCartCount, deleteCart } from '../api/mall'

export const useCartStore = defineStore('cart', {
  state: () => ({
    list: [],
    loading: false
  }),

  getters: {
    totalCount: (state) => {
      return state.list.reduce((sum, item) => sum + item.count, 0)
    },
    totalPrice: (state) => {
      return state.list.reduce((sum, item) => sum + item.price * item.count, 0)
    },
    selectedItems: (state) => {
      return state.list.filter(item => item.selected)
    }
  },

  actions: {
    async fetchCart() {
      this.loading = true
      try {
        const data = await getCartList()
        this.list = data.map(item => ({ ...item, selected: true }))
      } catch (e) {
        console.error('获取购物车失败:', e)
      } finally {
        this.loading = false
      }
    },

    async addToCart(productId, count = 1) {
      try {
        await addCart({ productId, count })
        await this.fetchCart()
        return true
      } catch (e) {
        console.error('添加购物车失败:', e)
        return false
      }
    },

    async updateCount(id, count) {
      try {
        await updateCartCount({ id, count })
        const item = this.list.find(i => i.id === id)
        if (item) item.count = count
      } catch (e) {
        console.error('更新数量失败:', e)
      }
    },

    async removeItem(id) {
      try {
        await deleteCart(id)
        this.list = this.list.filter(item => item.id !== id)
      } catch (e) {
        console.error('删除失败:', e)
      }
    },

    toggleSelect(id) {
      const item = this.list.find(i => i.id === id)
      if (item) item.selected = !item.selected
    },

    toggleSelectAll(selected) {
      this.list.forEach(item => item.selected = selected)
    },

    clearSelected() {
      this.list = this.list.filter(item => !item.selected)
    }
  }
})
