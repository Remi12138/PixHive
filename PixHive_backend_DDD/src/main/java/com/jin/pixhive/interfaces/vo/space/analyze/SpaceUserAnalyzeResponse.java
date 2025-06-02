package com.jin.pixhive.interfaces.vo.space.analyze;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Space User Behavior Analyze Response
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SpaceUserAnalyzeResponse implements Serializable {

    /**
     * time period
     */
    private String period;

    private Long count;

    private static final long serialVersionUID = 1L;
}

