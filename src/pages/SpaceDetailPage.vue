<template>
  <div id="spaceDetailPage">
    <!-- space info -->
    <a-flex justify="space-between" align="middle">
      <h2>
        <div style="margin-bottom: 8px" />
        {{ space.spaceName }} (Private Space)
        <span
          v-if="space.spaceLevel !== undefined"
          class="tag-style"
        >
          <template v-if="space.spaceLevel === 0">🌱 Starter</template>
          <template v-else-if="space.spaceLevel === 1">🚀 Pro</template>
          <template v-else-if="space.spaceLevel === 2">👑 Premium</template>
        </span>
      </h2>
      <h2>
        <a-space size="middle" >
          <a-button type="primary" :href="`/add_picture?spaceId=${id}`" target="_blank">
            + Add Picture
          </a-button>
          <a-button
            type="primary"
            ghost
            :icon="h(BarChartOutlined)"
            :href="`/space_analyze?spaceId=${id}`"
            target="_blank"
          >
            Space Analysis
          </a-button>
          <a-button :icon="h(EditOutlined)" @click="doBatchEdit"> Batch Edit</a-button>
          <a-tooltip
            :title="`Space Usage: ${formatSize(space.totalSize)} / ${formatSize(space.maxSize)}`"
          >
            <a-progress
              type="circle"
              :percent="((space.totalSize * 100) / space.maxSize).toFixed(1)"
              :size="42"
            />
          </a-tooltip>
        </a-space>
      </h2>
    </a-flex>
    <div style="margin-bottom: 16px" />
    <!-- search form -->
    <PictureSearchForm :onSearch="onSearch" />
    <div style="margin-bottom: 16px" />
    <!-- search picture by color -->
    <a-form-item label="Search Picture by Color" style="margin-top: 16px">
      <color-picker format="hex" @pureColorChange="onColorChange" /> Top 12 sorted by color similarity
    </a-form-item>
    <div style="margin-bottom: 16px" />
    <!-- picture list -->
    <PictureList :dataList="dataList" :loading="loading" :showOp="true" :onReload="fetchData"/>
    <!-- pagination -->
    <a-pagination
      style="text-align: right"
      v-model:current="searchParams.current"
      v-model:pageSize="searchParams.pageSize"
      :total="total"
      :show-total="() => `Picture Count: ${total} / ${space.maxCount}`"
      @change="onPageChange"
    />
    <BatchEditPictureModal
      ref="batchEditPictureModalRef"
      :spaceId="id"
      :pictureList="dataList"
      :onSuccess="onBatchEditPictureSuccess"
    />
  </div>
</template>

<script setup lang="ts">
import { reactive, ref, onMounted, computed, h } from "vue"
import { message } from 'ant-design-vue'
import { downloadImage, formatSize } from '@/utils'
import { EditOutlined, BarChartOutlined, DeleteOutlined, DownloadOutlined } from "@ant-design/icons-vue";
import { useLoginUserStore } from '@/stores/useLoginUserStore'
import { useRouter } from "vue-router";
import { getSpaceVoByIdUsingGet, listSpaceVoByPageUsingPost } from '@/api/spaceController'
import PictureList from '@/components/PictureList.vue'
import { listPictureVoByPageUsingPost, searchPictureByColorUsingPost } from '@/api/pictureController'
import PictureSearchForm from '@/components/PictureSearchForm.vue'
import { ColorPicker } from "vue3-colorpicker"
import "vue3-colorpicker/style.css"
import BatchEditPictureModal from '@/components/BatchEditPictureModal.vue'

const props = defineProps<{
  id: string | number
}>()
const space = ref<API.SpaceVO>({})

// ---------------- fetch space detail ------------
const fetchSpaceDetail = async () => {
  try {
    const res = await getSpaceVoByIdUsingGet({
      id: props.id,
    })
    if (res.data.code === 0 && res.data.data) {
      space.value = res.data.data
    } else {
      message.error('Fetch Space Detail Error, ' + res.data.message)
    }
  } catch (e: any) {
    message.error('Fetch Space Detail Error, ' + e.message)
  }
}

onMounted(() => {
  fetchSpaceDetail()
})

// ---------------- fetch picture list ------------
// data
const dataList = ref([])
const total = ref(0)
const loading = ref(true)

// search params
const searchParams = ref<API.PictureQueryRequest>({
  current: 1,
  pageSize: 12,
  sortField: 'createTime',
  sortOrder: 'descend',
})

// page params
const onPageChange = (page, pageSize) => {
  searchParams.value.current = page
  searchParams.value.pageSize = pageSize
  fetchData()
}

// call backend to fetch pictures
const fetchData = async () => {
  loading.value = true
  const params = {
    spaceId: props.id,
    ...searchParams.value,
  }
  const res = await listPictureVoByPageUsingPost(params)
  if (res.data.data) {
    dataList.value = res.data.data.records ?? []
    total.value = res.data.data.total ?? 0
  } else {
    message.error('Fetch Picture Error, ' + res.data.message)
  }
  loading.value = false
}

const onSearch = (newSearchParams: API.PictureQueryRequest) => {
  searchParams.value = {
    ...searchParams.value,
    ...newSearchParams,
    current: 1,
  }
  fetchData()
}

onMounted(() => {
  fetchData()
})

const onColorChange = async (color: string) => {
  loading.value = true
  const res = await searchPictureByColorUsingPost({
    picColor: color,
    spaceId: props.id,
  })
  if (res.data.code === 0 && res.data.data) {
    const data = res.data.data ?? [];
    dataList.value = data;
    total.value = data.length;
  } else {
    message.error('Search Picture by Color failed, ' + res.data.message)
  }
  loading.value = false
}

const batchEditPictureModalRef = ref()

// after success in batch edit, refresh page data
const onBatchEditPictureSuccess = () => {
  fetchData()
}

// open batch edit modal
const doBatchEdit = () => {
  if (batchEditPictureModalRef.value) {
    batchEditPictureModalRef.value.openModal()
  }
}


</script>

<style scoped>
#spaceDetailPage {
  margin-bottom: 16px;
}
.tag-style {
  margin-left: 12px;
  background-color: #f0f0f0;
  padding: 2px 10px;
  border-radius: 12px;
  font-size: 14px;
  font-weight: 500;
  display: inline-block;
}
</style>
