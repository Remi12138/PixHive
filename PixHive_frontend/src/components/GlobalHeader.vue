<template>
  <div class="GlobalHeader">
    <a-row :wrap="false">
      <!-- logo and title -->
      <a-col flex="200px">
        <RouterLink to="/">
          <div class="title-bar">
            <img class="logo" src="../assets/logo.png" alt="logo" />
            <div class="title">PixHive</div>
          </div>
        </RouterLink>
      </a-col>
      <!-- menu -->
      <a-col flex="auto">
        <a-menu
          v-model:selectedKeys="current"
          mode="horizontal"
          :items="items"
          @click="doMenuClick"
        />
      </a-col>
      <!-- User Info  -->
      <a-col flex="120px">
        <div v-if="loginUserStore.loginUser.id">
          <a-dropdown>
            <div style="display: flex; align-items: center; gap: 8px;">
              <!-- VIP icon and label inline -->
              <div v-if="isVip" style="display: flex; align-items: center; gap: 2px;">
                <CrownOutlined style="color: #efcb03; font-size: 16px;" />
                <span style="color: #efcb03; font-size: 12px; font-weight: 500;">VIP</span>
              </div>

              <!-- Avatar and name -->
              <ASpace>
                <a-avatar :src="loginUserStore.loginUser.userAvatar" />
                <span>{{ loginUserStore.loginUser.userName ?? 'DefaultUser' }}</span>
              </ASpace>
            </div>
<!--            <ASpace>-->
<!--              <div v-if="loginUserStore.loginUser.vipCode" style="text-align: center">-->
<!--                <CrownOutlined style="color: #efcb03; font-size: 16px; font-weight: 500;" />-->
<!--                <div style="font-size: 10px; color: #efcb03; line-height: 1;">VIP</div>-->
<!--              </div>-->
<!--              <a-avatar :src="loginUserStore.loginUser.userAvatar" />-->
<!--              <span>{{ loginUserStore.loginUser.userName ?? 'DefaultUser' }}</span>>-->
<!--            </ASpace>-->
            <template #overlay>
              <a-menu>
                <a-menu-item>
                  <router-link to="/user/profile">
                    <UserOutlined />
                    Profile
                  </router-link>
                </a-menu-item>
                <a-menu-item>
                  <router-link to="/my_space">
                    <FileOutlined />
                    Space
                  </router-link>
                </a-menu-item>
                <a-menu-item @click="doLogout">
                  <LogoutOutlined />
                  Logout
                </a-menu-item>

              </a-menu>
            </template>
          </a-dropdown>
        </div>
        <div v-else>
          <a-button type="primary" href="/user/login">Login</a-button>
        </div>
      </a-col>
    </a-row>
  </div>
</template>
<script lang="ts" setup>
import { h, ref, computed } from 'vue'
import { HomeOutlined, LogoutOutlined, UserOutlined, CrownOutlined, FileOutlined } from '@ant-design/icons-vue'
import { MenuProps } from 'ant-design-vue'

const loginUserStore = useLoginUserStore()

// menu
const originItems = [
  {
    key: '/',
    icon: () => h(HomeOutlined),
    label: 'Home',
    title: 'Home',
  },
  {
    key: '/add_picture',
    label: 'Add Picture',
    title: 'Add Picture',
  },
  {
    key: '/user_redeem_vip',
    label: 'VIP Redeem',
    title: 'VIP Redeem',
  },
  {
    key: '/admin/userManage',
    label: 'User Manage',
    title: 'User Manage',
  },
  {
    key: '/admin/pictureManage',
    label: 'Picture Manage',
    title: 'Picture Manage',
  },
  {
    key: '/admin/spaceManage',
    label: 'Space Manage',
    title: 'Space Manage',
  },
  {
    key: 'others',
    label: h('a', { href: 'https://linkedin.com/in/xianjing-jin-huang', target: '_blank' }, 'About'),
    title: 'About',
  },
]

// filter Menus by role
const filterMenus = (menus = [] as MenuProps['items']) => {
  return menus?.filter((menu) => {
    if (menu?.key?.startsWith('/admin')) {
      const loginUser = loginUserStore.loginUser
      if (!loginUser || loginUser.userRole !== "admin") {
        return false
      }
    }
    return true
  })
}

// Display the routing array in the menu
const items = computed<MenuProps['items']>(() => filterMenus(originItems))

import { useRouter } from "vue-router";
import { useLoginUserStore } from '@/stores/useLoginUserStore'
import { userLogoutUsingPost } from '@/api/userController'
import { message } from 'ant-design-vue'
const router = useRouter();

// Route jump event
const doMenuClick = ({ key }: { key: string }) => {
  router.push({
    path: key,
  });
};

// Currently selected menu
const current = ref<string[]>(['home'])
// Listen for route changes and update the current selected menu
router.afterEach((to, from, next) => {
  current.value = [to.path];
});

// Logout
const doLogout = async () => {
  const res = await userLogoutUsingPost()
  console.log(res)
  if (res.data.code === 0) {
    loginUserStore.setLoginUser({
      userName: 'notLogin',
    })
    message.success('Logout success!')
    router.push('/user/login')
  } else {
    message.error('Logout error: ' + res.data.message)
  }
}

const isVip = computed(() => {
  const user = loginUserStore.loginUser
  if (!user.vipCode || !user.vipExpireTime) return false
  return new Date() < new Date(user.vipExpireTime)
})

</script>

<style scoped>
.title-bar {
  display: flex;
  align-items: center;
}

.title {
  color: black;
  font-size: 18px;
  margin-left: 16px;
}

.logo {
  height: 48px;
}
</style>

