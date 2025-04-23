<template>
  <div id="spaceManagePage">
    <a-flex justify="space-between">
      <h2>Space User Manage</h2>
      <a-space>
        <a-button type="primary" href="/add_space" target="_blank">+ Add Space</a-button>
        <a-button type="primary" ghost href="/space_analyze?queryPublic=1" target="_blank">
          Analyze Public
        </a-button>
        <a-button type="primary" ghost href="/space_analyze?queryAll=1" target="_blank">
          Analyze All
        </a-button>
      </a-space>
    </a-flex>
    <div style="margin-bottom: 16px" />
    <!--  Add user form  -->
    <a-form layout="inline" :model="formData" @finish="handleSubmit">
      <a-form-item label="User ID" name="userId">
        <a-input v-model:value="formData.userId" placeholder="Please enter user id" allow-clear />
      </a-form-item>
      <a-form-item>
        <a-button type="primary" html-type="submit">+ Add</a-button>
      </a-form-item>
    </a-form>
    <div style="margin-bottom: 16px" />
    <!--  User table  -->
    <a-table :columns="columns" :data-source="dataList">
      <template #bodyCell="{ column, record }">
        <template v-if="column.dataIndex === 'userInfo'">
          <a-space>
            <a-avatar :src="record.user?.userAvatar" />
            {{ record.user?.userName }}
          </a-space>
        </template>
        <template v-if="column.dataIndex === 'spaceRole'">
          <a-select
            v-model:value="record.spaceRole"
            :options="SPACE_ROLE_OPTIONS"
            @change="(value) => editSpaceRole(value, record)"
          />
        </template>
        <template v-else-if="column.dataIndex === 'createTime'">
          {{ dayjs(record.createTime).format('YYYY-MM-DD HH:mm:ss') }}
        </template>
        <template v-else-if="column.key === 'action'">
          <a-space wrap>
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
  SPACE_LEVEL_MAP, SPACE_LEVEL_OPTIONS, SPACE_ROLE_OPTIONS
} from '../../constants/space'
import { formatSize } from '../../utils'
import {
  addSpaceUserUsingPost,
  deleteSpaceUserUsingPost,
  editSpaceUserUsingPost,
  listSpaceUserUsingPost
} from '@/api/spaceUserController'

const columns = [
  {
    title: 'User',
    dataIndex: 'userInfo',
  },
  {
    title: 'Role',
    dataIndex: 'spaceRole',
  },
  {
    title: 'createTime',
    dataIndex: 'createTime',
  },
  {
    title: 'Action',
    key: 'action',
  },
]

interface Props {
  id: string
}

const props = defineProps<Props>()

const dataList = ref([])

const fetchData = async () => {
  const spaceId = props.id
  if (!spaceId) {
    return
  }
  const res = await listSpaceUserUsingPost({
    spaceId,
  })
  if (res.data.data) {
    dataList.value = res.data.data ?? []
  } else {
    message.error('listSpaceUserUsingPost failed, ' + res.data.message)
  }
}

onMounted(() => {
  fetchData()
})

const editSpaceRole = async (value, record) => {
  const res = await editSpaceUserUsingPost({
    id: record.id,
    spaceRole: value,
  })
  if (res.data.code === 0) {
    message.success('Modify success!')
  } else {
    message.error('Modify failed, ' + res.data.message)
  }
}

const doDelete = async (id: string) => {
  if (!id) {
    return
  }
  const res = await deleteSpaceUserUsingPost({ id })
  if (res.data.code === 0) {
    message.success('Delete success!')
    // refresh
    fetchData()
  } else {
    message.error('Delete Error: ' + res.data.message)
  }
}

const formData = reactive<API.SpaceUserAddRequest>({})

// add new user to space
const handleSubmit = async () => {
  const spaceId = props.id
  if (!spaceId) {
    return
  }
  const res = await addSpaceUserUsingPost({
    spaceId,
    ...formData,
  })
  if (res.data.code === 0) {
    message.success('Add new user success!')
    // 刷新数据
    fetchData()
  } else {
    message.error('Add new user failed, ' + res.data.message)
  }
}

</script>


