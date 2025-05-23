<template>
  <div id="addPicturePage">
    <h2 style="margin-bottom: 16px">
      {{ route.query?.id ? 'Edit Picture' : 'Create Picture' }}
    </h2>
    <a-typography-paragraph v-if="spaceId" type="secondary">
      Save to Space: <a :href="`/space/${spaceId}`" target="_blank">{{ spaceId }}</a>
    </a-typography-paragraph>

    <!-- choose how to upload -->
    <a-tabs v-model:activeKey="uploadType">
      <a-tab-pane key="file" tab="File Upload">
        <PictureUpload :picture="picture" :spaceId="spaceId" :onSuccess="onSuccess" />
      </a-tab-pane>
      <a-tab-pane key="url" tab="URL Upload" force-render>
        <UrlPictureUpload :picture="picture" :spaceId="spaceId" :onSuccess="onSuccess" />
      </a-tab-pane>
    </a-tabs>
    <!-- Picture Edit -->
    <div v-if="picture" class="edit-bar">
      <a-space size="middle">
        <a-button :icon="h(EditOutlined)" @click="doEditPicture">Edit Picture</a-button>
        <a-button :disabled="!isPictureEligibleForOutpainting" type="primary" ghost :icon="h(FullscreenOutlined)" @click="doImageOutPainting">
          AI Outpainting
        </a-button>
      </a-space>
      <div style="margin-bottom: 16px" />
      <a-alert type="info" show-icon message="AI Outpainting Tip" style="margin-bottom: 16px; text-align: left">
        <template #description>
          <div>
            To use the AI Outpainting feature, your picture must meet the following requirements:<br />
            • Format: JPG, JPEG, PNG, HEIF, or WEBP<br />
            • Size: No more than 10MB<br />
            • Resolution: Between 512×512 and 4096×4096 pixels
          </div>
        </template>
      </a-alert>
      <ImageCropper
        ref="imageCropperRef"
        :imageUrl="picture?.url"
        :picture="picture"
        :spaceId="spaceId"
        :space="space"
        :onSuccess="onCropSuccess"
      />
      <ImageOutPainting
        ref="imageOutPaintingRef"
        :picture="picture"
        :spaceId="spaceId"
        :onSuccess="onImageOutPaintingSuccess"
      />
    </div>
    <!-- Picture Info -->
    <a-form v-if="picture" layout="vertical" :model="pictureForm" @finish="handleSubmit">
      <a-form-item label="name" name="name">
        <a-input v-model:value="pictureForm.name" placeholder="Enter name" />
      </a-form-item>
      <a-form-item label="introduction" name="introduction">
        <a-textarea
          v-model:value="pictureForm.introduction"
          placeholder="Enter introduction"
          :auto-size="{ minRows: 2, maxRows: 5 }"
          allowClear
        />
      </a-form-item>
      <a-form-item label="category" name="category">
        <a-auto-complete
          v-model:value="pictureForm.category"
          placeholder="Enter category"
          :options="categoryOptions"
          allowClear
        />
      </a-form-item>
      <a-form-item label="tags" name="tags">
        <a-select
          v-model:value="pictureForm.tags"
          mode="tags"
          placeholder="Enter tags"
          :options="tagOptions"
          allowClear
        />
      </a-form-item>
      <a-form-item>
        <a-button type="primary" html-type="submit" style="width: 100%">Create</a-button>
      </a-form-item>
    </a-form>
  </div>

</template>

<script setup lang="ts">
import PictureUpload from '@/components/PictureUpload.vue'
import { reactive, ref, onMounted, computed, h, watchEffect } from "vue";
import { editPictureUsingPost, getPictureVoByIdUsingGet, listPictureTagCategoryUsingGet } from '@/api/pictureController'
import { useRouter, useRoute } from "vue-router";
import { message } from 'ant-design-vue'
import UrlPictureUpload from '@/components/UrlPictureUpload.vue'
import ImageCropper from '@/components/ImageCropper.vue'
import { EditOutlined, FullscreenOutlined } from '@ant-design/icons-vue'
import ImageOutPainting from '@/components/ImageOutPainting.vue'
import { getSpaceVoByIdUsingGet } from '@/api/spaceController'


