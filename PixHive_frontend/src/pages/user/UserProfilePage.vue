<template>
  <div class="profile-container">
    <h2>User Profile</h2>
    <a-form :model="formState" layout="vertical" @submit.prevent @finish="handleSubmit">
      <!-- userName -->
      <a-form-item label="User Name" name="userName">
        <a-input v-model:value="formState.userName" placeholder="Enter new username" />
      </a-form-item>

      <!-- Avatar -->
      <a-form-item label="Avatar" name="userAvatar">
        <a-tabs v-model:activeKey="avatarUploadType">
          <a-tab-pane key="file" tab="Upload File">
            <a-upload
              name="file"
              :show-upload-list="false"

              :custom-request="uploadAvatar"
            >
              <a-spin :spinning="avatarUploading">
                <a-avatar
                  :src="formState.userAvatar"
                  shape="circle"
                  size="large"
                  style="cursor: pointer"
                />
              </a-spin>
              <div style="margin-top: 8px; color: #1890ff;">Click avatar to upload</div>
            </a-upload>
          </a-tab-pane>

          <a-tab-pane key="url" tab="Use URL">
            <a-input
              v-model:value="formState.userAvatar"
              placeholder="Paste avatar image URL"
              allow-clear
            />
          </a-tab-pane>
        </a-tabs>
      </a-form-item>

      <!-- Password -->
      <a-form-item
        label="New Password"
        name="userPassword"
        :rules="[
          { min: 8, message: 'The password cannot be less than 8 characters!' }
        ]"
      >
        <a-input-password v-model:value="formState.userPassword" placeholder="Password (optional)" />
      </a-form-item>

      <!-- Confirm Password -->
      <a-form-item
        label="Confirm Password"
        name="checkPassword"
        :rules="[
          { min: 8, message: 'The confirmation password cannot be less than 8 characters!' },
          { validator: validateConfirmPassword }
        ]"
      >
        <a-input-password v-model:value="formState.checkPassword" placeholder="Confirm password (optional)" />
      </a-form-item>

      <a-form-item>
        <a-button type="primary" html-type="submit" :loading="loading">Update Profile</a-button>
      </a-form-item>
    </a-form>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { message } from 'ant-design-vue'
import { useLoginUserStore } from '@/stores/useLoginUserStore'
import { updateProfileUsingPost, uploadAvatarUsingPost } from '@/api/userController'
import { useRouter } from 'vue-router'
import type { UploadRequestOption as UploadRequestOptionType } from 'ant-design-vue/lib/upload/interface'

const avatarUploadType = ref<'file' | 'url'>('file')
const avatarUploading = ref(false)

// Upload logic
const uploadAvatar = async (options: UploadRequestOptionType) => {
  const file = options.file as File
  const formData = new FormData()
  formData.append('file', file)

  avatarUploading.value = true
  try {
    const res = await uploadAvatarUsingPost({}, file)
    const { code, data, message: errMsg } = res?.data ?? {}

    if (code === 0) {
      formState.value.userAvatar = data
      message.success('Avatar uploaded successfully!')
      options.onSuccess?.({}, file)
    } else {
      message.error('Upload failed: ' + (errMsg || 'Unknown error'))
      options.onError?.(new Error(errMsg || 'Upload failed'))
    }
  } catch (e) {
    message.error('Upload error')
    options.onError?.(e)
  } finally {
    avatarUploading.value = false
  }
}

const router = useRouter()

const loginUserStore = useLoginUserStore()

const formState = ref({
  userName: loginUserStore.loginUser.userName || '',
  userAvatar: loginUserStore.loginUser.userAvatar || '',
  userPassword: '',
  checkPassword: ''
})

const loading = ref(false)

const validateConfirmPassword = (_: unknown, value: string) => {
  const newPassword = formState.value.userPassword
  if (!newPassword && !value) {
    // both empty → valid
    return Promise.resolve()
  }
  if (value !== newPassword) {
    return Promise.reject(new Error('Passwords do not match!'))
  }
  return Promise.resolve()
}


const handleSubmit = async () => {
  loading.value = true
  try {
    const payload = {
      userName: formState.value.userName,
      userAvatar: formState.value.userAvatar,
      userPassword: formState.value.userPassword
    }

    const res = await updateProfileUsingPost(payload)

    if (res?.data?.code === 0) {
      message.success('Profile updated successfully!')

      // update local store
      loginUserStore.setLoginUser({
        ...loginUserStore.loginUser,
        userName: payload.userName,
        userAvatar: payload.userAvatar
      })

      // success -> home page
      router.push('/')
    } else {
      message.error('Update failed: ' + res?.data?.message)
    }
  } catch (e) {
    message.error('Request error')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.profile-container {
  max-width: 480px;
  margin: 40px auto;
  padding: 24px;
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}
</style>
