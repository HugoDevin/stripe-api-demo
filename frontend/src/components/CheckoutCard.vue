<template>
  <el-card>
    <template #header>
      <div class="header">Stripe Demo（Vue3 + TS + Element Plus）</div>
    </template>
    <slot />
  </el-card>
</template>

<style scoped>
.header {
  font-size: 20px;
  font-weight: bold;
}
</style>
