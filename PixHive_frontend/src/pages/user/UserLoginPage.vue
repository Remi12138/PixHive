<template>
  <div id="userLoginPage">
    <h2 class="title">PixHive - User Login</h2>
    <div class="desc">Enterprise intelligent collaborative cloud library</div>
    <a-form
      :model="formState"
      name="basic"
      autocomplete="off"
      @finish="handleSubmit">
      <a-form-item name="userAccount" :rules="[{ required: true, message: 'Please input your user account!' }]">
        <a-input v-model:value="formState.userAccount" placeholder="user account" />
      </a-form-item>
      <a-form-item
        name="userPassword"
        :rules="[
          { required: true, message: 'Please input your password!' },
          { min: 8, message: 'The password cannot be less than 8 characters!' },
        ]"
      >
        <a-input-password v-model:value="formState.userPassword" placeholder="password" />
      </a-form-item>
      <div class="tips">
        No account?
        <RouterLink to="/user/register">Register</RouterLink>
      </div>
      <a-form-item>
        <a-button type="primary" html-type="submit" style="width: 100%">Login</a-button>
      </a-form-item>
    </a-form>
  </div>
</template>

<script lang="ts" setup>
import { reactive } from 'vue';
import { useLoginUserStore } from '@/stores/useLoginUserStore'
import { userLoginUsingPost } from '@/api/userController'
import { useRouter } from "vue-router";
import { message } from 'ant-design-vue'

// receive form input
const formState = reactive<API.UserLoginRequest>({
  userAccount: '',
  userPassword: '',
});
const router = useRouter()
const loginUserStore = useLoginUserStore()

/**
 * submit form
 * @param values
 */
const handleSubmit = async (values: any) => {
  try {
    const res = await userLoginUsingPost(values)
    // login success, store login status to the global status
    if (res.data.code === 0 && res.data.data) {
      await loginUserStore.fetchLoginUser()
      message.success('Login success!')
      router.push({
        path: '/',
        replace: true,
      })
    } else {
      message.error('Login error!' + res.data.message)
    }
  } catch (e) {
    message.error('Login error!' + e.message)
  }
}
</script>

<style scoped>
#userLoginPage {
  max-width: 360px;
  margin: 0 auto;
}

.title {
  text-align: center;
  margin-bottom: 16px;
}

.desc {
  text-align: center;
  color: #bbb;
  margin-bottom: 16px;
}

.tips {
  margin-bottom: 16px;
  color: #bbb;
  font-size: 13px;
  text-align: right;
}

</style>
