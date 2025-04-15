export const SPACE_LEVEL_ENUM = {
  STARTER: 0,
  PRO: 1,
  PREMIUM: 2,
} as const;


export const SPACE_LEVEL_MAP: Record<number, string> = {
  0: 'Starter',
  1: 'Pro',
  2: 'Premium',
};


export const SPACE_LEVEL_OPTIONS = Object.keys(SPACE_LEVEL_MAP).map((key) => {
  const value = Number(key); // Convert string key to number
  return {
    label: SPACE_LEVEL_MAP[value],
    value,
  };
});
