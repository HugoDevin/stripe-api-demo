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

export interface PublicKeyResponse {
  algorithm: string
  publicKey: string
}

export interface CardPayload {
  number: string
  expMonth: number | null
  expYear: number | null
  cvc: string
}

export const EMPTY_CHECKOUT: CheckoutResponse = {
  clientSecret: '',
  orderId: '',
  product: '',
  amount: 0,
  currency: 'USD'
}

export const EMPTY_CARD_PAYLOAD: CardPayload = {
  number: '',
  expMonth: null,
  expYear: null,
  cvc: ''
}
