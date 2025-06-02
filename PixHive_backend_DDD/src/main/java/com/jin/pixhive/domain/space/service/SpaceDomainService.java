package com.jin.pixhive.domain.space.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.jin.pixhive.domain.space.entity.Space;
import com.jin.pixhive.domain.user.entity.User;
import com.jin.pixhive.interfaces.dto.space.SpaceQueryRequest;

public interface SpaceDomainService {

    /**
     * get Query Wrapper
     * @param spaceQueryRequest
     * @return
     */
    QueryWrapper<Space> getQueryWrapper(SpaceQueryRequest spaceQueryRequest);

    /**
     * fill maxSize, maxCount by space level
     * @param space
     */
    void fillSpaceBySpaceLevel(Space space);

    void checkSpaceAuth(User loginUser, Space space);
}
