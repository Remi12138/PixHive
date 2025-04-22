package com.jin.pixhive_backend.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.jin.pixhive_backend.model.dto.spaceuser.SpaceUserAddRequest;
import com.jin.pixhive_backend.model.dto.spaceuser.SpaceUserQueryRequest;
import com.jin.pixhive_backend.model.entity.SpaceUser;
import com.baomidou.mybatisplus.extension.service.IService;
import com.jin.pixhive_backend.model.vo.SpaceUserVO;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
* @author xianjinghuang
*/
public interface SpaceUserService extends IService<SpaceUser> {
    long addSpaceUser(SpaceUserAddRequest spaceUserAddRequest);
    void validSpaceUser(SpaceUser spaceUser, boolean add);
    QueryWrapper<SpaceUser> getQueryWrapper(SpaceUserQueryRequest spaceUserQueryRequest);
    SpaceUserVO getSpaceUserVO(SpaceUser spaceUser, HttpServletRequest request);
    List<SpaceUserVO> getSpaceUserVOList(List<SpaceUser> spaceUserList);
}
