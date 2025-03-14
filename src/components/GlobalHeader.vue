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
      <!-- login -->
      <a-col flex="120px">
        <div class="user-login-status">
          <div v-if="loginUserStore.loginUser.id">
            {{ loginUserStore.loginUser.userName ?? 'DefaultName' }}
          </div>
          <div v-else>
            <a-button type="primary" href="/user/login">Login</a-button>
          </div>
        </div>

      </a-col>
    </a-row>
  </div>
</template>
<script lang="ts" setup>
import { h, ref } from 'vue'
import { HomeOutlined } from '@ant-design/icons-vue'
import { MenuProps } from 'ant-design-vue'

const loginUserStore = useLoginUserStore()

const items = ref<MenuProps['items']>([
  {
    key: '/',
    icon: () => h(HomeOutlined),
    label: 'Home',
    title: 'Home',
  },
  {
    key: '/about',
    label: 'About',
    title: 'About',
  },
  {
    key: 'others',
    label: h('a', { href: 'https://linkedin.com/in/xianjing-jin-huang', target: '_blank' }, 'LinkedIn'),
    title: 'LinkedIn',
  },
])
import { useRouter } from "vue-router";
import { useLoginUserStore } from '@/stores/useLoginUserStore'
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

