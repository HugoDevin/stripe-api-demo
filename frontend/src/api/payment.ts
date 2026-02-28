import { http } from './http'
import type { AppConfig, CheckoutResponse, Order, Product, PublicKeyResponse } from '../types/payment'

export const paymentApi = {
  async getConfig() {
    const { data } = await http.get<AppConfig>('/config')
    return data
  },
  async getProducts() {
    const { data } = await http.get<Array<{ name: string; price: number }>>('/products')
    return data.map((item) => ({ name: item.name, amount: item.price })) as Product[]
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
    const { data } = await http.get<
      Array<{ id: string; status: string; currency?: string; totalAmount?: number; items?: Array<{ name?: string }> }>
    >('/orders')
    return data.map((order) => ({
      id: order.id,
      product: order.items?.[0]?.name || '-',
      amount: Number(order.totalAmount || 0),
      currency: order.currency || 'USD',
      status: order.status
    })) as Order[]
  }
}
