package com.jin.pixhive_backend.model.enums;

import cn.hutool.core.util.ObjUtil;
import lombok.Getter;

@Getter
public enum SpaceLevelEnum {

    STARTER("Starter", 0, 100, 100L * 1024 * 1024), // 100M
    PRO("Pro", 1, 1000, 1000L * 1024 * 1024), // 1G
    PREMIUM("Premium", 2, 10000, 10000L * 1024 * 1024); // 10G

    private final String text;

    private final int value;

    private final long maxCount;

    private final long maxSize;

    SpaceLevelEnum(String text, int value, long maxCount, long maxSize) {
        this.text = text;
        this.value = value;
        this.maxCount = maxCount;
        this.maxSize = maxSize;
    }

    /**
     * get Enum by Value
     */
    public static SpaceLevelEnum getEnumByValue(Integer value) {
        if (ObjUtil.isEmpty(value)) {
            return null;
        }
        for (SpaceLevelEnum spaceLevelEnum : SpaceLevelEnum.values()) {
            if (spaceLevelEnum.value == value) {
                return spaceLevelEnum;
            }
        }
        return null;
    }
}

