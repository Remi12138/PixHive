import { createRouter, createWebHistory } from 'vue-router'
import HomeView from '../views/HomeView.vue'
import HomePage from '@/pages/HomePage.vue'
import UserLoginPage from '@/pages/user/UserLoginPage.vue'
import UserRegisterPage from '@/pages/user/UserRegisterPage.vue'
import UserManagePage from '@/pages/admin/UserManagePage.vue'
import AddPicturePage from '@/pages/AddPicturePage.vue'
import PictureManagePage from '@/pages/admin/PictureManagePage.vue'
import PictureDetailPage from '@/pages/PictureDetailPage.vue'
import AddPictureBatchPage from '@/pages/AddPictureBatchPage.vue'
import SpaceManagePage from '@/pages/admin/SpaceManagePage.vue'
import AddSpacePage from '@/pages/AddSpacePage.vue'
import MySpacePage from '@/pages/MySpacePage.vue'
import SpaceDetailPage from '@/pages/SpaceDetailPage.vue'
import SearchPicturePage from '@/pages/SearchPicturePage.vue'
import SpaceAnalyzePage from '@/pages/SpaceAnalyzePage.vue'
import SpaceUserManagePage from '@/pages/admin/SpaceUserManagePage.vue'
import UserRedeemVipPage from '@/pages/UserRedeemVipPage.vue'
import UserProfilePage from '@/pages/user/UserProfilePage.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'home',
      component: HomePage,
    },
    {
      path: '/user/login',
      name: 'user login',
      component: UserLoginPage,
    },
    {
      path: '/user/register',
      name: 'user register',
      component: UserRegisterPage,
    },
    {
      path: '/admin/userManage',
      name: 'user manage',
      component: UserManagePage,
    },
    {
      path: '/add_picture',
      name: 'add picture',
      component: AddPicturePage,
    },
    {
      path: '/add_picture/batch',
      name: 'add batch picture',
      component: AddPictureBatchPage,
    },
    {
      path: '/admin/pictureManage',
      name: 'picture manage',
      component: PictureManagePage,
    },
    {
      path: '/picture/:id',
      name: 'picture detail',
      component: PictureDetailPage,
      props: true,
    },
    {
      path: '/admin/spaceManage',
      name: 'space manage',
      component: SpaceManagePage,
    },
    {
      path: '/SpaceUserManage/:id',
      name: 'space user manage',
      component: SpaceUserManagePage,
      props: true,
    },
    {
      path: '/add_space',
      name: 'add space',
      component: AddSpacePage,
    },
    {
      path: '/my_space',
      name: 'my space',
      component: MySpacePage,
    },
    {
      path: '/space/:id',
      name: 'space detail',
      component: SpaceDetailPage,
      props: true,
    },
    {
      path: '/space_analyze',
      name: 'space analyze',
      component: SpaceAnalyzePage,
      props: true,
    },
    {
      path: '/search_picture',
      name: 'search picture',
      component: SearchPicturePage,
    },
    {
      path: '/user_redeem_vip',
      name: 'vip redeem',
      component: UserRedeemVipPage,
    },
    {
      path: '/user/profile',
      name: 'UserProfile',
      component: UserProfilePage,
    }

  ],
})

export default router
