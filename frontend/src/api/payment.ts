import { http } from './http'
import type { AppConfig, CheckoutResponse, Order, Product, PublicKeyResponse } from '../types/payment'

export const paymentApi = {
  async getConfig() {
    const { data } = await http.get<AppConfig>('/config')
    return data
  },
  async getProducts() {
    const { data } = await http.get<Product[]>('/products')
    return data
  },
  async getPublicKey() {
    const { data } = await http.get<PublicKeyResponse>('/security/public-key')
    return data
  },
  async createCheckout(product: string) {
    const { data } = await http.post<CheckoutResponse>('/checkout', { product })
    return data
  },
  async payEncrypted(orderId: string, encryptedData: string) {
    await http.post(`/orders/${orderId}/pay-encrypted`, { encryptedData })
  },
  async completeOrder(orderId: string) {
    await http.post(`/orders/${orderId}/complete`)
  },
  async getOrders() {
    const { data } = await http.get<Order[]>('/orders')
    return data
  }
}
