/**
 * format size
 * @param size
 */
export const formatSize = (size?: number) => {
  if (!size) return 'unknown'
  if (size < 1024) return size + ' B'
  if (size < 1024 * 1024) return (size / 1024).toFixed(2) + ' KB'
  return (size / (1024 * 1024)).toFixed(2) + ' MB'
}

import { saveAs } from 'file-saver';

/**
 * download Image
 * @param url
 * @param fileName after download, store name
 */
export function downloadImage(url?: string, fileName?: string) {
  if (!url) {
    return
  }
  saveAs(url, fileName)
}

