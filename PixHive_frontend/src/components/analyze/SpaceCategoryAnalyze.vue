<template>
  <div class="space-category-analyze">
    <a-card title="Space Category Analyze" style="width: 100%">
      <v-chart :option="options" style="height: 320px; max-width: 100%;" :loading="loading" />
    </a-card>
  </div>

</template>

<script setup lang="ts">
import VChart from "vue-echarts";
import "echarts";
import { getSpaceCategoryAnalyzeUsingPost } from '@/api/spaceAnalyzeController'
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

const dataList = ref<API.SpaceCategoryAnalyzeResponse[]>([])
const loading = ref(true)

const fetchData = async () => {
  loading.value = true
  const res = await getSpaceCategoryAnalyzeUsingPost({
    queryAll: props.queryAll,
    queryPublic: props.queryPublic,
    spaceId: props.spaceId,
  })
  if (res.data.code === 0) {
    dataList.value = res.data.data ?? []
  } else {
    message.error('getSpaceCategoryAnalyzeUsingPost failed, ' + res.data.message)
  }
  loading.value = false
}

const options = computed(() => {
  const categories = dataList.value.map((item) => item.category)
  const countData = dataList.value.map((item) => item.count)
  const sizeData = dataList.value.map((item) => (item.totalSize / (1024 * 1024)).toFixed(2)) // to MB

  return {
    tooltip: { trigger: 'axis' },
    legend: { data: ['Count', 'Size'], top: 'bottom' },
    xAxis: { type: 'category', data: categories },
    yAxis: [
      {
        type: 'value',
        name: 'Count',
        axisLine: { show: true, lineStyle: { color: '#5470C6' } },
      },
      {
        type: 'value',
        name: 'Size (MB)',
        position: 'right',
        axisLine: { show: true, lineStyle: { color: '#91CC75' } },
        splitLine: {
          lineStyle: {
            color: '#91CC75', // Grid color
            type: 'dashed', // Line Style: 'solid', 'dashed', 'dotted'
          },
        },
      },
    ],
    series: [
      { name: 'Count', type: 'bar', data: countData, yAxisIndex: 0 },
      { name: 'Size', type: 'bar', data: sizeData, yAxisIndex: 1 },
    ],
  }
})

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
