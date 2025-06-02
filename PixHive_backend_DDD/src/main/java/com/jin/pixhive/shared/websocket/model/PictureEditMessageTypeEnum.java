package com.jin.pixhive.shared.websocket.model;

import lombok.Getter;

@Getter
public enum PictureEditMessageTypeEnum {

    INFO("Info", "INFO"),
    ERROR("Error", "ERROR"),
    ENTER_EDIT("Enter Edit", "ENTER_EDIT"),
    EXIT_EDIT("Exit Edit", "EXIT_EDIT"),
    EDIT_ACTION("Edit Action", "EDIT_ACTION");

    private final String text;
    private final String value;

    PictureEditMessageTypeEnum(String text, String value) {
        this.text = text;
        this.value = value;
    }

    public static PictureEditMessageTypeEnum getEnumByValue(String value) {
        if (value == null || value.isEmpty()) {
            return null;
        }
        for (PictureEditMessageTypeEnum typeEnum : PictureEditMessageTypeEnum.values()) {
            if (typeEnum.value.equals(value)) {
                return typeEnum;
            }
        }
        return null;
    }
}

