<template>
  <div class="picture-list">
    <!-- picture list -->
    <a-list
      :grid="{ gutter: 16, xs: 1, sm: 2, md: 3, lg: 4, xl: 5, xxl: 6 }"
      :data-source="dataList"
      :loading="loading"
    >
      <template #renderItem="{ item: picture }">
        <a-list-item style="padding: 0">
          <!-- single picture -->
          <a-card hoverable @click="doClickPicture(picture)">
            <!-- object-fit: cover -> adjust,same width & height -->
            <template #cover>
              <img
                style="height: 180px; object-fit: cover"
                :alt="picture.name"
                :src="picture.thumbnailUrl ?? picture.url"
                loading="lazy"
              />
            </template>
            <a-card-meta :title="picture.name">
              <template #description>
                <a-flex>
                  <a-tag color="green">
                    {{ picture.category ?? 'Default' }}
                  </a-tag>
                  <a-tag v-for="tag in picture.tags" :key="tag">
                    {{ tag }}
                  </a-tag>
                </a-flex>
              </template>
            </a-card-meta>
            <template v-if="showOp" #actions>
              <a-space @click="e => doEdit(picture, e)">
                <EditOutlined />
                Edit
              </a-space>
              <a-space @click="e => doDelete(picture, e)">
                <DeleteOutlined />
                Delete
              </a-space>
            </template>
          </a-card>
        </a-list-item>
      </template>
    </a-list>
  </div>
</template>

<script setup lang="ts">
import { useRouter } from 'vue-router'
import { DeleteOutlined, EditOutlined } from '@ant-design/icons-vue'
import { deletePictureUsingPost } from '@/api/pictureController'
import { message } from 'ant-design-vue'

interface Props {
  dataList?: API.PictureVO[]
  loading?: boolean
  showOp?: boolean
  onReload?: () => void
}

const props = withDefaults(defineProps<Props>(), {
  dataList: () => [],
  loading: false,
  showOp: false,
})

// Jump to picture details
const router = useRouter()
const doClickPicture = (picture) => {
  router.push({
    path: `/picture/${picture.id}`,
  })
}

const doEdit = (picture, e) => {
  e.stopPropagation()
  router.push({
    path: '/add_picture',
    query: {
      id: picture.id,
      spaceId: picture.spaceId,
    },
  })
}

const doDelete = async (picture, e) => {
  e.stopPropagation()
  const id = picture.id
  if (!id) {
    return
  }
  const res = await deletePictureUsingPost({ id })
  if (res.data.code === 0) {
    message.success('Delete success!')
    // fresh
    props?.onReload?.()
  } else {
    message.error('Delete Error: ' + res.data.message)
  }
}

</script>

<style scoped></style>
