export interface Product {
  name: string
  amount: number
}

export interface CheckoutResponse {
  clientSecret: string
  orderId: string
  product: string
  amount: number
  currency: string
}

export interface Order {
  id: string
  product: string
  amount: number
  status: string
}

export interface AppConfig {
  publishableKey: string
}

export const EMPTY_CHECKOUT: CheckoutResponse = {
  clientSecret: '',
  orderId: '',
  product: '',
  amount: 0,
  currency: 'USD'
}
