import { createRouter, createWebHistory } from 'vue-router'
import CheckoutPage from '../views/CheckoutPage.vue'

export const router = createRouter({
  history: createWebHistory(),
  routes: [
    {
      path: '/',
      name: 'checkout',
      component: CheckoutPage
    }
  ]
})
