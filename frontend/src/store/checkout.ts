import { defineStore } from 'pinia'
import { ElMessage } from 'element-plus'
import { nextTick, ref } from 'vue'
import { paymentApi } from '../api/payment'
import { loadStripeJs, type StripeLike } from '../api/stripe'
import { EMPTY_CHECKOUT, type CheckoutResponse, type Order, type Product } from '../types/payment'

export const useCheckoutStore = defineStore('checkout', () => {
  const products = ref<Product[]>([])
  const orders = ref<Order[]>([])
  const selectedProduct = ref('')
  const activeStep = ref(0)
  const paying = ref(false)
  const checkout = ref<CheckoutResponse>(EMPTY_CHECKOUT)

  let stripe: StripeLike | null = null
  let cardElement: unknown = null

  const loadProducts = async () => {
    products.value = await paymentApi.getProducts()
  }

  const loadOrders = async () => {
    orders.value = await paymentApi.getOrders()
  }

  const createCheckout = async () => {
    if (!selectedProduct.value) return

    try {
      const [config, checkoutResult] = await Promise.all([
        paymentApi.getConfig(),
        paymentApi.createCheckout(selectedProduct.value)
      ])

      checkout.value = checkoutResult
      stripe = window.Stripe(config.publishableKey)

      await nextTick()
      const elements = stripe.elements()
      cardElement = elements.create('card')
      cardElement.mount('#card-element')
      activeStep.value = 1
    } catch {
      ElMessage.error('建立付款失敗')
    }
  }

  const pay = async () => {
    if (!stripe || !cardElement) {
      ElMessage.error('Stripe 尚未初始化')
      return
    }

    paying.value = true

    const { error, paymentIntent } = await stripe.confirmCardPayment(checkout.value.clientSecret, {
      payment_method: { card: cardElement }
    })

    if (error) {
      ElMessage.error(error.message || '付款失敗')
      paying.value = false
      return
    }

    if (paymentIntent?.status === 'succeeded') {
      await paymentApi.completeOrder(checkout.value.orderId)
      ElMessage.success('付款成功')
      await loadOrders()
      activeStep.value = 2
    }

    paying.value = false
  }

  const initialize = async () => {
    await loadStripeJs()
    await Promise.all([loadProducts(), loadOrders()])
  }

  return {
    products,
    orders,
    selectedProduct,
    activeStep,
    paying,
    checkout,
    createCheckout,
    pay,
    initialize
  }
})
