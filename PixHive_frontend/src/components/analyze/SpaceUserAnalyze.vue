<template>
  <div class="space-user-analyze">
    <a-card title="Space User Behavior Analyze" style="width: 100%">
      <v-chart :option="options" style="height: 320px; max-width: 100%" :loading="loading" />
      <template #extra>
        <a-space>
          <a-segmented v-model:value="timeDimension" :options="timeDimensionOptions" />
          <a-input-search placeholder="Please enter User id" enter-button="Search User" @search="doSearch" />
        </a-space>
      </template>
    </a-card>
  </div>
</template>

<script setup lang="ts">
import VChart from "vue-echarts";
import "echarts";
import { getSpaceCategoryAnalyzeUsingPost, getSpaceUserAnalyzeUsingPost } from '@/api/spaceAnalyzeController'
import { computed, ref, watchEffect } from 'vue'
import { message } from 'ant-design-vue'

interface Props {
  queryAll?: boolean
  queryPublic?: boolean
  spaceId?: string
}

const props = withDefaults(defineProps<Props>(), {
  queryAll: false,
  queryPublic: false,
})

const dataList = ref<API.SpaceUserAnalyzeResponse[]>([])
const loading = ref(true)

const options = computed(() => {
  // const periods = dataList.value.map((item) => item.period) // Time Period
  const periods = dataList.value.map((item) => {
    const raw = item.period

    // If the format is exactly 6 digits (like "202514"), treat it as year-week
    if (/^\d{6}$/.test(raw)) {
      const year = raw.slice(0, 4)
      const week = raw.slice(4)
      return `Week ${week}, ${year}`
    }

    // Otherwise, leave it as-is (day or month format)
    return raw
  })

  const counts = dataList.value.map((item) => item.count) // Upload Quantity

  return {
    tooltip: { trigger: 'axis' },
    xAxis: { type: 'category', data: periods, name: 'Time Period' },
    yAxis: { type: 'value', name: 'Upload Quantity' },
    series: [
      {
        name: 'Upload Quantity',
        type: 'line',
        data: counts,
        smooth: true,
        emphasis: {
          focus: 'series',
        },
      },
    ],
  }
})

const userId = ref<string>()
const timeDimension = ref<string>('day')
const timeDimensionOptions = [
  {
    label: 'day',
    value: 'day',
  },
  {
    label: 'week',
    value: 'week',
  },
  {
    label: 'month',
    value: 'month',
  },
]

const doSearch = (value: string) => {
  userId.value = value
}

const fetchData = async () => {
  loading.value = true
  const res = await getSpaceUserAnalyzeUsingPost({
    queryAll: props.queryAll,
    queryPublic: props.queryPublic,
    spaceId: props.spaceId,
    timeDimension: timeDimension.value,
    userId: userId.value,
  })
  if (res.data.code === 0) {
    dataList.value = res.data.data ?? []
  } else {
    message.error('getSpaceUserAnalyzeUsingPost failed, ' + res.data.message)
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
