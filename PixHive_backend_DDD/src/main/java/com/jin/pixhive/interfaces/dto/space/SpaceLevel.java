package com.jin.pixhive.interfaces.dto.space;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

/**
 * space level (could display on frontend)
 */
@Data
@AllArgsConstructor
public class SpaceLevel {

    private int value;

    private String text;

    private long maxCount;

    private long maxSize;
}






