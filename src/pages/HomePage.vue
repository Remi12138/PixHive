<template>
  <div id="homePage">
    <!-- search bar -->
    <div class="search-bar">
      <a-input-search
        placeholder="Search through tons of images"
        v-model:value="searchParams.searchText"
        enter-button="Search"
        size="large"
        @search="doSearch"
      />
    </div>

    <!-- category + tag -->
    <a-tabs v-model:activeKey="selectedCategory" @change="doSearch">
      <a-tab-pane key="all" tab="All" />
      <a-tab-pane v-for="category in categoryList" :key="category" :tab="category" />
    </a-tabs>
    <div class="tag-bar">
      <span style="margin-right: 8px">Tag: </span>
      <a-space :size="[0, 8]" wrap>
        <a-checkable-tag
          v-for="(tag, index) in tagList"
          :key="tag"
          v-model:checked="selectedTagList[index]"
          @change="doSearch"
        >
          {{ tag }}
        </a-checkable-tag>
      </a-space>
    </div>

    <!-- picture list -->
    <PictureList :dataList="dataList" :loading="loading" />
    <!-- pagination -->
    <a-pagination
      style="text-align: right"
      v-model:current="searchParams.current"
      v-model:pageSize="searchParams.pageSize"
      :total="total"
      @change="onPageChange"
    />
  </div>
</template>

<script setup lang="ts">
import { reactive, ref, onMounted, computed } from "vue"
import { message } from 'ant-design-vue'
import { listPictureTagCategoryUsingGet, listPictureVoByPageUsingPost } from '@/api/pictureController'
import { useRouter } from "vue-router";
import PictureList from '@/components/PictureList.vue'

const dataList = ref([])
const total = ref(0)
const loading = ref(true)

// search params
const searchParams = reactive<API.PictureQueryRequest>({
  current: 1,
  pageSize: 12,
  sortField: 'createTime',
  sortOrder: 'descend',
})

// page params
const onPageChange = (page: number, pageSize: number) => {
    searchParams.current = page
    searchParams.pageSize = pageSize
    fetchData()
  }

// fetch data
const fetchData = async () => {
  loading.value = true

  const params = {
    ...searchParams,
    tags: [] as string[],
  }
  if (selectedCategory.value !== 'all') {
    params.category = selectedCategory.value
  }
  selectedTagList.value.forEach((useTag, index) => {
    if (useTag) {
      params.tags.push(tagList.value[index])
    }
  })
  const res = await listPictureVoByPageUsingPost(params)
  if (res.data.data) {
    dataList.value = res.data.data.records ?? []
    total.value = res.data.data.total ?? 0
  } else {
    message.error('Get picture data error，' + res.data.message)
  }
  loading.value = false
}

onMounted(() => {
  fetchData()
})

const doSearch = () => {
  // Reset search params
  searchParams.current = 1
  fetchData()
}

const categoryList = ref<string[]>([])
const selectedCategory = ref<string>('all')
const tagList = ref<string[]>([])
const selectedTagList = ref<string[]>([])

// get Tag/Category options
const getTagCategoryOptions = async () => {
  const res = await listPictureTagCategoryUsingGet()
  if (res.data.code === 0 && res.data.data) {
    // Convert to the format accepted by the drop-down option component
    categoryList.value = res.data.data.categoryList ?? []
    tagList.value = res.data.data.tagList ?? []
  } else {
    message.error('get Tag/Category options failed，' + res.data.message)
  }
}

onMounted(() => {
  getTagCategoryOptions()
})

</script>

<style scoped>
#homePage {
  margin-bottom: 16px;
}
#homePage .search-bar {
  max-width: 480px;
  margin: 0 auto 16px;
}
#homePage .tag-bar {
  margin-bottom: 16px;
}
</style>
