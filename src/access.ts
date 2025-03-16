import { useLoginUserStore } from '@/stores/useLoginUserStore'
import { message } from 'ant-design-vue'
import router from '@/router'

// avoid fetch every time when change webpage
// fetch Login User only once
let firstFetchLoginUser = true;

/**
 * Global authority check, every time when change webpage
 */
router.beforeEach(async (to, from, next) => {
  const loginUserStore = useLoginUserStore()
  let loginUser = loginUserStore.loginUser

  // first fetch, wait backend return user info, then check authority
  if (firstFetchLoginUser) {
    await loginUserStore.fetchLoginUser()
    loginUser = loginUserStore.loginUser
    firstFetchLoginUser = false;
  }
  const toUrl = to.fullPath
  //-----custom---------
  if (toUrl.startsWith('/admin')) {
    if (!loginUser || loginUser.userRole !== 'admin') {
      message.error('No authority!')
      next(`/user/login?redirect=${to.fullPath}`)
      return
    }
  }
  next()
})
