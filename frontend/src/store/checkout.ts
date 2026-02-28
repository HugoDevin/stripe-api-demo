import { defineStore } from 'pinia'
import { ElMessage } from 'element-plus'
import { nextTick, ref } from 'vue'
import { paymentApi } from '../api/payment'
import { loadStripeJs, type StripeLike } from '../api/stripe'
import {
  EMPTY_CHECKOUT,
  type CheckoutResponse,
  type Order,
  type Product
} from '../types/payment'

export const useCheckoutStore = defineStore('checkout', () => {
  const products = ref<Product[]>([])
  const orders = ref<Order[]>([])
  const selectedProduct = ref('')
  const activeStep = ref(0)
  const paying = ref(false)
  const checkout = ref<CheckoutResponse>(EMPTY_CHECKOUT)
  const defaultCurrency = ref('USD')
  const publishableKey = ref('')
  const stripe = ref<StripeLike | null>(null)
  const stripeCard = ref<unknown | null>(null)

  const loadProducts = async () => {
    products.value = await paymentApi.getProducts()
  }

  const loadOrders = async () => {
    orders.value = await paymentApi.getOrders()
  }

  const loadConfig = async () => {
    const config = await paymentApi.getConfig()
    defaultCurrency.value = config.currency
    publishableKey.value = config.publishableKey || ''
  }

  const ensureStripeCardMounted = async () => {
    if (!stripe.value || stripeCard.value) return

    for (let i = 0; i < 8; i += 1) {
      await nextTick()
      const container = document.getElementById('stripe-card-element')
      if (container && container.clientWidth > 80) {
        const elements = stripe.value.elements()
        const cardElement = elements.create('card')
        cardElement.mount('#stripe-card-element')
        stripeCard.value = cardElement
        return
      }
      await new Promise((resolve) => setTimeout(resolve, 50))
    }

    throw new Error('stripe card container not ready')
  }

  const initializeStripe = async () => {
    if (!publishableKey.value) return
    await loadStripeJs()
    stripe.value = window.Stripe(publishableKey.value)
  }

  const createCheckout = async () => {
    if (!selectedProduct.value) return

    try {
      checkout.value = await paymentApi.createCheckout(selectedProduct.value)
      activeStep.value = 1
      await ensureStripeCardMounted()
    } catch {
      ElMessage.error('建立付款失敗')
    }
  }

  const pay = async () => {
    if (!stripe.value) {
      ElMessage.error('目前未設定 Stripe 金鑰，無法在此環境付款')
      return
    }

    paying.value = true

    try {
      await ensureStripeCardMounted()
      if (!stripeCard.value) throw new Error('stripe card not mounted')
      const result = await stripe.value.confirmCardPayment(checkout.value.clientSecret, {
        payment_method: { card: stripeCard.value }
      })
      if (result.error) throw new Error(result.error.message || 'stripe payment failed')

      ElMessage.success('付款已送出，等待 Stripe webhook 同步狀態')
      await loadOrders()
      activeStep.value = 2
    } catch {
      ElMessage.error('付款失敗，請檢查卡片資料或 Stripe 設定')
    } finally {
      paying.value = false
    }
  }

  const initialize = async () => {
    await Promise.all([loadConfig(), loadProducts(), loadOrders()])
    await initializeStripe()
  }

  return {
    products,
    orders,
    selectedProduct,
    activeStep,
    paying,
    checkout,
    defaultCurrency,
    createCheckout,
    pay,
    initialize
  }
})
