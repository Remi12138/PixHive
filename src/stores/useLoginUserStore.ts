import { defineStore } from "pinia";
import { ref } from "vue";

/**
 * Store the status of login user information
 */
export const useLoginUserStore = defineStore("loginUser", () => {
  const loginUser = ref<any>({
    userName: "notLogin",
  });

  async function fetchLoginUser() {
    // todo
    // const res = await getCurrentUser();
    // if (res.data.code === 0 && res.data.data) {
    //   loginUser.value = res.data.data;
    // }

    // test login, 3s
    setTimeout(() => {
      loginUser.value = { userName: 'TestUser', id: 1 }
    }, 3000)
  }

  /**
   * Set login user
   * @param newLoginUser
   */
  function setLoginUser(newLoginUser: any) {
    loginUser.value = newLoginUser;
  }

  return { loginUser, setLoginUser, fetchLoginUser };
});
