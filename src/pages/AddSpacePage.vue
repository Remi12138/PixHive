<template>
  <div id="addSpacePage">
    <h2 style="margin-bottom: 16px">
      {{ route.query?.id ? 'Edit Space' : 'Create Space' }}
    </h2>

    <a-form layout="vertical" :model="spaceForm" @finish="handleSubmit">
      <a-form-item label="Space Name" name="spaceName">
        <a-input v-model:value="spaceForm.spaceName" placeholder="Please enter space name" allow-clear />
      </a-form-item>
      <a-form-item label="Space Level" name="spaceLevel">
        <a-select
          v-model:value="spaceForm.spaceLevel"
          :options="SPACE_LEVEL_OPTIONS"
          placeholder="Please select space level"
          style="min-width: 180px"
          allow-clear
        />
      </a-form-item>
      <a-form-item>
        <a-button type="primary" html-type="submit" style="width: 100%" :loading="loading">
          Submit
        </a-button>
      </a-form-item>
    </a-form>
    <!-- space level intro -->
    <a-card title="Choose Your Space Level">
      <a-typography-paragraph>
        * Currently, only the Starter version is supported. If you need to upgrade space, please contact
        <a href="https://linkedin.com/in/xianjing-jin-huang" target="_blank">Jin</a>
      </a-typography-paragraph>
      <div style="display: flex; gap: 16px; flex-wrap: wrap">
        <a-card
          v-for="spaceLevel in spaceLevelList"
          :key="spaceLevel.text"
          style="width: 200px; text-align: center; border: 1px solid #f0f0f0; border-radius: 10px"
        >
          <div style="font-size: 32px; margin-bottom: 12px">
            <template v-if="spaceLevel.value === 0">🌱</template>
            <template v-else-if="spaceLevel.value === 1">🚀</template>
            <template v-else-if="spaceLevel.value === 2">👑</template>
          </div>
          <div style="font-weight: 700">{{ spaceLevel.text }}</div>
          <div>Size: {{ formatSize(spaceLevel.maxSize) }}</div>
          <div>Count: {{ spaceLevel.maxCount }}</div>
        </a-card>
      </div>
    </a-card>
  </div>

</template>

<script setup lang="ts">
import { reactive, ref, onMounted } from "vue";
import {
  addSpaceUsingPost,
  editSpaceUsingPost,
  getSpaceVoByIdUsingGet,
  updateSpaceUsingPost
} from '@/api/spaceController'
import { useRouter, useRoute } from "vue-router";
import { message } from 'ant-design-vue'
import { SPACE_LEVEL_ENUM, SPACE_LEVEL_OPTIONS } from '@/constants/space'
import { formatSize } from '../utils'
import { listSpaceLevelUsingGet } from '@/api/spaceController'

const space = ref<API.SpaceVO>();
const spaceForm = reactive<API.SpaceAddRequest | API.SpaceUpdateRequest>({
  spaceName: '',
  spaceLevel: SPACE_LEVEL_ENUM.STARTER,
})
const loading = ref(false)
const router = useRouter()

const spaceLevelList = ref<API.SpaceLevel[]>([])

// fetch SpaceLevelList
const fetchSpaceLevelList = async () => {
  const res = await listSpaceLevelUsingGet()
  if (res.data.code === 0 && res.data.data) {
    spaceLevelList.value = res.data.data
  } else {
    message.error('Load space level failed, ' + res.data.message)
  }
}

onMounted(() => {
  fetchSpaceLevelList()
})


/**
 * submit form
 * @param values
 */
const handleSubmit = async (values: any) => {
  const spaceId = oldSpace.value?.id
  loading.value = true
  let res
  if (spaceId) { // update
    res = await updateSpaceUsingPost({
      id: spaceId,
      ...spaceForm,
    })
  } else { // create
    res = await addSpaceUsingPost({
      ...spaceForm,
    })
  }
  if (res.data.code === 0 && res.data.data) {
    message.success("Create space success!")
    let path = `/space/${spaceId ?? res.data.data}`
    router.push({
      path,
    })
  } else {
    message.error('Create space failed!，' + res.data.message)
  }
  loading.value = false
}


const route = useRoute()
const oldSpace = ref<API.SpaceVO>()

// get Old Space
const getOldSpace = async () => {
  const id = route.query?.id
  if (id) {
    const res = await getSpaceVoByIdUsingGet({
      id: id,
    })
    if (res.data.code === 0 && res.data.data) {
      const data = res.data.data
      oldSpace.value = data
      spaceForm.spaceName = data.spaceName
      spaceForm.spaceLevel = data.spaceLevel
    }
  }
}

onMounted(() => {
  getOldSpace()
})
</script>

<style scoped>
#addSpacePage {
  max-width: 720px;
  margin: 0 auto;
}
</style>
