package com.jin.pixhive_backend.model.vo.space.analyze;

import lombok.Data;

import java.io.Serializable;

/**
 * Space Usage Analyze Response
 */
@Data
public class SpaceUsageAnalyzeResponse implements Serializable {

    private Long usedSize;

    private Long maxSize;

    private Double sizeUsageRatio;

    private Long usedCount;

    private Long maxCount;

    private Double countUsageRatio;

    private static final long serialVersionUID = 1L;
}
