<template>
  <div id="spaceManagePage">
    <a-flex justify="space-between">
      <h2>Space Manage</h2>
      <a-space>
        <a-button type="primary" href="/add_space" target="_blank">+ Add Space</a-button>
      </a-space>
    </a-flex>
    <div style="margin-bottom: 16px" />
    <!--  search form  -->
    <a-form layout="inline" :model="searchParams" @finish="doSearch">
      <a-form-item label="spaceName" name="spaceName">
        <a-input v-model:value="searchParams.spaceName" placeholder="Please enter space name" allow-clear />
      </a-form-item>
      <a-form-item label="spaceLevel" name="spaceLevel">
        <a-select
          v-model:value="searchParams.spaceLevel"
          :options="SPACE_LEVEL_OPTIONS"
          placeholder="Please select space level"
          style="min-width: 180px"
          allow-clear
        />
      </a-form-item>
      <a-form-item label="userId" name="userId">
        <a-input v-model:value="searchParams.userId" placeholder="Please enter user Id" allow-clear />
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
        <!-- space level -->
        <template v-if="column.dataIndex === 'spaceLevel'">
          <a-tag>{{ SPACE_LEVEL_MAP[record.spaceLevel] }}</a-tag>
        </template>
        <!-- space use info -->
        <template v-if="column.dataIndex === 'spaceUseInfo'">
          <div>Size: {{ formatSize(record.totalSize) }} / {{ formatSize(record.maxSize) }}</div>
          <div>Count: {{ record.totalCount }} / {{ record.maxCount }}</div>
        </template>
        <template v-else-if="column.dataIndex === 'createTime'">
          {{ dayjs(record.createTime).format('YYYY-MM-DD HH:mm:ss') }}
        </template>
        <template v-else-if="column.dataIndex === 'editTime'">
          {{ dayjs(record.editTime).format('YYYY-MM-DD HH:mm:ss') }}
        </template>
        <template v-else-if="column.key === 'action'">
          <a-space wrap>
            <a-button type="link" :href="`/add_space?id=${record.id}`" target="_blank">
              Edit
            </a-button>
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
  deleteSpaceUsingPost,
  listSpaceByPageUsingPost
} from '@/api/spaceController'
import { ref, reactive, onMounted, computed } from "vue"
import { message } from 'ant-design-vue'
import dayjs from "dayjs"
import {
  SPACE_LEVEL_MAP, SPACE_LEVEL_OPTIONS
} from '../../constants/space'
import { formatSize } from '../../utils'
const columns = [
  {
    title: 'id',
    dataIndex: 'id',
    width: 80,
  },
  {
    title: 'spaceName',
    dataIndex: 'spaceName',
  },
  {
    title: 'spaceLevel',
    dataIndex: 'spaceLevel',
  },
  {
    title: 'spaceUseInfo',
    dataIndex: 'spaceUseInfo',
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
const dataList = ref([])
const total = ref(0)

// search params
const searchParams = reactive<API.SpaceQueryRequest>({
  current: 1,
  pageSize: 10,
  sortField: 'createTime',
  sortOrder: 'descend',
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

// fetch data
const fetchData = async () => {
  const res = await listSpaceByPageUsingPost({
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

// search form submit
const doSearch = () => {
  // init current page
  searchParams.current = 1
  fetchData()
}

// Table change processing
const doTableChange = (page: any) => {
  searchParams.current = page.current
  searchParams.pageSize = page.pageSize
  fetchData()
}


// delete space
const doDelete = async (id: string) => {
  if (!id) {
    return
  }
  const res = await deleteSpaceUsingPost({ id })
  if (res.data.code === 0) {
    message.success('Delete success!')
    // refresh data
    fetchData()
  } else {
    message.error('Delete Error: ' + res.data.message)
  }
}

</script>


