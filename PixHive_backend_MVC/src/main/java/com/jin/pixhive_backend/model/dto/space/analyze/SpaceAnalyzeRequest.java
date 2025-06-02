package com.jin.pixhive_backend.model.dto.space.analyze;

import lombok.Data;

import java.io.Serializable;

/**
 * General Space Analyze Request
 */
@Data
public class SpaceAnalyzeRequest implements Serializable {

    private Long spaceId;

    private boolean queryPublic;

    private boolean queryAll;

    private static final long serialVersionUID = 1L;
}

