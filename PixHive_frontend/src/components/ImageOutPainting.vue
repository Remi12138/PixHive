<template>
  <a-modal
    class="image-out-painting"
    v-model:visible="visible"
    title="AI Image Outpainting"
    :footer="false"
    @cancel="closeModal"
  >
    <a-row gutter="16">
      <a-col span="12">
        <h4>Original Picture</h4>
        <img :src="picture?.url" :alt="picture?.name" style="max-width: 100%" />
      </a-col>
      <a-col span="12">
        <h4>Outpainting Result</h4>
        <img
          v-if="resultImageUrl"
          :src="resultImageUrl"
          :alt="picture?.name"
          style="max-width: 100%"
        />
      </a-col>
    </a-row>
    <div style="margin-bottom: 16px" />
    <a-flex gap="16" justify="center">
      <a-button type="primary" :loading="!!taskId" ghost
                @click="createTask">
        AI Generate
      </a-button>
      <a-button type="primary" v-if="resultImageUrl"
                :loading="uploadLoading"
                @click="handleUpload">
        Apply
      </a-button>
    </a-flex>
  </a-modal>
</template>

<script setup lang="ts">
import { ref, onUnmounted } from 'vue'
import {
  createPictureOutPaintingTaskUsingPost,
  getPictureOutPaintingTaskUsingGet, uploadPictureByUrlUsingPost,
  uploadPictureUsingPost
} from '@/api/pictureController'
import { message } from 'ant-design-vue'

interface Props {
  picture?: API.PictureVO
  spaceId?: number
  onSuccess?: (newPicture: API.PictureVO) => void
}

const props = defineProps<Props>()
const visible = ref(false)
const resultImageUrl = ref<string>()

const openModal = () => {
  visible.value = true
}

const closeModal = () => {
  visible.value = false
}

defineExpose({
  openModal,
})

// store, then to query
let taskId = ref<string>()

const createTask = async () => {
  if (!props.picture?.id) {
    return
  }
  const res = await createPictureOutPaintingTaskUsingPost({
    pictureId: props.picture.id,
    // The expansion parameters can be set as needed
    parameters: {
      xScale: 2,
      yScale: 2,
    },
  })
  if (res.data.code === 0 && res.data.data) {
    message.success('The task has been successfully created. Please wait patiently and do not exit!')
    console.log(res.data.data.output.taskId)
    // store taskId to query
    taskId.value = res.data.data.output.taskId
    // start to poll
    startPolling()
  } else {
    message.error('Create failed，' + res.data.message)
  }
}

// polling timer
let pollingTimer: NodeJS.Timeout = null

// must clean
const clearPolling = () => {
  if (pollingTimer) {
    clearInterval(pollingTimer)
    pollingTimer = null
    taskId.value = null
  }
}

const startPolling = () => {
  if (!taskId.value) return

  pollingTimer = setInterval(async () => {
    try {
      const res = await getPictureOutPaintingTaskUsingGet({
        taskId: taskId.value,
      })
      if (res.data.code === 0 && res.data.data) {
        const taskResult = res.data.data.output
        if (taskResult.taskStatus === 'SUCCEEDED') {
          message.success('Outpainting Completed!')
          resultImageUrl.value = taskResult.outputImageUrl
          clearPolling()
        } else if (taskResult.taskStatus === 'FAILED') {
          message.error('Outpainting failed!')
          clearPolling()
        }
      }
    } catch (error) {
      console.error('Query task error, ', error)
      message.error('Query task failed. Please try again later.')
      clearPolling()
    }
  }, 3000) // poll every 3s
}

// Clean the timer to avoid memory leaks
onUnmounted(() => {
  clearPolling()
})

const uploadLoading = ref<boolean>(false)

const handleUpload = async () => {
  uploadLoading.value = true
  try {
    const params: API.PictureUploadRequest = {
      fileUrl: resultImageUrl.value,
      spaceId: props.spaceId,
    }
    if (props.picture) {
      params.id = props.picture.id
    }
    const res = await uploadPictureByUrlUsingPost(params)
    if (res.data.code === 0 && res.data.data) {
      message.success('Picture upload successfully!')
      props.onSuccess?.(res.data.data)
      closeModal()
    } else {
      message.error('Picture upload failed, ' + res.data.message)
    }
  } catch (error) {
    message.error('Picture upload failed')
  } finally {
    uploadLoading.value = false
  }
}
</script>

<style scoped>
.image-out-painting {
  text-align: center;
}
</style>
