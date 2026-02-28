<template>
  <div class="page">
    <CheckoutCard>
      <el-steps :active="activeStep" finish-status="success" align-center>
        <el-step title="選擇商品" />
        <el-step title="付款" />
        <el-step title="訂單" />
      </el-steps>

      <div class="section">
        <div v-if="activeStep === 0">
          <el-form label-width="100px">
            <el-form-item label="商品">
              <el-select v-model="selectedProduct" placeholder="請選擇商品" style="width: 100%">
                <el-option
                  v-for="item in products"
                  :key="item.name"
                  :label="`${item.name} - ${formatMinorAmount(item.amount, defaultCurrency)}`"
                  :value="item.name"
                />
              </el-select>
            </el-form-item>
            <el-button type="primary" :disabled="!selectedProduct" @click="createCheckout">建立付款</el-button>
          </el-form>
        </div>

        <div v-else-if="activeStep === 1">
          <p>商品：{{ checkout.product }} / 金額：{{ formatMinorAmount(checkout.amount, checkout.currency) }}</p>
          <el-form label-width="100px" class="card-form">
            <el-form-item label="信用卡">
              <div class="stripe-card-wrapper">
                <div id="stripe-card-element" class="stripe-card-element" />
              </div>
            </el-form-item>
          </el-form>
          <el-alert
            title="前端將透過 Stripe Elements 與 Stripe.js 完成付款確認，付款結果由 Stripe webhook 回傳後端"
            type="info"
            :closable="false"
            class="tip"
          />
          <el-button type="success" :loading="paying" @click="pay">確認付款</el-button>
        </div>

        <div v-else>
          <el-table :data="orders" style="width: 100%">
            <el-table-column prop="id" label="ID" min-width="220" />
            <el-table-column prop="product" label="商品" />
            <el-table-column label="金額">
              <template #default="scope">
                {{ formatMinorAmount(scope.row.amount, scope.row.currency || defaultCurrency) }}
              </template>
            </el-table-column>
            <el-table-column prop="status" label="狀態" />
          </el-table>
        </div>
      </div>
    </CheckoutCard>
  </div>
</template>

<script setup lang="ts">
import { onMounted } from 'vue'
import { storeToRefs } from 'pinia'
import CheckoutCard from '../components/CheckoutCard.vue'
import { useCheckoutStore } from '../store/checkout'

const checkoutStore = useCheckoutStore()

const { products, orders, selectedProduct, activeStep, paying, checkout, defaultCurrency } =
  storeToRefs(checkoutStore)
const { createCheckout, pay, initialize } = checkoutStore

const formatMinorAmount = (amount: number, currencyCode: string) => {
  return new Intl.NumberFormat('zh-TW', {
    style: 'currency',
    currency: (currencyCode || defaultCurrency.value || 'USD').toUpperCase()
  }).format(amount / 100)
}

onMounted(async () => {
  await initialize()
})
</script>

<style scoped>
.page {
  max-width: 900px;
  margin: 30px auto;
}
.section {
  margin-top: 24px;
}
.card-form {
  margin: 16px 0;
}
.tip {
  margin-bottom: 16px;
}
.stripe-card-wrapper {
  width: 100%;
}
.stripe-card-element {
  width: 100%;
  min-height: 40px;
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  padding: 10px 12px;
  background: #fff;
  box-sizing: border-box;
}
</style>
