<template>
  <div class="space-rank-analyze">
    <a-card title="Space Usage Rank" style="width: 100%">
      <v-chart :option="options" style="height: 320px" />
    </a-card>
  </div>
</template>

<script setup lang="ts">
import VChart from "vue-echarts";
import "echarts";
import { getSpaceCategoryAnalyzeUsingPost, getSpaceRankAnalyzeUsingPost } from '@/api/spaceAnalyzeController'
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

const dataList = ref<API.Space[]>([])
const loading = ref(true)

const fetchData = async () => {
  loading.value = true
  const res = await getSpaceRankAnalyzeUsingPost({
    // queryAll: props.queryAll,
    // queryPublic: props.queryPublic,
    // spaceId: props.spaceId,
    topN: 10, // default: 10
  })
  if (res.data.code === 0) {
    dataList.value = res.data.data ?? []
  } else {
    message.error('getSpaceRankAnalyzeUsingPost failed, ' + res.data.message)
  }
  loading.value = false
}


const options = computed(() => {
  const spaceNames = dataList.value.map((item) => item.spaceName)
  const usageData = dataList.value.map((item) => (item.totalSize / (1024 * 1024)).toFixed(2)) // to MB

  return {
    tooltip: { trigger: 'axis' },
    xAxis: {
      type: 'category',
      data: spaceNames,
    },
    yAxis: {
      type: 'value',
      name: 'Space Usage (MB)',
    },
    series: [
      {
        name: 'Space Usage (MB)',
        type: 'bar',
        data: usageData,
        itemStyle: {
          color: '#5470C6',
        },
      },
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
