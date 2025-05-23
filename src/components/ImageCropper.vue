<template>
  <a-modal class="image-cropper" v-model:visible="visible" title="Edit Picture" :footer="false" @cancel="closeModal">
    <!-- ratio selector -->
    <!-- take effect when drag box -->
    <div class="aspect-ratio-selector">
      <a-radio-group v-model:value="aspectRatio" button-style="solid">
        <a-radio-button value="free">Free</a-radio-button>
        <a-radio-button value="1:1">1:1</a-radio-button>
        <a-radio-button value="4:3">4:3</a-radio-button>
        <a-radio-button value="16:9">16:9</a-radio-button>
        <a-radio-button value="3:4">3:4</a-radio-button>
        <a-radio-button value="9:16">9:16</a-radio-button>
      </a-radio-group>
    </div>
    <div style="margin-bottom: 16px" />
    <!-- crop component -->
    <vue-cropper
      ref="cropperRef"
      :img="imageUrl"
      :autoCrop="true"
      :fixedBox="false"
      :centerBox="true"
      :canMoveBox="true"
      :canMove="false"
      :info="true"
      outputType="png"
      :fixed="aspectRatio !== 'free'"
      :fixedNumber="currentAspectRatio"
    />
    <div style="margin-bottom: 16px" />
    <!-- Collaborative editing operation -->
    <div class="image-edit-actions" v-if="isTeamSpace">
      <a-space>
        <a-button v-if="editingUser" disabled> {{ editingUser.userName }}is editing...</a-button>
        <a-button v-if="canEnterEdit" type="primary" ghost @click="enterEdit">Enter Edit</a-button>
        <a-button v-if="canExitEdit" danger ghost @click="exitEdit">Exit Edit</a-button>
      </a-space>
    </div>
    <div style="margin-bottom: 16px" />
    <!-- image action -->
    <div class="image-cropper-actions">
      <a-space>
        <a-button @click="rotateLeft" :disabled="!canEdit">Rotate Left</a-button>
        <a-button @click="rotateRight" :disabled="!canEdit">Rotate Right</a-button>
        <a-button @click="changeScale(1)" :disabled="!canEdit">Zoom in</a-button>
        <a-button @click="changeScale(-1)" :disabled="!canEdit">Zoom out</a-button>
        <a-button type="primary" :loading="loading" :disabled="!canEdit" @click="handleConfirm">OK</a-button>
      </a-space>
    </div>
  </a-modal>
</template>

<script setup lang="ts">
import { ref, computed, watchEffect, onUnmounted } from 'vue'
import { uploadPictureUsingPost } from '@/api/pictureController'
import { message } from 'ant-design-vue'
import { useLoginUserStore } from '@/stores/useLoginUserStore'
import PictureEditWebSocket from '@/utils/pictureEditWebSocket'
import { PICTURE_EDIT_ACTION_ENUM, PICTURE_EDIT_MESSAGE_TYPE_ENUM } from '@/constants/picture'
import { SPACE_TYPE_ENUM } from '@/constants/space'

interface Props {
  imageUrl?: string
  picture?: API.PictureVO
  spaceId?: number
  space?: API.SpaceVO
  onSuccess?: (newPicture: API.PictureVO) => void
}

const props = defineProps<Props>()

const isTeamSpace = computed(() => {
  return props.space?.spaceType === SPACE_TYPE_ENUM.TEAM;
})

const cropperRef = ref()
const visible = ref(false)

const openModal = () => {
  visible.value = true
}

// expose function to parent
defineExpose({
  openModal,
});

const rotateLeft = () => {
  cropperRef.value.rotateLeft()
  editAction(PICTURE_EDIT_ACTION_ENUM.ROTATE_LEFT)
}

const rotateRight = () => {
  cropperRef.value.rotateRight()
  editAction(PICTURE_EDIT_ACTION_ENUM.ROTATE_RIGHT)
}

const changeScale = (num: number) => {
  cropperRef.value.changeScale(num)
  if (num > 0) {
    editAction(PICTURE_EDIT_ACTION_ENUM.ZOOM_IN)
  } else {
    editAction(PICTURE_EDIT_ACTION_ENUM.ZOOM_OUT)
  }
}

const loading = ref<boolean>(false)

const handleConfirm = () => {
  cropperRef.value.getCropBlob((blob: Blob) => { // blob is the cropped file
    const fileName = (props.picture?.name || 'image') + '.png'
    const file = new File([blob], fileName, { type: blob.type })
    handleUpload({ file })
  })
}

