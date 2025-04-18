<template>
  <div class="picture-search-form">
    <!-- search form -->
    <a-form layout="inline" :model="searchParams" @finish="doSearch">
      <a-form-item label="Key Words" name="searchText">
        <a-input
          v-model:value="searchParams.searchText"
          placeholder="name or introduction"
          allow-clear
        />
      </a-form-item>
      <a-form-item label="Category" name="category">
        <a-auto-complete
          v-model:value="searchParams.category"
          style="min-width: 180px"
          :options="categoryOptions"
          placeholder="enter category"
          allowClear
        />
      </a-form-item>
      <a-form-item label="Tags" name="tags">
        <a-select
          v-model:value="searchParams.tags"
          style="min-width: 180px"
          :options="tagOptions"
          mode="tags"
          placeholder="enter tags"
          allowClear
        />
      </a-form-item>
      <a-form-item label="Date" name="">
        <a-range-picker
          style="width: 400px"
          show-time
          v-model:value="dateRange"
          :placeholder="['Start Date', 'End Date']"
          format="YYYY/MM/DD HH:mm:ss"
          :presets="rangePresets"
          @change="onRangeChange"
        />
      </a-form-item>
      <a-form-item label="Name" name="name">
        <a-input v-model:value="searchParams.name" placeholder="enter name" allow-clear />
      </a-form-item>
      <a-form-item label="Introduction" name="introduction">
        <a-input v-model:value="searchParams.introduction" placeholder="enter introduction" allow-clear />
      </a-form-item>
      <a-form-item label="Picture Width" name="picWidth">
        <a-input-number v-model:value="searchParams.picWidth" />
      </a-form-item>
      <a-form-item label="Picture Height" name="picHeight">
        <a-input-number v-model:value="searchParams.picHeight" />
      </a-form-item>
      <a-form-item label="Picture Format" name="picFormat">
        <a-input v-model:value="searchParams.picFormat" placeholder="enter format" allow-clear />
      </a-form-item>
      <a-form-item>
        <a-space>
          <a-button type="primary" html-type="submit" style="width: 96px">Search</a-button>
          <a-button type="primary" html-type="reset" @click="doClear">Reset</a-button>
        </a-space>
      </a-form-item>
    </a-form>
  </div>
</template>
<script lang="ts" setup>

import { SmileOutlined, DownOutlined } from '@ant-design/icons-vue';
import {
  deletePictureUsingPost, doPictureReviewUsingPost,
  listPictureByPageUsingPost, listPictureTagCategoryUsingGet
} from '@/api/pictureController'
import { ref, reactive, onMounted, computed } from "vue"
import { message } from 'ant-design-vue'
import dayjs from "dayjs"
import PictureQueryRequest = API.PictureQueryRequest
interface Props {
  onSearch?: (searchParams: PictureQueryRequest) => void
}

const props = defineProps<Props>()

// search params
const searchParams = reactive<API.PictureQueryRequest>({})

// search form submit
const doSearch = () => {
  props.onSearch?.(searchParams)
}

const dateRange = ref<[]>([])

/**
 * data change
 * @param dates
 * @param dateStrings
 */
const onRangeChange = (dates: any[], dateStrings: string[]) => {
  if (dates?.length >= 2) {
    searchParams.startEditTime = dates[0].toDate()
    searchParams.endEditTime = dates[1].toDate()
  } else { // if dates == null, enter here
    searchParams.startEditTime = undefined
    searchParams.endEditTime = undefined
  }
}

const rangePresets = ref([
  { label: 'last 7 days', value: [dayjs().add(-7, 'd'), dayjs()] },
  { label: 'last 14 days', value: [dayjs().add(-14, 'd'), dayjs()] },
  { label: 'last 30 days', value: [dayjs().add(-30, 'd'), dayjs()] },
  { label: 'last 90 days', value: [dayjs().add(-90, 'd'), dayjs()] },
])

const categoryOptions = ref<string[]>([])
const tagOptions = ref<string[]>([])

// get Tag Category Options
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

const doClear = () => {
  // undefined each value in searchParams
  Object.keys(searchParams).forEach((key) => {
    searchParams[key] = undefined
  })
  // dateRange must be an empty array
  dateRange.value = []
  // re-search
  props.onSearch?.(searchParams)
}
</script>

<style scoped>
.picture-search-form .ant-form-item {
  margin-top: 16px;
}
</style>

