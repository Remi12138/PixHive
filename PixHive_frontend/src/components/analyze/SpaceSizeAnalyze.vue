<template>
  <div class="space-size-analyze">
    <a-card title="Space Size Analyze" style="width: 100%">
      <v-chart :option="options" style="height: 320px; max-width: 100%" :loading="loading" />
    </a-card>
  </div>


</template>

<script setup lang="ts">
import VChart from "vue-echarts";
import "echarts";
import { getSpaceCategoryAnalyzeUsingPost, getSpaceSizeAnalyzeUsingPost } from '@/api/spaceAnalyzeController'
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

const dataList = ref<API.SpaceSizeAnalyzeResponse[]>([])
const loading = ref(true)

const fetchData = async () => {
  loading.value = true
  const res = await getSpaceSizeAnalyzeUsingPost({
    queryAll: props.queryAll,
    queryPublic: props.queryPublic,
    spaceId: props.spaceId,
  })
  if (res.data.code === 0) {
    dataList.value = res.data.data ?? []
  } else {
    message.error('getSpaceSizeAnalyzeUsingPost failed, ' + res.data.message)
  }
  loading.value = false
}


const options = computed(() => {
  const pieData = dataList.value.map((item) => ({
    name: item.sizeRange,
    value: item.count,
  }))

  return {
    tooltip: {
      trigger: 'item',
      formatter: '{a} <br/>{b}: {c} ({d}%)',
    },
    legend: {
      top: 'bottom',
    },
    series: [
      {
        name: 'Picture Size',
        type: 'pie',
        radius: '50%',
        data: pieData,
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
