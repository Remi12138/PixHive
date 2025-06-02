<template>
  <div id="globalSider">
    <a-layout-sider
      class="sider"
      v-if="loginUserStore.loginUser.id"
      width="200"
      breakpoint="lg"
      collapsed-width="0"
    >
      <a-menu
        mode="inline"
        v-model:selectedKeys="current"
        :items="menuItems"
        @click="doMenuClick"
      />
    </a-layout-sider>
  </div>

</template>
<script lang="ts" setup>
import { useRouter } from "vue-router";
import { useLoginUserStore } from '@/stores/useLoginUserStore'
import { userLogoutUsingPost } from '@/api/userController'
import { message } from 'ant-design-vue'
import { SPACE_TYPE_ENUM } from '@/constants/space'
import { h, ref, computed, watchEffect } from 'vue'
import { PictureOutlined, UserOutlined, TeamOutlined } from '@ant-design/icons-vue'
import { MenuProps } from 'ant-design-vue'
import { listMyTeamSpaceUsingPost } from '@/api/spaceUserController'
import emitter from '@/utils/eventBus'

emitter.on('teamSpaceCreated', () => {
  fetchTeamSpaceList()
})

const loginUserStore = useLoginUserStore()

// menu
const fixedMenuItems = [
  {
    key: '/',
    label: 'Public Library',
    icon: () => h(PictureOutlined),
  },
  {
    key: '/my_space',
    label: 'My Space',
    icon: () => h(UserOutlined),
  },
  {
    key: '/add_space?type=' + SPACE_TYPE_ENUM.TEAM,
    label: 'Create Team',
    icon: () => h(TeamOutlined),
  },
]

const teamSpaceList = ref<API.SpaceUserVO[]>([])
const menuItems = computed(() => {
  // if no team space, only display fixedMenuItems
  if (teamSpaceList.value.length < 1) {
    return fixedMenuItems;
  }
  // team space list
  const teamSpaceSubMenus = teamSpaceList.value.map((spaceUser) => {
    const space = spaceUser.space
    return {
      key: '/space/' + spaceUser.spaceId,
      label: space?.spaceName,
    }
  })
  const teamSpaceMenuGroup = {
    type: 'group',
    label: 'My Team',
    key: 'teamSpace',
    children: teamSpaceSubMenus,
  }
  return [...fixedMenuItems, teamSpaceMenuGroup]
})


const fetchTeamSpaceList = async () => {
  const res = await listMyTeamSpaceUsingPost()
  if (res.data.code === 0 && res.data.data) {
    teamSpaceList.value = res.data.data
  } else {
    message.error('Load my team space failed, ' + res.data.message)
  }
}


watchEffect(() => {
  // if login, fetchTeamSpaceList
  if (loginUserStore.loginUser.id) {
    fetchTeamSpaceList()
  }
})

const router = useRouter()

// Currently selected menu
const current = ref<string[]>([])
// Listen for route changes and update the current selected menu
router.afterEach((to, from, failure) => {
  current.value = [to.path]
})

// Route jump event
const doMenuClick = ({ key }: { key: string }) => {
  router.push(key)
}
</script>

<style scoped>
#globalSider .ant-layout-sider {
  background: none;
}

</style>

