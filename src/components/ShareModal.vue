<template>
  <a-modal v-model:visible="visible" title="title" :footer="false" @cancel="closeModal">
    <h4>Copy the link</h4>
    <a-typography-link copyable>
      {{ link }}
    </a-typography-link>
    <div style="margin-bottom: 16px" />
    <h4>Scan to view</h4>
    <a-qrcode :value="link" />
  </a-modal>
</template>

<script setup lang="ts">
import { ref, defineExpose } from 'vue'

interface Props {
  title: string
  link: string
}

const props = withDefaults(defineProps<Props>(), {
  title: () => 'Share Picture',
  link: () => 'https://laoyujianli.com/share/yupi',
})

const visible = ref(false)

const openModal = () => {
  visible.value = true
}

const closeModal = () => {
  visible.value = false
}

// expose function to parent
defineExpose({
  openModal,
});

</script>

<style scoped>
</style>

