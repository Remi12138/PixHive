<template>
  <a-modal class="image-cropper" v-model:visible="visible" title="Edit Picture" :footer="false" @cancel="closeModal">
    <!-- ratio selector -->
    <!-- take effect when drag box -->
    <div class="aspect-ratio-selector">
      <a-radio-group v-model:value="aspectRatio" button-style="solid">
        <a-radio-button value="free">Free</a-radio-button>
        <a-radio-button value="1:1">1:1</a-radio-button>
        <a-radio-button value="4:3">4:3</a-radio-button>
        <a-radio-button value="16:9">16:9</a-radio-button>
        <a-radio-button value="3:4">3:4</a-radio-button>
        <a-radio-button value="9:16">9:16</a-radio-button>
      </a-radio-group>
    </div>
    <div style="margin-bottom: 16px" />
    <!-- crop component -->
    <vue-cropper
      ref="cropperRef"
      :img="imageUrl"
      :autoCrop="true"
      :fixedBox="false"
      :centerBox="true"
      :canMoveBox="true"
      :canMove="false"
      :info="true"
      outputType="png"
      :fixed="aspectRatio !== 'free'"
      :fixedNumber="currentAspectRatio"
    />
    <div style="margin-bottom: 16px" />
    <!-- image action -->
    <div class="image-cropper-actions">
      <a-space>
        <a-button @click="rotateLeft">Rotate Right</a-button>
        <a-button @click="rotateRight">Rotate Left</a-button>
        <a-button @click="changeScale(1)">Zoom in</a-button>
        <a-button @click="changeScale(-1)">Zoom out</a-button>
        <a-button type="primary" :loading="loading" @click="handleConfirm">OK</a-button>
      </a-space>
    </div>
  </a-modal>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { uploadPictureUsingPost } from '@/api/pictureController'
import { message } from 'ant-design-vue'

interface Props {
  imageUrl?: string
  picture?: API.PictureVO
  spaceId?: number
  onSuccess?: (newPicture: API.PictureVO) => void
}

const props = defineProps<Props>()

const cropperRef = ref()
const visible = ref(false)

const openModal = () => {
  visible.value = true
}

const closeModal = () => {
  visible.value = false
}

// expose function to parent
defineExpose({
  openModal,
});

const rotateLeft = () => {
  cropperRef.value.rotateLeft()
}

const rotateRight = () => {
  cropperRef.value.rotateRight()
}

const changeScale = (num: number) => {
  cropperRef.value?.changeScale(num)
}

const loading = ref<boolean>(false)

const handleConfirm = () => {
  cropperRef.value.getCropBlob((blob: Blob) => { // blob is the cropped file
    const fileName = (props.picture?.name || 'image') + '.png'
    const file = new File([blob], fileName, { type: blob.type })
    handleUpload({ file })
  })
}

const handleUpload = async ({ file }: any) => {
  loading.value = true
  try {
    const params: API.PictureUploadRequest = props.picture ? { id: props.picture.id } : {}
    params.spaceId = props.spaceId
    const res = await uploadPictureUsingPost(params, {}, file)
    if (res.data.code === 0 && res.data.data) {
      message.success('Upload success!')
      // pass uploaded pic info to parent
      props.onSuccess?.(res.data.data)
      closeModal();
    } else {
      message.error('Upload failed, ' + res.data.message)
    }
  } catch (error) {
    message.error('Upload failed')
  } finally {
    loading.value = false
  }
}

const aspectRatio = ref('free')

// compute current ratio: width/height
const currentAspectRatio = computed(() => {
  if (aspectRatio.value === 'free') return [0, 0]
  const [width, height] = aspectRatio.value.split(':').map(Number)
  return [width, height]
})

</script>

<style>
.image-cropper {
  text-align: center;
}

.image-cropper .vue-cropper {
  height: 400px !important;
}
</style>
