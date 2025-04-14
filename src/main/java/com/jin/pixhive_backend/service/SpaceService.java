package com.jin.pixhive_backend.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jin.pixhive_backend.model.dto.space.SpaceAddRequest;
import com.jin.pixhive_backend.model.dto.space.SpaceQueryRequest;
import com.jin.pixhive_backend.model.entity.Space;
import com.jin.pixhive_backend.model.entity.Space;
import com.baomidou.mybatisplus.extension.service.IService;
import com.jin.pixhive_backend.model.entity.User;
import com.jin.pixhive_backend.model.vo.SpaceVO;

import javax.servlet.http.HttpServletRequest;

/**
* @author xianjinghuang
* @description database operation service for table [space]
*/
public interface SpaceService extends IService<Space> {
    /**
     * space cannot be null;
     * create: name & level cannot be null
     * create / edit: level exist, name.len <= 30
     * @param space
     * @param add true: create, false: edit
     */
    void validSpace(Space space, boolean add);

    /**
     * (single) desensitization: space -> VO
     * @param space
     * @param request
     * @return
     */
    SpaceVO getSpaceVO(Space space, HttpServletRequest request);

    /**
     * (page) desensitization: space -> VO
     * @param spacePage
     * @param request
     * @return
     */
    Page<SpaceVO> getSpaceVOPage(Page<Space> spacePage, HttpServletRequest request);

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

    /**
     * user create a new space
     * @param spaceAddRequest
     * @param loginUser
     * @return
     */
    long addSpace(SpaceAddRequest spaceAddRequest, User loginUser);
}
