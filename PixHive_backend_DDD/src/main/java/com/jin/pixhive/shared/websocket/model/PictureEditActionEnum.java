package com.jin.pixhive.shared.websocket.model;

import lombok.Getter;

@Getter
public enum PictureEditActionEnum {

    ZOOM_IN("Zoom in", "ZOOM_IN"),
    ZOOM_OUT("Zoom out", "ZOOM_OUT"),
    ROTATE_LEFT("Rotate Left", "ROTATE_LEFT"),
    ROTATE_RIGHT("Rotate Right", "ROTATE_RIGHT");

    private final String text;
    private final String value;

    PictureEditActionEnum(String text, String value) {
        this.text = text;
        this.value = value;
    }

    public static PictureEditActionEnum getEnumByValue(String value) {
        if (value == null || value.isEmpty()) {
            return null;
        }
        for (PictureEditActionEnum actionEnum : PictureEditActionEnum.values()) {
            if (actionEnum.value.equals(value)) {
                return actionEnum;
            }
        }
        return null;
    }
}
