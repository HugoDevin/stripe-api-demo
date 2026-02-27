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
            <el-form-item label="卡號">
              <el-input v-model="card.number" maxlength="19" placeholder="4242424242424242" />
            </el-form-item>
            <el-row :gutter="12">
              <el-col :span="8">
                <el-form-item label="月">
                  <el-input-number v-model="card.expMonth" :min="1" :max="12" :controls="false" />
                </el-form-item>
              </el-col>
              <el-col :span="8">
                <el-form-item label="年">
                  <el-input-number v-model="card.expYear" :min="2024" :max="2099" :controls="false" />
                </el-form-item>
              </el-col>
              <el-col :span="8">
                <el-form-item label="CVC">
                  <el-input v-model="card.cvc" maxlength="4" show-password placeholder="123" />
                </el-form-item>
              </el-col>
            </el-row>
          </el-form>
          <el-alert
            title="卡片資料會先在前端以 RSA-OAEP 加密，再送往後端解密處理"
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

const { products, orders, selectedProduct, activeStep, paying, checkout, card, defaultCurrency } =
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
</style>
