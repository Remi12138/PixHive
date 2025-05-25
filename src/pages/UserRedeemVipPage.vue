<template>
  <div id="vipRedeemPage">
    <h2 style="margin-bottom: 16px">VIP Redeem</h2>
    <!-- redeem form -->
    <a-form name="formData" layout="vertical" :model="formData" @finish="handleSubmit">
      <a-form-item name="vipCode" label="VIP Code">
        <a-input
          v-model:value="formData.vipCode"
          placeholder="Please enter vip redeem code"
          allow-clear
        />
      </a-form-item>
      <a-form-item>
        <a-button type="primary" html-type="submit" style="width: 100%" :loading="loading">
          Redeem
        </a-button>
      </a-form-item>
    </a-form>
  </div>
</template>

<script setup lang="ts">
import { reactive, ref } from 'vue'
import { message } from 'ant-design-vue'
import { exchangeVipUsingPost } from '@/api/userController.ts'
import { useRouter } from 'vue-router'

const formData = reactive<API.VipRedeemRequest>({
  vipCode: '',
})

const loading = ref(false)

const router = useRouter()

const handleSubmit = async () => {
  if (!formData.vipCode) {
    message.error('Please enter vip redeem code!')
    return
  }

  loading.value = true

  try {
    const res = await exchangeVipUsingPost({
      vipCode: formData.vipCode,
    })

    if (res.data.code === 0 && res.data.data) {
      message.success('Redeem success！')
      router.push({
        path: `/`,
      })
    } else {
      message.error('Redeem error, ' + res.data.message)
    }
  } catch (error) {
    message.error('Redeem error, please try it later!')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
#vipRedeemPage {
  max-width: 720px;
  margin: 0 auto;
}
</style>
