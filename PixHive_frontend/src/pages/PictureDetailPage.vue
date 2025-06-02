<template>
  <div id="pictureDetailPage">
    <a-row :gutter="[16, 16]">
      <!-- Display picture-->
      <a-col :sm="24" :md="16" :xl="18">
        <a-card title="Preview">
          <a-image
            style="max-height: 600px; object-fit: contain"
            :src="picture.url"
          />
        </a-card>
      </a-col>

      <!-- Information -->
      <a-col :sm="24" :md="8" :xl="6">
        <a-card title="Information">
          <a-descriptions :column="1">
            <a-descriptions-item label="Creator">
              <a-space>
                <a-avatar :size="24" :src="picture.user?.userAvatar" />
                <div>{{ picture.user?.userName }}</div>
              </a-space>
            </a-descriptions-item>
            <a-descriptions-item label="Name">
              {{ picture.name ?? 'Unknown' }}
            </a-descriptions-item>
            <a-descriptions-item label="Introduction">
              {{ picture.introduction ?? '-' }}
            </a-descriptions-item>
            <a-descriptions-item label="Category">
              {{ picture.category ?? 'Default' }}
            </a-descriptions-item>
            <a-descriptions-item label="Tag">
              <a-tag v-for="tag in picture.tags" :key="tag">
                {{ tag }}
              </a-tag>
            </a-descriptions-item>
            <a-descriptions-item label="Format">
              {{ picture.picFormat ?? '-' }}
            </a-descriptions-item>
            <a-descriptions-item label="Width">
              {{ picture.picWidth ?? '-' }}
            </a-descriptions-item>
            <a-descriptions-item label="Height">
              {{ picture.picHeight ?? '-' }}
            </a-descriptions-item>
            <a-descriptions-item label="Scale">
              {{ picture.picScale ?? '-' }}
            </a-descriptions-item>
            <a-descriptions-item label="Size">
              {{ formatSize(picture.picSize) }}
            </a-descriptions-item>
            <a-descriptions-item label="Color Theme">
              <a-space>
                {{ picture.picColor ?? '-' }}
                <div
                  v-if="picture.picColor"
                  :style="{
                    backgroundColor: toHexColor(picture.picColor),
                    width: '16px',
                    height: '16px',
                  }"
                />
              </a-space>
            </a-descriptions-item>
          </a-descriptions>

          <!--  picture download/edit/delete  -->
          <a-space wrap>
            <a-button type="primary" @click="doDownload">
              Download
              <template #icon>
                <DownloadOutlined />
              </template>
            </a-button>
            <a-button type="primary" ghost @click="doShare">
              Share
              <template #icon>
                <share-alt-outlined />
              </template>
            </a-button>
            <a-button :icon="h(EditOutlined)" v-if="canEdit" type="default" @click="doEdit">
              Edit
              <template #icon>
                <EditOutlined />
              </template>
            </a-button>
            <a-button :icon="h(DeleteOutlined)" v-if="canDelete" danger @click="doDelete">
              Delete
              <template #icon>
                <DeleteOutlined />
              </template>
            </a-button>
          </a-space>
        </a-card>
      </a-col>
    </a-row>
    <ShareModal ref="shareModalRef" :link="shareLink" />
  </div>
</template>

<script setup lang="ts">
import { reactive, ref, onMounted, computed, h } from "vue"
import { message } from 'ant-design-vue'
import { downloadImage, formatSize, toHexColor } from '@/utils'
import { EditOutlined, DeleteOutlined, DownloadOutlined, ShareAltOutlined } from "@ant-design/icons-vue";
import { useLoginUserStore } from '@/stores/useLoginUserStore'
import { useRouter } from "vue-router";
import { deletePictureUsingPost, getPictureVoByIdUsingGet } from '@/api/pictureController'
import ShareModal from '@/components/ShareModal.vue'
import { SPACE_PERMISSION_ENUM } from '@/constants/space'

interface Props {
  id: string | number
}
const props = defineProps<Props>()
const picture = ref<API.PictureVO>({})
const loginUserStore = useLoginUserStore()

// general Permission Checker
function createPermissionChecker(permission: string) {
  return computed(() => {
    return (picture.value.permissionList ?? []).includes(permission)
  })
}

// specific Permission Checker
const canEdit = createPermissionChecker(SPACE_PERMISSION_ENUM.PICTURE_EDIT)
const canDelete = createPermissionChecker(SPACE_PERMISSION_ENUM.PICTURE_DELETE)


// check edit authority
// user state may change (login/out), need to update 'canEdit'-> computed
// const canEdit = computed(() => {
//   const loginUser = loginUserStore.loginUser;
//   if (!loginUser.id) {
//     return false
//   }
//   // self/admin
//   const user = picture.value.user || {}
//   return loginUser.id === user.id || loginUser.userRole === 'admin'
// })

const router = useRouter()

const doEdit = () => {
  router.push({
    path: '/add_picture',
    query: {
      id: picture.value.id,
      spaceId: picture.value.spaceId
    }
  })
}

const doDelete = async () => {
  const id = picture.value.id
  if (!id) {
    return
  }
  const res = await deletePictureUsingPost({ id })
  if (res.data.code === 0) {
    message.success('Delete success!')
  } else {
    message.error('Delete Error: ' + res.data.message)
  }
  router.push('/')
}

const doDownload = () => {
  downloadImage(picture.value.url)
}

// fetch Picture Detail
const fetchPictureDetail = async () => {
  try {
    const res = await getPictureVoByIdUsingGet({
      id: props.id,
    })
    if (res.data.code === 0 && res.data.data) {
      picture.value = res.data.data
    } else {
      message.error('Fetch Picture Detail failed: ' + res.data.message)
    }
  } catch (e: any) {
    message.error('Fetch Picture Detail failed: ' + e.message)
  }
}

onMounted(() => {
  fetchPictureDetail()
})

const shareModalRef = ref()
const shareLink = ref<string>()

const doShare = () => {
  shareLink.value = `${window.location.protocol}//${window.location.host}/picture/${picture.value.id}`
  if (shareModalRef.value) {
    shareModalRef.value.openModal()
  }
}
</script>

<style scoped>
#pictureDetailPage {
  margin-bottom: 16px;
}
</style>
