package com.jin.pixhive.domain.space.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.jin.pixhive.domain.space.entity.SpaceUser;
import com.jin.pixhive.interfaces.dto.spaceuser.SpaceUserQueryRequest;

public interface SpaceUserDomainService {
    QueryWrapper<SpaceUser> getQueryWrapper(SpaceUserQueryRequest spaceUserQueryRequest);
}
