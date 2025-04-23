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


export const SPACE_TYPE_ENUM = {
  PRIVATE: 0,
  TEAM: 1,
}

export const SPACE_TYPE_MAP: Record<number, string> = {
  0: 'Private Space',
  1: 'Team Space',
}

export const SPACE_TYPE_OPTIONS = Object.keys(SPACE_TYPE_MAP).map((key) => {
  const value = Number(key) // 将字符串 key 转换为数字
  return {
    label: SPACE_TYPE_MAP[value],
    value,
  }
})

export const SPACE_ROLE_ENUM = {
  VIEWER: "viewer",
  EDITOR: "editor",
  ADMIN: "admin",
} as const;

export const SPACE_ROLE_MAP: Record<string, string> = {
  viewer: "Viewer",
  editor: "Editor",
  admin: "Admin",
};

export const SPACE_ROLE_OPTIONS = Object.keys(SPACE_ROLE_MAP).map((key) => {
  return {
    label: SPACE_ROLE_MAP[key],
    value: key,
  };
});


export const SPACE_PERMISSION_ENUM = {
  SPACE_USER_MANAGE: "spaceUser:manage",
  PICTURE_VIEW: "picture:view",
  PICTURE_UPLOAD: "picture:upload",
  PICTURE_EDIT: "picture:edit",
  PICTURE_DELETE: "picture:delete",
} as const;

