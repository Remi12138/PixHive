<template>
  <div id="userRegisterPage">
    <h2 class="title">PixHive - User Register</h2>
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
      <a-form-item
        name="checkPassword"
        :rules="[
          { required: true, message: 'Please re-enter your password!' },
          { min: 8, message: 'The confirmation password cannot be less than 8 characters!' },
        ]"
      >
        <a-input-password v-model:value="formState.checkPassword" placeholder="confirm password" />
      </a-form-item>
      <div class="tips">
        Already has account?
        <RouterLink to="/user/login">Login</RouterLink>
      </div>
      <a-form-item>
        <a-button type="primary" html-type="submit" style="width: 100%">Register</a-button>
      </a-form-item>
    </a-form>
  </div>
</template>

<script lang="ts" setup>
import { reactive } from 'vue';
import { useLoginUserStore } from '@/stores/useLoginUserStore'
import { userLoginUsingPost, userRegisterUsingPost } from '@/api/userController'
import { useRouter } from "vue-router";
import { message } from 'ant-design-vue'

// receive form input
const formState = reactive<API.UserRegisterRequest>({
  userAccount: '',
  userPassword: '',
  checkPassword: '',
});
const router = useRouter()

/**
 * submit form
 * @param values
 */
const handleSubmit = async (values: any) => {
  // check 2 password match
  if (formState.userPassword !== formState.checkPassword) {
    message.error('The two passwords you entered did not match!')
    return
  }
  const res = await userRegisterUsingPost(values)
  // 注册成功，跳转到登录页面
  if (res.data.code === 0 && res.data.data) {
    message.success('Register success!')
    router.push({
      path: '/user/login',
      replace: true,
    })
  } else {
    message.error('Register error: ' + res.data.message)
  }
}
</script>

<style scoped>
#userRegisterPage {
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
