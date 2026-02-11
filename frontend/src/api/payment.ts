import { http } from './http'
import type { AppConfig, CheckoutResponse, Order, Product } from '../types/payment'

export const paymentApi = {
  async getConfig() {
    const { data } = await http.get<AppConfig>('/config')
    return data
  },
  async getProducts() {
    const { data } = await http.get<Product[]>('/products')
    return data
  },
  async createCheckout(product: string) {
    const { data } = await http.post<CheckoutResponse>('/checkout', { product })
    return data
  },
  async completeOrder(orderId: string) {
    await http.post(`/orders/${orderId}/complete`)
  },
  async getOrders() {
    const { data } = await http.get<Order[]>('/orders')
    return data
  }
}
