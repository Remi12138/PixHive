<template>
  <div id="addPictureBatchPage">
    <h2 style="margin-bottom: 16px">Add Batch Pictures</h2>
    <a-form layout="vertical" :model="formData" @finish="handleSubmit">
      <a-form-item label="Search Text" name="searchText">
        <a-input v-model:value="formData.searchText" placeholder="Please enter search text" />
      </a-form-item>
      <a-form-item label="Batch Size" name="count">
        <a-input-number
          v-model:value="formData.count"
          placeholder="Please enter search count"
          style="min-width: 180px"
          :min="1"
          :max="30"
          allow-clear
        />
      </a-form-item>
      <a-form-item label="Name Prefix (Optional)" name="namePrefix">
        <a-input v-model:value="formData.namePrefix" placeholder="Please enter name prefix (The serial number is automatically added)" />
      </a-form-item>
      <a-form-item>
        <a-button type="primary" html-type="submit" style="width: 100%" :loading="loading">
          Submit
        </a-button>
      </a-form-item>
    </a-form>
  </div>

</template>

<script setup lang="ts">
import { reactive, ref, onMounted } from "vue";
import {
  uploadPictureByBatchUsingPost
} from '@/api/pictureController'
import { useRouter, useRoute } from "vue-router";
import { message } from 'ant-design-vue'

const formData = reactive<API.PictureUploadByBatchRequest>({
  count: 10,
})
const loading = ref(false)

const router = useRouter()

const handleSubmit = async (values: any) => {
  loading.value = true;
  const res = await uploadPictureByBatchUsingPost({
    ...formData,
  })
  if (res.data.code === 0 && res.data.data) {
    message.success(`Added ${res.data.data} pictures in total successfully!`)
    router.push({
      path: '/',
    })
  } else {
    message.error('Adding pictures filed，' + res.data.message)
  }
  loading.value = false;
}




</script>

<style scoped>
#addPicturePage {
  max-width: 720px;
  margin: 0 auto;
}
</style>
