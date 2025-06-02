<template>
  <div id="searchPicturePage">
    <h2 style="margin-bottom: 16px">Picture Search</h2>
    <h3 style="margin: 16px 0">Original Picture</h3>
    <!-- Original Picture -->
    <a-card style="width: 240px">
      <template #cover>
        <img
          style="height: 180px; object-fit: cover"
          :alt="picture.name"
          :src="picture.thumbnailUrl ?? picture.url"
        />
      </template>
    </a-card>
    <h3 style="margin: 16px 0">Search Result</h3>
    <!-- Picture Result List -->
    <a-list
      :grid="{ gutter: 16, xs: 1, sm: 2, md: 3, lg: 4, xl: 5, xxl: 6 }"
      :data-source="dataList"
      :loading="loading"
    >
      <template #renderItem="{ item }">
        <a-list-item style="padding: 0">
          <a :href="item.fromUrl" target="_blank">
            <a-card>
              <template #cover>
                <img style="height: 180px; object-fit: cover" :src="item.thumbUrl" />
              </template>
            </a-card>
          </a>
        </a-list-item>
      </template>
    </a-list>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { useRoute } from 'vue-router'
import { getPictureVoByIdUsingGet, searchPictureByPictureUsingPost } from '@/api/pictureController'
import { message } from 'ant-design-vue'

const route = useRoute()
const loading = ref(false)

const pictureId = computed(() => {
  return route.query?.pictureId
})

const picture = ref<API.PictureVO>({})

const getOriginPicture = async () => {
  const id = route.query?.pictureId
  if (id) {
    const res = await getPictureVoByIdUsingGet({
      id: id,
    })
    if (res.data.code === 0 && res.data.data) {
      const data = res.data.data
      picture.value = data
    }
  }
}

onMounted(() => {
  getOriginPicture()
})

const dataList = ref<API.ImageSearchResult[]>([])
// fetch search result
const fetchResultData = async () => {
  try {
    loading.value = true
    const res = await searchPictureByPictureUsingPost({
      pictureId: pictureId.value,
    })
    if (res.data.code === 0 && res.data.data) {
      dataList.value = res.data.data ?? []
    } else {
      message.error('Fetch Result Data failed, ' + res.data.message)
    }
  } catch (e: any) {
    message.error('Fetch Picture Detail Error: ' + e.message)
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  fetchResultData()
})

</script>

<style scoped>
</style>
