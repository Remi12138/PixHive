/**
 * picture review status
 */
export const PIC_REVIEW_STATUS_ENUM = {
  REVIEWING: 0,
  PASS: 1,
  REJECT: 2,
}

/**
 * picture review status dispaly label
 */
export const PIC_REVIEW_STATUS_MAP = {
  0: 'reviewing',
  1: 'pass',
  2: 'reject',
}

/**
 * Drop-down form options
 */
export const PIC_REVIEW_STATUS_OPTIONS = Object.keys(PIC_REVIEW_STATUS_MAP).map((key) => {
  return {
    label: PIC_REVIEW_STATUS_MAP[key],
    value: key,
  }
})
