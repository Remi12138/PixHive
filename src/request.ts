import axios from 'axios'
import { message } from 'ant-design-vue'

// Create Axios instance
const myAxios = axios.create({
  baseURL: 'http://localhost:8123',
  timeout: 60000, // ms
  withCredentials: true,
})

// Add a request interceptor
myAxios.interceptors.request.use(
  function (config) {
    // Do something before request is sent
    return config
  },
  function (error) {
    // Do something with request error
    return Promise.reject(error)
  },
)

// Add a response interceptor
myAxios.interceptors.response.use(
  function (response) {
    const { data } = response
    // not login
    if (data.code === 40100) {
      // If it is not a request to obtain user information and
      // the user is not already on the user login page, go to the login page
      if (
        !response.request.responseURL.includes('user/get/login') &&
        !window.location.pathname.includes('/user/login')
      ) {
        message.warning('Please login first!')
        window.location.href = `/user/login?redirect=${window.location.href}`
      }
    }
    return response
  },
  function (error) {
    // Any status codes that falls outside the range of 2xx cause this function to trigger
    // Do something with response error
    return Promise.reject(error)
  },
)

export default myAxios
