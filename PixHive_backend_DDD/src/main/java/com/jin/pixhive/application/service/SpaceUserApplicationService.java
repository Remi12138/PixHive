package com.jin.pixhive.application.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.jin.pixhive.interfaces.dto.spaceuser.SpaceUserAddRequest;
import com.jin.pixhive.interfaces.dto.spaceuser.SpaceUserQueryRequest;
import com.jin.pixhive.domain.space.entity.SpaceUser;
import com.baomidou.mybatisplus.extension.service.IService;
import com.jin.pixhive.interfaces.vo.space.SpaceUserVO;

import javax.servlet.http.HttpServletRequest;
import java.util.List;


public interface SpaceUserApplicationService extends IService<SpaceUser> {
    long addSpaceUser(SpaceUserAddRequest spaceUserAddRequest);
    void validSpaceUser(SpaceUser spaceUser, boolean add);
    QueryWrapper<SpaceUser> getQueryWrapper(SpaceUserQueryRequest spaceUserQueryRequest);
    SpaceUserVO getSpaceUserVO(SpaceUser spaceUser, HttpServletRequest request);
    List<SpaceUserVO> getSpaceUserVOList(List<SpaceUser> spaceUserList);
}
