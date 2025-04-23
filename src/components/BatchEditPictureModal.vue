<template>
  <div class="batch-edit-picture-modal">
    <a-modal v-model:visible="visible" title="Batch Edit Picture" :footer="false" @cancel="closeModal">
      <a-typography-paragraph type="secondary">* It only takes effect on the pictures on the current page</a-typography-paragraph>
      <!-- Batch Request Form -->
      <a-form layout="vertical" :model="formData" @finish="handleSubmit">
        <a-form-item label="Category" name="category">
          <a-auto-complete
            v-model:value="formData.category"
            :options="categoryOptions"
            placeholder="enter category"
            allowClear
          />
        </a-form-item>
        <a-form-item label="Tags" name="tags">
          <a-select
            v-model:value="formData.tags"
            :options="tagOptions"
            mode="tags"
            placeholder="enter tags"
            allowClear
          />
        </a-form-item>
        <a-form-item label="Name Rule" name="nameRule">
          <a-input v-model:value="formData.nameRule" placeholder="Use {index} can generate it dynamically. e.g., myLogo{index}" />
        </a-form-item>
        <a-form-item>
          <a-button type="primary" html-type="submit">Submit</a-button>
        </a-form-item>
      </a-form>
    </a-modal>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { reactive, onMounted } from 'vue'
import {
  editPictureByBatchUsingPost,
  listPictureTagCategoryUsingGet,
} from '@/api/pictureController'
import { message } from 'ant-design-vue'

interface Props {
  pictureList: API.PictureVO[]
  spaceId: number | string
  onSuccess: () => void
}

const props = withDefaults(defineProps<Props>(), {})

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
})

// init form data
const formData = reactive({
  category: '',
  tags: [],
  nameRule: '',
})

const categoryOptions = ref<string[]>([])
const tagOptions = ref<string[]>([])

const getTagCategoryOptions = async () => {
  const res = await listPictureTagCategoryUsingGet()
  if (res.data.code === 0 && res.data.data) {
    // Convert to the format accepted by the dropdown option component
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
    message.error('Get Tag/Category options failed，' + res.data.message)
  }
}

onMounted(() => {
  getTagCategoryOptions()
})

const handleSubmit = async (values: any) => {
  if (!props.pictureList) {
    return
  }
  const res = await editPictureByBatchUsingPost({
    pictureIdList: props.pictureList.map((picture) => picture.id),
    spaceId: props.spaceId,
    ...values,
  })
  if (res.data.code === 0 && res.data.data) {
    message.success('Edit success!')
    closeModal()
    props.onSuccess?.()
  } else {
    message.error('Edit failed, ' + res.data.message)
  }
}

</script>


<style scoped>
</style>

