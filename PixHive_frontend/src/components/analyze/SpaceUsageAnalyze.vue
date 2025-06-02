<template>
  <div class="space-usage-analyze">
    <a-flex gap="middle">
      <a-card title="Memory Space" style="width: 50%">
        <div style="height: 320px; text-align: center">
          <h3>{{ formatSize(data.usedSize) }} / {{ data.maxSize ? formatSize(data.maxSize) : 'Unlimited' }}</h3>
          <a-progress type="dashboard" :percent="data.sizeUsageRatio ?? 0" />
        </div>
      </a-card>
      <a-card title="Picture Count" style="width: 50%">
        <div style="height: 320px; text-align: center">
          <h3>{{ data.usedCount }} / {{ data.maxCount ?? 'Unlimited' }} </h3>
          <a-progress type="dashboard" :percent="data.countUsageRatio ?? 0" />
        </div>
      </a-card>
    </a-flex>
  </div>
</template>

<script setup lang="ts">
import { getSpaceUsageAnalyzeUsingPost } from '@/api/spaceAnalyzeController'
import { ref, watchEffect } from 'vue'
import { message } from 'ant-design-vue'
import { formatSize } from '../../utils'

interface Props {
  queryAll?: boolean
  queryPublic?: boolean
  spaceId?: string
}

const props = withDefaults(defineProps<Props>(), {
  queryAll: false,
  queryPublic: false,
})

const data = ref<API.SpaceUsageAnalyzeResponse>({})
const loading = ref(true)

const fetchData = async () => {
  loading.value = true
  const res = await getSpaceUsageAnalyzeUsingPost({
    queryAll: props.queryAll,
    queryPublic: props.queryPublic,
    spaceId: props.spaceId,
  })
  if (res.data.code === 0 && res.data.data) {
    data.value = res.data.data
  } else {
    message.error('getSpaceUsageAnalyzeUsingPost failed, ' + res.data.message)
  }
  loading.value = false
}

/**
 * Listen for variables in fetchData,
 * if they change, trigger the reloading of it again
 */
watchEffect(() => {
  fetchData()
})


</script>

<style scoped>
</style>
