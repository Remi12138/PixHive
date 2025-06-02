package com.jin.pixhive_backend.model.dto.space.analyze;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Space User Behavior Analyze Request
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class SpaceUserAnalyzeRequest extends SpaceAnalyzeRequest {

    private Long userId;

    /**
     * timeDimension: day / week / month
     */
    private String timeDimension;
}


