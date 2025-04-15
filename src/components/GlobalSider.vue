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
import { h, ref, computed } from 'vue'
import { PictureOutlined, UserOutlined } from '@ant-design/icons-vue'
import { MenuProps } from 'ant-design-vue'

const loginUserStore = useLoginUserStore()

// menu
const menuItems = [
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
]

const router = useRouter()

// Currently selected menu
const current = ref<string[]>([])
// Listen for route changes and update the current selected menu
router.afterEach((to, from, failure) => {
  current.value = [to.path]
})

// Route jump event
const doMenuClick = ({ key }: { key: string }) => {
  router.push({
    path: key,
  })
}






import { useRouter } from "vue-router";
import { useLoginUserStore } from '@/stores/useLoginUserStore'
import { userLogoutUsingPost } from '@/api/userController'
import { message } from 'ant-design-vue'






</script>

<style scoped>
#globalSider .ant-layout-sider {
  background: none;
}

</style>

