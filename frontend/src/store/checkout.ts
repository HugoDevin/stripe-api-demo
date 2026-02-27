import { defineStore } from 'pinia'
import { ElMessage } from 'element-plus'
import { ref } from 'vue'
import { paymentApi } from '../api/payment'
import {
  EMPTY_CARD_PAYLOAD,
  EMPTY_CHECKOUT,
  type CardPayload,
  type CheckoutResponse,
  type Order,
  type Product
} from '../types/payment'

const toBase64 = (bytes: Uint8Array) => btoa(String.fromCharCode(...bytes))

const decodeBase64 = (base64: string) => {
  const binary = atob(base64)
  return Uint8Array.from(binary, (char) => char.charCodeAt(0))
}

const encryptCardPayload = async (publicKeyBase64: string, payload: CardPayload) => {
  const keyData = decodeBase64(publicKeyBase64)
  const cryptoKey = await window.crypto.subtle.importKey(
    'spki',
    keyData,
    {
      name: 'RSA-OAEP',
      hash: 'SHA-256'
    },
    false,
    ['encrypt']
  )

  const encoded = new TextEncoder().encode(JSON.stringify(payload))
  const encrypted = await window.crypto.subtle.encrypt({ name: 'RSA-OAEP' }, cryptoKey, encoded)
  return toBase64(new Uint8Array(encrypted))
}

export const useCheckoutStore = defineStore('checkout', () => {
  const products = ref<Product[]>([])
  const orders = ref<Order[]>([])
  const selectedProduct = ref('')
  const activeStep = ref(0)
  const paying = ref(false)
  const checkout = ref<CheckoutResponse>(EMPTY_CHECKOUT)
  const card = ref<CardPayload>({ ...EMPTY_CARD_PAYLOAD })
  const defaultCurrency = ref('USD')

  const loadProducts = async () => {
    products.value = await paymentApi.getProducts()
  }

  const loadOrders = async () => {
    orders.value = await paymentApi.getOrders()
  }

  const loadConfig = async () => {
    const config = await paymentApi.getConfig()
    defaultCurrency.value = config.currency
  }

  const createCheckout = async () => {
    if (!selectedProduct.value) return

    try {
      checkout.value = await paymentApi.createCheckout(selectedProduct.value)
      activeStep.value = 1
      card.value = { ...EMPTY_CARD_PAYLOAD }
    } catch {
      ElMessage.error('建立付款失敗')
    }
  }

  const pay = async () => {
    if (!card.value.number || !card.value.expMonth || !card.value.expYear || !card.value.cvc) {
      ElMessage.error('請完整輸入信用卡資料')
      return
    }

    paying.value = true

    try {
      const { publicKey } = await paymentApi.getPublicKey()
      const encryptedData = await encryptCardPayload(publicKey, card.value)
      await paymentApi.payEncrypted(checkout.value.orderId, encryptedData)
      ElMessage.success('付款成功')
      await loadOrders()
      activeStep.value = 2
    } catch {
      ElMessage.error('付款失敗，請確認卡片資料')
    } finally {
      paying.value = false
    }
  }

  const initialize = async () => {
    await Promise.all([loadConfig(), loadProducts(), loadOrders()])
  }

  return {
    products,
    orders,
    selectedProduct,
    activeStep,
    paying,
    checkout,
    card,
    defaultCurrency,
    createCheckout,
    pay,
    initialize
  }
})
