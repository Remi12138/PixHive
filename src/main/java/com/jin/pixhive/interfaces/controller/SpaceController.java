package com.jin.pixhive.interfaces.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jin.pixhive.infrastructure.annotation.AuthCheck;
import com.jin.pixhive.infrastructure.common.BaseResponse;
import com.jin.pixhive.infrastructure.common.DeleteRequest;
import com.jin.pixhive.infrastructure.common.ResultUtils;
import com.jin.pixhive.domain.user.constant.UserConstant;
import com.jin.pixhive.infrastructure.exception.BusinessException;
import com.jin.pixhive.infrastructure.exception.ErrorCode;
import com.jin.pixhive.infrastructure.exception.ThrowUtils;
import com.jin.pixhive.interfaces.assembler.SpaceAssembler;
import com.jin.pixhive.interfaces.dto.space.*;
import com.jin.pixhive.shared.auth.SpaceUserAuthManager;
import com.jin.pixhive.domain.space.entity.Space;
import com.jin.pixhive.domain.user.entity.User;
import com.jin.pixhive.domain.space.valueobject.SpaceLevelEnum;
import com.jin.pixhive.interfaces.vo.space.SpaceVO;
import com.jin.pixhive.application.service.SpaceApplicationService;
import com.jin.pixhive.application.service.UserApplicationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;


@Slf4j
@RestController
@RequestMapping("/space")
public class SpaceController {
    @Resource
    private UserApplicationService userApplicationService;

    @Resource
    private SpaceApplicationService spaceApplicationService;

    @Resource
    private SpaceUserAuthManager spaceUserAuthManager;

    @PostMapping("/add")
    public BaseResponse<Long> addSpace(@RequestBody SpaceAddRequest spaceAddRequest, HttpServletRequest request) {
        ThrowUtils.throwIf(spaceAddRequest == null, ErrorCode.PARAMS_ERROR);
        User loginUser = userApplicationService.getLoginUser(request);
        long newId = spaceApplicationService.addSpace(spaceAddRequest, loginUser);
        return ResultUtils.success(newId);
    }

    /**
     * delete space
     */
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteSpace(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userApplicationService.getLoginUser(request);
        long id = deleteRequest.getId();
        // check space exist
        Space oldSpace = spaceApplicationService.getById(id);
        ThrowUtils.throwIf(oldSpace == null, ErrorCode.NOT_FOUND_ERROR);
        // self or admin can delete
        spaceApplicationService.checkSpaceAuth(loginUser, oldSpace);
        // remove from database
        boolean result = spaceApplicationService.removeById(id);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }

    /**
     * [admin] update space (add more field)
     */
    @PostMapping("/update")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> updateSpace(@RequestBody SpaceUpdateRequest spaceUpdateRequest,
                                               HttpServletRequest request) {
        if (spaceUpdateRequest == null || spaceUpdateRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // dto -> space
        Space space = SpaceAssembler.toSpaceEntity(spaceUpdateRequest);
        // fill size, count
        spaceApplicationService.fillSpaceBySpaceLevel(space);
        // valid space
        space.validSpace(false);
        // check exist
        long id = spaceUpdateRequest.getId();
        Space oldSpace = spaceApplicationService.getById(id);
        ThrowUtils.throwIf(oldSpace == null, ErrorCode.NOT_FOUND_ERROR);


        // update to database
        boolean result = spaceApplicationService.updateById(space);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }

    /**
     * [admin] get Space By Id
     */
    @GetMapping("/get")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Space> getSpaceById(long id, HttpServletRequest request) {
        ThrowUtils.throwIf(id <= 0, ErrorCode.PARAMS_ERROR);
        // query
        Space space = spaceApplicationService.getById(id);
        ThrowUtils.throwIf(space == null, ErrorCode.NOT_FOUND_ERROR);
        return ResultUtils.success(space);
    }

    /**
     * get SpaceVO By Id
     * VO: user can view
     */
    @GetMapping("/get/vo")
    public BaseResponse<SpaceVO> getSpaceVOById(long id, HttpServletRequest request) {
        ThrowUtils.throwIf(id <= 0, ErrorCode.PARAMS_ERROR);
        // query
        Space space = spaceApplicationService.getById(id);
        ThrowUtils.throwIf(space == null, ErrorCode.NOT_FOUND_ERROR);

        SpaceVO spaceVO = spaceApplicationService.getSpaceVO(space, request);
        User loginUser = userApplicationService.getLoginUser(request);
        List<String> permissionList = spaceUserAuthManager.getPermissionList(space, loginUser);
        spaceVO.setPermissionList(permissionList);

        return ResultUtils.success(spaceVO);
    }

    /**
     * [admin] list Space By Page
     */
    @PostMapping("/list/page")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Page<Space>> listSpaceByPage(@RequestBody SpaceQueryRequest spaceQueryRequest) {
        long current = spaceQueryRequest.getCurrent();
        long size = spaceQueryRequest.getPageSize();
        // query
        Page<Space> spacePage = spaceApplicationService.page(new Page<>(current, size),
                spaceApplicationService.getQueryWrapper(spaceQueryRequest));
        return ResultUtils.success(spacePage);
    }

    /**
     * get SpaceVO By Page (show to user)
     */
    @PostMapping("/list/page/vo")
    public BaseResponse<Page<SpaceVO>> listSpaceVOByPage(@RequestBody SpaceQueryRequest spaceQueryRequest,
                                                             HttpServletRequest request) {
        long current = spaceQueryRequest.getCurrent();
        long size = spaceQueryRequest.getPageSize();
        // Restricted crawler
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        // query
        Page<Space> spacePage = spaceApplicationService.page(new Page<>(current, size),
                spaceApplicationService.getQueryWrapper(spaceQueryRequest));
        // get VO
        return ResultUtils.success(spaceApplicationService.getSpaceVOPage(spacePage, request));
    }

    /**
     * (user) edit space
     */
    @PostMapping("/edit")
    public BaseResponse<Boolean> editSpace(@RequestBody SpaceEditRequest spaceEditRequest, HttpServletRequest request) {
        if (spaceEditRequest == null || spaceEditRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // dto -> space
        Space space = SpaceAssembler.toSpaceEntity(spaceEditRequest);
        // fill size, count
        spaceApplicationService.fillSpaceBySpaceLevel(space);
        // manually set edit time (updateTime automatically)
        space.setEditTime(new Date());
        // valid space
        space.validSpace(false);
        User loginUser = userApplicationService.getLoginUser(request);
        // check exist
        long id = spaceEditRequest.getId();
        Space oldSpace = spaceApplicationService.getById(id);
        ThrowUtils.throwIf(oldSpace == null, ErrorCode.NOT_FOUND_ERROR);
        // self or admin can edit
        spaceApplicationService.checkSpaceAuth(loginUser, oldSpace);
        // update in database
        boolean result = spaceApplicationService.updateById(space);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }

    /**
     * get all level list, show on frontend
     * @return
     */
    @GetMapping("/list/level")
    public BaseResponse<List<SpaceLevel>> listSpaceLevel() {
        List<SpaceLevel> spaceLevelList = Arrays.stream(SpaceLevelEnum.values())
                .map(spaceLevelEnum -> new SpaceLevel(
                        spaceLevelEnum.getValue(),
                        spaceLevelEnum.getText(),
                        spaceLevelEnum.getMaxCount(),
                        spaceLevelEnum.getMaxSize()))
                .collect(Collectors.toList());
        return ResultUtils.success(spaceLevelList);
    }
}
