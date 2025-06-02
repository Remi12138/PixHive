package com.jin.pixhive.application.service;

import com.jin.pixhive.interfaces.dto.space.analyze.*;
import com.jin.pixhive.interfaces.vo.space.analyze.*;
import com.jin.pixhive.domain.space.entity.Space;
import com.jin.pixhive.domain.user.entity.User;

import java.util.List;

public interface SpaceAnalyzeApplicationService {
    /**
     * get Space Usage Analyze
     */
    SpaceUsageAnalyzeResponse getSpaceUsageAnalyze(SpaceUsageAnalyzeRequest spaceUsageAnalyzeRequest, User loginUser);

    /**
     * get Space Category Analyze
     */
    List<SpaceCategoryAnalyzeResponse> getSpaceCategoryAnalyze(SpaceCategoryAnalyzeRequest spaceCategoryAnalyzeRequest, User loginUser);

    /**
     * get Space Tag Analyze
     */
    List<SpaceTagAnalyzeResponse> getSpaceTagAnalyze(SpaceTagAnalyzeRequest spaceTagAnalyzeRequest, User loginUser);

    /**
     * get Space Size Analyze
     */
    List<SpaceSizeAnalyzeResponse> getSpaceSizeAnalyze(SpaceSizeAnalyzeRequest spaceSizeAnalyzeRequest, User loginUser);

    /**
     * get Space User Behavior Analyze
     */
    List<SpaceUserAnalyzeResponse> getSpaceUserAnalyze(SpaceUserAnalyzeRequest spaceUserAnalyzeRequest, User loginUser);

    /**
     * get Space Rank Analyze [admin]
     */
    List<Space> getSpaceRankAnalyze(SpaceRankAnalyzeRequest spaceRankAnalyzeRequest, User loginUser);
}