const handleUpload = async ({ file }: any) => {
  loading.value = true
  try {
    const params: API.PictureUploadRequest = props.picture ? { id: props.picture.id } : {}
    params.spaceId = props.spaceId
    const res = await uploadPictureUsingPost(params, {}, file)
    if (res.data.code === 0 && res.data.data) {
      message.success('Upload success!')
      // pass uploaded pic info to parent
      props.onSuccess?.(res.data.data)
      closeModal();
    } else {
      message.error('Upload failed, ' + res.data.message)
    }
  } catch (error) {
    message.error('Upload failed')
  } finally {
    loading.value = false
  }
}

const aspectRatio = ref('free')

// compute current ratio: width/height
const currentAspectRatio = computed(() => {
  if (aspectRatio.value === 'free') return [0, 0]
  const [width, height] = aspectRatio.value.split(':').map(Number)
  return [width, height]
})

// ----- Real-Time Edit---------
const loginUserStore = useLoginUserStore()
let loginUser = loginUserStore.loginUser

// is editing now
const editingUser = ref<API.UserVO>()

// if no one is editing, canEnterEdit
const canEnterEdit = computed(() => {
  return !editingUser.value
})

// self is editing, can exit
const canExitEdit = computed(() => {
  return editingUser.value?.id === loginUser.id
})

const canEdit = computed(() => {
  // not team space, can edit
  if (!isTeamSpace.value) {
    return true
  }
  // team space
  // editingUser can perform an edit action
  return editingUser.value?.id === loginUser.id
})

// ---- websocket logic -----
let websocket: PictureEditWebSocket | null

// init WebSocket connect, bind event
const initWebsocket = () => {
  const pictureId = props.picture?.id
  if (!pictureId || !visible.value) {
    return
  }
  // if previous connection not be released
  if (websocket) {
    websocket.disconnect()
  }
  // Create a WebSocket instance
  websocket = new PictureEditWebSocket(pictureId)
  // Create a WebSocket connect
  websocket.connect()

  // listen INFO
  websocket.on(PICTURE_EDIT_MESSAGE_TYPE_ENUM.INFO, (msg) => {
    console.log('Receive INFO: ', msg)
    message.info(msg.message)
  })

  // listen ERROR
  websocket.on(PICTURE_EDIT_MESSAGE_TYPE_ENUM.ERROR, (msg) => {
    console.log('Receive ERROR: ', msg)
    message.error(msg.message)
  })

  // listen ENTER_EDIT
  websocket.on(PICTURE_EDIT_MESSAGE_TYPE_ENUM.ENTER_EDIT, (msg) => {
    console.log('Receive ENTER_EDIT: ', msg)
    message.info(msg.message)
    editingUser.value = msg.user
  })

  // listen EDIT_ACTION
  websocket.on(PICTURE_EDIT_MESSAGE_TYPE_ENUM.EDIT_ACTION, (msg) => {
    console.log('Receive EDIT_ACTION: ', msg)
    message.info(msg.message)
    switch (msg.editAction) {
      case PICTURE_EDIT_ACTION_ENUM.ROTATE_LEFT:
        cropperRef.value.rotateLeft()
        break
      case PICTURE_EDIT_ACTION_ENUM.ROTATE_RIGHT:
        cropperRef.value.rotateRight()
        break
      case PICTURE_EDIT_ACTION_ENUM.ZOOM_IN:
        cropperRef.value.changeScale(1)
        break
      case PICTURE_EDIT_ACTION_ENUM.ZOOM_OUT:
        cropperRef.value.changeScale(-1)
        break
    }
  })

  // listen EXIT_EDIT
  websocket.on(PICTURE_EDIT_MESSAGE_TYPE_ENUM.EXIT_EDIT, (msg) => {
    console.log('Receive EXIT_EDIT: ', msg)
    message.info(msg.message)
    editingUser.value = undefined
  })
}

watchEffect(() => {
  // only team space will init
  if (isTeamSpace.value) {
    initWebsocket()
  }
})

onUnmounted(() => {
  if (websocket) {
    websocket.disconnect()
  }
  editingUser.value = undefined
})

const closeModal = () => {
  visible.value = false
  if (websocket) {
    websocket.disconnect()
  }
  editingUser.value = undefined
}

const enterEdit = () => {
  if (websocket) {
    websocket.sendMessage({
      type: PICTURE_EDIT_MESSAGE_TYPE_ENUM.ENTER_EDIT,
    })
  }
}

const exitEdit = () => {
  if (websocket) {
    websocket.sendMessage({
      type: PICTURE_EDIT_MESSAGE_TYPE_ENUM.EXIT_EDIT,
    })
  }
}

const editAction = (action: string) => {
  if (websocket) {
    websocket.sendMessage({
      type: PICTURE_EDIT_MESSAGE_TYPE_ENUM.EDIT_ACTION,
      editAction: action,
    })
  }
}


</script>

<style>
.image-cropper {
  text-align: center;
}

.image-cropper .vue-cropper {
  height: 400px !important;
}
</style>
