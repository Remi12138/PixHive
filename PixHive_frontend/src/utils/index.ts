/**
 * format size
 * @param size
 */
export const formatSize = (size?: number) => {
  if (!size) return 'unknown'
  if (size < 1024) return size + ' B'
  if (size < 1024 * 1024) return (size / 1024).toFixed(2) + ' KB'
  if (size < 1024 * 1024 * 1024) return (size / (1024 * 1024)).toFixed(2) + ' MB'
  return (size / (1024 * 1024 * 1024)).toFixed(2) + ' GB'
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

/**
 * picColor -> standard #RRGGBB
 */
export function toHexColor(input: string): string {
  // remove "0x" prefix
  const colorValue = input.startsWith('0x') ? input.slice(2) : input

  // Remaining -> Hex -> 6-digit Hex String
  const hexColor = parseInt(colorValue, 16).toString(16).padStart(6, '0')

  // Return the standard #RRGGBB format
  return `#${hexColor}`
}

