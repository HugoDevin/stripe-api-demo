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
                  :label="`${item.name} - $${(item.amount / 100).toFixed(2)}`"
                  :value="item.name"
                />
              </el-select>
            </el-form-item>
            <el-button type="primary" :disabled="!selectedProduct" @click="createCheckout">建立付款</el-button>
          </el-form>
        </div>

        <div v-else-if="activeStep === 1">
          <p>商品：{{ checkout.product }} / 金額：${{ (checkout.amount / 100).toFixed(2) }}</p>
          <div id="card-element"></div>
          <el-button type="success" :loading="paying" @click="pay">確認付款</el-button>
        </div>

        <div v-else>
          <el-table :data="orders" style="width: 100%">
            <el-table-column prop="id" label="ID" min-width="220" />
            <el-table-column prop="product" label="商品" />
            <el-table-column prop="amount" label="金額(cents)" />
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

const { products, orders, selectedProduct, activeStep, paying, checkout } = storeToRefs(checkoutStore)
const { createCheckout, pay, initialize } = checkoutStore

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
#card-element {
  padding: 10px;
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  margin-bottom: 16px;
}
</style>
