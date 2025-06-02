package com.jin.pixhive_backend.model.dto.space.analyze;

import lombok.Data;

import java.io.Serializable;

/**
 * Space Rank Analyze Request [admin]
 */
@Data
public class SpaceRankAnalyzeRequest implements Serializable {

    private Integer topN = 10;

    private static final long serialVersionUID = 1L;
}


