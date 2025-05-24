package com.jin.pixhive.application.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jin.pixhive.interfaces.dto.space.SpaceAddRequest;
import com.jin.pixhive.interfaces.dto.space.SpaceQueryRequest;
import com.jin.pixhive.domain.space.entity.Space;
import com.baomidou.mybatisplus.extension.service.IService;
import com.jin.pixhive.domain.user.entity.User;
import com.jin.pixhive.interfaces.vo.space.SpaceVO;

import javax.servlet.http.HttpServletRequest;

public interface SpaceApplicationService extends IService<Space> {

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

    void checkSpaceAuth(User loginUser, Space space);
}
