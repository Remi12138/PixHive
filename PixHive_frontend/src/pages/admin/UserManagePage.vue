<template>
  <div id="userManagePage">
    <!--  search form  -->
    <a-form layout="inline" :model="searchParams" @finish="doSearch">
      <a-form-item label="user account">
        <a-input v-model:value="searchParams.userAccount" placeholder="enter user account" allow-clear/>
      </a-form-item>
      <a-form-item label="user name">
        <a-input v-model:value="searchParams.userName" placeholder="enter user name" allow-clear/>
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
    >
    <template #bodyCell="{ column, record }">
        <template v-if="column.dataIndex === 'userAvatar'">
          <a-image :src="record.userAvatar" :width="120" />
        </template>
        <template v-else-if="column.dataIndex === 'userRole'">
          <div v-if="record.userRole === 'admin'">
            <a-tag color="green">Admin</a-tag>
          </div>
          <div v-else>
            <a-tag color="blue">User</a-tag>
          </div>
        </template>
        <template v-else-if="column.dataIndex === 'createTime'">
          {{ dayjs(record.createTime).format('YYYY-MM-DD HH:mm:ss') }}
        </template>
        <template v-else-if="column.key === 'action'">
          <a-button danger @click="doDelete(record.id)">Delete</a-button>
        </template>
      </template>
    </a-table>
  </div>
</template>
<script lang="ts" setup>
import { SmileOutlined, DownOutlined } from '@ant-design/icons-vue';
import { deleteUserUsingPost, listUserVoByPageUsingPost } from '@/api/userController'
import { ref, reactive, onMounted, computed } from "vue"
import { message } from 'ant-design-vue'
import dayjs from "dayjs"
const columns = [
  {
    title: 'id',
    // dataIndex: query from database
    dataIndex: 'id',
  },
  {
    title: 'userAccount',
    dataIndex: 'userAccount',
  },
  {
    title: 'userName',
    dataIndex: 'userName',
  },
  {
    title: 'userAvatar',
    dataIndex: 'userAvatar',
  },
  {
    title: 'userProfile',
    dataIndex: 'userProfile',
  },
  {
    title: 'userRole',
    dataIndex: 'userRole',
  },
  {
    title: 'createTime',
    dataIndex: 'createTime',
  },
  {
    title: 'action',
    key: 'action',
  },
]

// init data
const dataList = ref<API.UserVO[]>([])
const total = ref(0)

// search params
const searchParams = reactive<API.UserQueryRequest>({
  current: 1,
  pageSize: 10,
  sortField: "createTime",
  sortOrder: "ascend",
})

// fetch data
const fetchData = async () => {
  const res = await listUserVoByPageUsingPost({
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

// delete user
const doDelete = async (id: string) => {
  if (!id) {
    return
  }
  const res = await deleteUserUsingPost({ id })
  if (res.data.code === 0) {
    message.success('Delete success!')
    // refresh data
    fetchData()
  } else {
    message.error('Delete Error: ' + res.data.message)
  }
}
</script>


