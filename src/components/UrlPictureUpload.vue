<template>
  <div class="url-picture-upload">
    <a-input-group compact style="margin-bottom: 16px">
      <a-input v-model:value="fileUrl" style="width: calc(100% - 120px)" placeholder="Please enter picture URL" />
      <a-button type="primary" :loading="loading" @click="handleUpload" style="width: 120px">Submit</a-button>
    </a-input-group>
    <div class="img-wrapper">
      <img v-if="picture?.url" :src="picture?.url" alt="avatar" />
    </div>
  </div>
</template>

<script lang="ts" setup>
import { ref } from 'vue';
import { message } from 'ant-design-vue';
import { uploadPictureByUrlUsingPost } from '@/api/pictureController'

interface Props {
  picture?: API.PictureVO
  onSuccess?: (newPicture: API.PictureVO) => void
}

const props = defineProps<Props>();
const loading = ref<boolean>(false)
const fileUrl = ref<string>()

/**
 * upload picture by url
 */
const handleUpload = async () => {
  loading.value = true
  try {
    const params: API.PictureUploadRequest = { fileUrl: fileUrl.value }
    if (props.picture) {
      params.id = props.picture.id
    }
    const res = await uploadPictureByUrlUsingPost(params)
    if (res.data.code === 0 && res.data.data) {
      message.success('Upload Success!')
      // pass uploaded picture info to parent
      props.onSuccess?.(res.data.data)
    } else {
      message.error('Upload Failed，' + res.data.message)
    }
  } catch (error) {
    message.error('Upload Failed!')
  } finally {
    loading.value = false
  }
}

</script>
<style scoped>
.url-picture-upload {
  margin-bottom: 16px;
}

.url-picture-upload img {
  max-width: 100%;
  max-height: 480px;
}

.url-picture-upload .img-wrapper {
  text-align: center;
  margin-top: 16px;
}
</style>