const picture = ref<API.PictureVO>();
const pictureForm = reactive<API.PictureEditRequest>({});
const uploadType = ref<'file' | 'url'>('file');
const route = useRoute()
const router = useRouter()

const spaceId = computed(() => {
  return route.query?.spaceId
})


const onSuccess = (newPicture: API.PictureVO) => {
  picture.value = newPicture;
  pictureForm.name = newPicture.name;
}


/**
 * submit form
 * @param values
 */
const handleSubmit = async (values: any) => {
  const pictureId = picture.value.id
  if (!pictureId) {
    return
  }
  const res = await editPictureUsingPost({
    id: pictureId,
    spaceId: spaceId.value,
    ...values,
  })
  if (res.data.code === 0 && res.data.data) {
    message.success('Create picture success!')
    // go to picture detail
    router.push({
      path: `/picture/${pictureId}`,
    })
  } else {
    message.error('Create picture failed!，' + res.data.message)
  }
}

const categoryOptions = ref<string[]>([])
const tagOptions = ref<string[]>([])

const getTagCategoryOptions = async () => {
  const res = await listPictureTagCategoryUsingGet()
  if (res.data.code === 0 && res.data.data) {
    // Convert to the format accepted by the drop-down option component
    tagOptions.value = (res.data.data.tagList ?? []).map((data: string) => {
      return {
        value: data,
        label: data,
      }
    })
    categoryOptions.value = (res.data.data.categoryList ?? []).map((data: string) => {
      return {
        value: data,
        label: data,
      }
    })
  } else {
    message.error('get Tag/Category Options error，' + res.data.message)
  }
}

onMounted(() => {
  getTagCategoryOptions()
})


// get Old Picture
const getOldPicture = async () => {
  const id = route.query?.id
  if (id) {
    const res = await getPictureVoByIdUsingGet({
      id: id,
    })
    if (res.data.code === 0 && res.data.data) {
      const data = res.data.data
      picture.value = data
      pictureForm.name = data.name
      pictureForm.introduction = data.introduction
      pictureForm.category = data.category
      pictureForm.tags = data.tags
    }
  }
}

onMounted(() => {
  getOldPicture()
})

// ----------- Picture Edit (Basic Crop) -------------------
const imageCropperRef = ref()

const doEditPicture = () => {
  if (imageCropperRef.value) {
    imageCropperRef.value.openModal()
  }
}

const onCropSuccess = (newPicture: API.PictureVO) => {
  picture.value = newPicture
}

// ----------- AI Outpainting -------------------

const imageOutPaintingRef = ref()

const doImageOutPainting = () => {
  if (imageOutPaintingRef.value) {
    imageOutPaintingRef.value.openModal()
  }
}

const onImageOutPaintingSuccess = (newPicture: API.PictureVO) => {
  picture.value = newPicture
}

const isPictureEligibleForOutpainting = computed(() => {
  if (!picture.value) return false

  const allowedFormats = ['jpg', 'jpeg', 'png', 'heif', 'webp']
  const format = picture.value.picFormat?.toLowerCase()
  const size = picture.value.picSize || 0
  const width = picture.value.picWidth || 0
  const height = picture.value.picHeight || 0

  return (
    allowedFormats.includes(format || '') &&
    size <= 10 * 1024 * 1024 &&
    width >= 512 && height >= 512 &&
    width <= 4096 && height <= 4096
  )
})

const space = ref<API.SpaceVO>()

// fetch space info
const fetchSpace = async () => {
  if (spaceId.value) {
    const res = await getSpaceVoByIdUsingGet({
      id: spaceId.value,
    })
    if (res.data.code === 0 && res.data.data) {
      space.value = res.data.data
    }
  }
}

watchEffect(() => {
  fetchSpace()
})

</script>

<style scoped>
#addPicturePage {
  max-width: 720px;
  margin: 0 auto;
}
#addPicturePage .edit-bar {
  text-align: center;
  margin: 16px 0;
}
</style>
