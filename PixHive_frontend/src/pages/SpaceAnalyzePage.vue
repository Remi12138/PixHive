<template>
  <div id="spaceAnalyzePage">
    <h2>
      PixHive Space Analysis -
      <span v-if="queryAll"> All the Space </span>
      <span v-else-if="queryPublic"> Public Space </span>
      <span v-else>
      <a :href="`/space/${spaceId}`" target="_blank">Space id：{{ spaceId }}</a>
    </span>
    </h2>
    <div style="margin-bottom: 16px" />
    <a-row :gutter="[16, 16]">
      <!-- Usage -->
      <a-col :xs="24" :md="12">
        <SpaceUsageAnalyze :spaceId="spaceId" :queryAll="queryAll" :queryPublic="queryPublic" />
      </a-col>
      <!-- Category -->
      <a-col :xs="24" :md="12">
        <SpaceCategoryAnalyze :spaceId="spaceId" :queryAll="queryAll" :queryPublic="queryPublic" />
      </a-col>
      <!-- Tag -->
      <a-col :xs="24" :md="12">
        <SpaceTagAnalyze :spaceId="spaceId" :queryAll="queryAll" :queryPublic="queryPublic" />
      </a-col>
      <!-- Size segmentation -->
      <a-col :xs="24" :md="12">
        <SpaceSizeAnalyze :spaceId="spaceId" :queryAll="queryAll" :queryPublic="queryPublic" />
      </a-col>
      <!-- User Behaviour -->
      <a-col :xs="24" :md="12">
        <SpaceUserAnalyze :spaceId="spaceId" :queryAll="queryAll" :queryPublic="queryPublic" />
      </a-col>
      <!-- Rank -->
      <a-col :xs="24" :md="12">
        <SpaceRankAnalyze v-if="isAdmin"
                          :spaceId="spaceId"
                          :queryAll="queryAll"
                          :queryPublic="queryPublic"
        />
      </a-col>
    </a-row>
  </div>
</template>

<script setup lang="ts">
import { reactive, ref, onMounted, computed, h } from "vue"
import { message } from 'ant-design-vue'
import { downloadImage, formatSize } from '@/utils'
import { EditOutlined, DeleteOutlined, DownloadOutlined } from "@ant-design/icons-vue";
import { useLoginUserStore } from '@/stores/useLoginUserStore'
import { useRouter, useRoute } from "vue-router";
import { getSpaceVoByIdUsingGet, listSpaceVoByPageUsingPost } from '@/api/spaceController'
import PictureList from '@/components/PictureList.vue'
import { listPictureVoByPageUsingPost, searchPictureByColorUsingPost } from '@/api/pictureController'
import PictureSearchForm from '@/components/PictureSearchForm.vue'
import { ColorPicker } from "vue3-colorpicker"
import "vue3-colorpicker/style.css"
import BatchEditPictureModal from '@/components/BatchEditPictureModal.vue'
import SpaceUsageAnalyze from '@/components/analyze/SpaceUsageAnalyze.vue'
import SpaceCategoryAnalyze from '@/components/analyze/SpaceCategoryAnalyze.vue'
import SpaceTagAnalyze from '@/components/analyze/SpaceTagAnalyze.vue'
import SpaceSizeAnalyze from '@/components/analyze/SpaceSizeAnalyze.vue'
import SpaceUserAnalyze from '@/components/analyze/SpaceUserAnalyze.vue'
import SpaceRankAnalyze from '@/components/analyze/SpaceRankAnalyze.vue'

const route = useRoute()

const spaceId = computed(() => {
  return route.query?.spaceId as string
})

// !! int->boolean
const queryAll = computed(() => {
  return !!route.query?.queryAll
})

const queryPublic = computed(() => {
  return !!route.query?.queryPublic
})

const loginUserStore = useLoginUserStore()
const loginUser = loginUserStore.loginUser

const isAdmin = computed(() => {
  return loginUser.userRole === 'admin'
})

</script>

<style scoped>
#spaceAnalyzePage {
  margin-bottom: 16px;
}
</style>
