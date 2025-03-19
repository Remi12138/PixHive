<template>
  <div id="pictureManagePage">
    <!--  search form  -->
    <a-form layout="inline" :model="searchParams" @finish="doSearch">
      <a-form-item label="Key words" name="searchText">
        <a-input
          v-model:value="searchParams.searchText"
          placeholder="name or introduction"
          allow-clear
        />
      </a-form-item>
      <a-form-item label="category" name="category">
        <a-input v-model:value="searchParams.category" placeholder="enter category" allow-clear />
      </a-form-item>
      <a-form-item label="tags" name="tags">
        <a-select
          v-model:value="searchParams.tags"
          mode="tags"
          placeholder="enter tags"
          style="min-width: 180px"
          allow-clear
        />
      </a-form-item>
      <a-form-item>
        <a-button type="primary" html-type="submit">Search</a-button>
      </a-form-item>
    </a-form>

    <div style="margin-bottom: 16px" />
    <!--  result table  -->
    <a-table
      :columns="columns"
      :data-source="dataList"
      :pagination="pagination"
      @change="doTableChange"
      :scroll="{ x: 'max-content' }"
    >
      <template #bodyCell="{ column, record }">
        <template v-if="column.dataIndex === 'url'">
          <a-image :src="record.url" :width="120" />
        </template>
        <!-- tags -->
        <template v-if="column.dataIndex === 'tags'">
          <a-space wrap>
            <a-tag v-for="tag in JSON.parse(record.tags || '[]')" :key="tag">{{ tag }}</a-tag>
          </a-space>
        </template>
        <!-- pic info -->
        <template v-if="column.dataIndex === 'picInfo'">
          <div>Format：{{ record.picFormat }}</div>
          <div>Width：{{ record.picWidth }}</div>
          <div>Height：{{ record.picHeight }}</div>
          <div>Scale：{{ record.picScale }}</div>
          <div>Size：{{ (record.picSize / 1024).toFixed(2) }}KB</div>
        </template>
        <template v-else-if="column.dataIndex === 'createTime'">
          {{ dayjs(record.createTime).format('YYYY-MM-DD HH:mm:ss') }}
        </template>
        <template v-else-if="column.dataIndex === 'editTime'">
          {{ dayjs(record.editTime).format('YYYY-MM-DD HH:mm:ss') }}
        </template>
        <template v-else-if="column.key === 'action'">
          <a-space>
            <a-button type="link" :href="`/add_picture?id=${record.id}`" target="_blank">Edit</a-button>
            <a-button type="link" danger @click="doDelete(record.id)">Delete</a-button>
          </a-space>
        </template>
      </template>
    </a-table>
  </div>
</template>
<script lang="ts" setup>

import { SmileOutlined, DownOutlined } from '@ant-design/icons-vue';
import {
  deletePictureUsingPost,
  listPictureByPageUsingPost
} from '@/api/pictureController'
import { ref, reactive, onMounted, computed } from "vue"
import { message } from 'ant-design-vue'
import dayjs from "dayjs"
const columns = [
  {
    title: 'id',
    dataIndex: 'id',
    width: 80,
  },
  {
    title: 'picture',
    dataIndex: 'url',
  },
  {
    title: 'name',
    dataIndex: 'name',
  },
  {
    title: 'introduction',
    dataIndex: 'introduction',
    ellipsis: true,
  },
  {
    title: 'category',
    dataIndex: 'category',
  },
  {
    title: 'tags',
    dataIndex: 'tags',
  },
  {
    title: 'picInfo',
    dataIndex: 'picInfo',
  },
  {
    title: 'userId',
    dataIndex: 'userId',
    width: 80,
  },
  {
    title: 'createTime',
    dataIndex: 'createTime',
  },
  {
    title: 'editTime',
    dataIndex: 'editTime',
  },
  {
    title: 'action',
    key: 'action',
  },
]

// init data
const dataList = ref<API.Picture[]>([])
const total = ref(0)

// search params
const searchParams = reactive<API.PictureQueryRequest>({
  current: 1,
  pageSize: 10,
  sortField: "createTime",
  sortOrder: "descend",
})

// fetch data
const fetchData = async () => {
  const res = await listPictureByPageUsingPost({
    ...searchParams
  })
  if (res.data.data) {
    dataList.value = res.data.data.records ?? []
    total.value = res.data.data.total ?? 0
  } else {
    message.error('Fetch data error: ' + res.data.message)
  }
}

// Request once when the page loads
onMounted(() => {
  fetchData()
})

// page params
const pagination = computed(() => {
  return {
    current: searchParams.current ?? 1,
    pageSize: searchParams.pageSize ?? 10,
    total: total.value,
    showSizeChanger: true,
    showTotal: (total) => `Total: ${total}`,
  }
})

// Table change processing
const doTableChange = (page: any) => {
  searchParams.current = page.current
  searchParams.pageSize = page.pageSize
  fetchData()
}

// search form submit
const doSearch = () => {
  // init current page
  searchParams.current = 1
  fetchData()
}

// delete picture
const doDelete = async (id: string) => {
  if (!id) {
    return
  }
  const res = await deletePictureUsingPost({ id })
  if (res.data.code === 0) {
    message.success('Delete success!')
    // refresh data
    fetchData()
  } else {
    message.error('Delete Error: ' + res.data.message)
  }
}
</script>


