package com.jin.pixhive_backend.controller;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.jin.pixhive_backend.annotation.AuthCheck;
import com.jin.pixhive_backend.common.BaseResponse;
import com.jin.pixhive_backend.common.DeleteRequest;
import com.jin.pixhive_backend.common.ResultUtils;
import com.jin.pixhive_backend.constant.UserConstant;
import com.jin.pixhive_backend.exception.BusinessException;
import com.jin.pixhive_backend.exception.ErrorCode;
import com.jin.pixhive_backend.exception.ThrowUtils;
import com.jin.pixhive_backend.manage.delete.PictureFileCleaner;
import com.jin.pixhive_backend.model.dto.picture.*;
import com.jin.pixhive_backend.model.entity.Picture;
import com.jin.pixhive_backend.model.entity.User;
import com.jin.pixhive_backend.model.enums.PictureReviewStatusEnum;
import com.jin.pixhive_backend.model.vo.PictureTagCategory;
import com.jin.pixhive_backend.model.vo.PictureVO;
import com.jin.pixhive_backend.service.PictureService;
import com.jin.pixhive_backend.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;


@Slf4j
@RestController
@RequestMapping("/picture")
public class PictureController {
    @Resource
    private UserService userService;

    @Resource
    private PictureService pictureService;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private PictureFileCleaner pictureFileCleaner;

    private final Cache<String, String> LOCAL_CACHE =
            Caffeine.newBuilder().initialCapacity(1024)
                    .maximumSize(10000L)
                    // expire in 5min
                    .expireAfterWrite(5L, TimeUnit.MINUTES)
                    .build();

    /**
     * upload picture (can re-upload, update)
     */
    @PostMapping("/upload")
//    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<PictureVO> uploadPicture(
            @RequestPart("file") MultipartFile multipartFile,
            PictureUploadRequest pictureUploadRequest,
            HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        PictureVO pictureVO = pictureService.uploadPicture(multipartFile, pictureUploadRequest, loginUser);
        return ResultUtils.success(pictureVO);
    }

    /**
     * by URL upload picture (can re-upload, update)
     */
    @PostMapping("/upload/url")
    public BaseResponse<PictureVO> uploadPictureByUrl(
            @RequestBody PictureUploadRequest pictureUploadRequest,
            HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        String fileUrl = pictureUploadRequest.getFileUrl();
        PictureVO pictureVO = pictureService.uploadPicture(fileUrl, pictureUploadRequest, loginUser);
        return ResultUtils.success(pictureVO);
    }


    /**
     * delete picture
     */
    @PostMapping("/delete")
    public BaseResponse<Boolean> deletePicture(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        long id = deleteRequest.getId();
        // check picture exist
        Picture oldPicture = pictureService.getById(id);
        ThrowUtils.throwIf(oldPicture == null, ErrorCode.NOT_FOUND_ERROR);
        // self or admin can delete
        if (!oldPicture.getUserId().equals(loginUser.getId()) && !userService.isAdmin(loginUser)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        // remove from database
        boolean result = pictureService.removeById(id);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        // delete in COS
        pictureFileCleaner.clearPictureFile(oldPicture);
        return ResultUtils.success(true);
    }

    /**
     * [admin] update picture (add more field)
     */
    @PostMapping("/update")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> updatePicture(@RequestBody PictureUpdateRequest pictureUpdateRequest,
                                               HttpServletRequest request) {
        if (pictureUpdateRequest == null || pictureUpdateRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // dto -> picture
        Picture picture = new Picture();
        BeanUtils.copyProperties(pictureUpdateRequest, picture);
        // tag list -> Json string
        picture.setTags(JSONUtil.toJsonStr(pictureUpdateRequest.getTags()));
        // valid picture
        pictureService.validPicture(picture);
        // check exist
        long id = pictureUpdateRequest.getId();
        Picture oldPicture = pictureService.getById(id);
        ThrowUtils.throwIf(oldPicture == null, ErrorCode.NOT_FOUND_ERROR);
        // fill review
        User loginUser = userService.getLoginUser(request);
        pictureService.fillReviewParams(oldPicture, loginUser);
        // update to database
        boolean result = pictureService.updateById(picture);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }

    /**
     * [admin] get Picture By Id
     */
    @GetMapping("/get")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Picture> getPictureById(long id, HttpServletRequest request) {
        ThrowUtils.throwIf(id <= 0, ErrorCode.PARAMS_ERROR);
        // query
        Picture picture = pictureService.getById(id);
        ThrowUtils.throwIf(picture == null, ErrorCode.NOT_FOUND_ERROR);
        return ResultUtils.success(picture);
    }

    /**
     * get PictureVO By Id
     * VO: user can view
     */
    @GetMapping("/get/vo")
    public BaseResponse<PictureVO> getPictureVOById(long id, HttpServletRequest request) {
        ThrowUtils.throwIf(id <= 0, ErrorCode.PARAMS_ERROR);
        // query
        Picture picture = pictureService.getById(id);
        ThrowUtils.throwIf(picture == null, ErrorCode.NOT_FOUND_ERROR);
        // get VO
        return ResultUtils.success(pictureService.getPictureVO(picture, request));
    }

    /**
     * [admin] list Picture By Page
     */
    @PostMapping("/list/page")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Page<Picture>> listPictureByPage(@RequestBody PictureQueryRequest pictureQueryRequest) {
        long current = pictureQueryRequest.getCurrent();
        long size = pictureQueryRequest.getPageSize();
        // query
        Page<Picture> picturePage = pictureService.page(new Page<>(current, size),
                pictureService.getQueryWrapper(pictureQueryRequest));
        return ResultUtils.success(picturePage);
    }

    /**
     * get PictureVO By Page (show to user)
     */
    @PostMapping("/list/page/vo")
    public BaseResponse<Page<PictureVO>> listPictureVOByPage(@RequestBody PictureQueryRequest pictureQueryRequest,
                                                             HttpServletRequest request) {
        long current = pictureQueryRequest.getCurrent();
        long size = pictureQueryRequest.getPageSize();
        // Restricted crawler
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        // user can only see "pass" picture
        pictureQueryRequest.setReviewStatus(PictureReviewStatusEnum.PASS.getValue());
        // query
        Page<Picture> picturePage = pictureService.page(new Page<>(current, size),
                pictureService.getQueryWrapper(pictureQueryRequest));
        // get VO
        return ResultUtils.success(pictureService.getPictureVOPage(picturePage, request));
    }

    /**
     * get PictureVO By Page with cache
     */
    @PostMapping("/list/page/vo/cache")
    public BaseResponse<Page<PictureVO>> listPictureVOByPageWithCache(@RequestBody PictureQueryRequest pictureQueryRequest,
                                                                      HttpServletRequest request) {
        long current = pictureQueryRequest.getCurrent();
        long size = pictureQueryRequest.getPageSize();
        // Restricted crawler
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        // user can only see "pass" picture
        pictureQueryRequest.setReviewStatus(PictureReviewStatusEnum.PASS.getValue());

        // create cache key
        String queryCondition = JSONUtil.toJsonStr(pictureQueryRequest);
        String hashKey = DigestUtils.md5DigestAsHex(queryCondition.getBytes());
        String cacheKey = "PixHive:listPictureVOByPage:" + hashKey;

        // 1. query local caching -- Caffeine
        String cachedValue = LOCAL_CACHE.getIfPresent(cacheKey);
        if (cachedValue != null) {
            Page<PictureVO> cachedPage = JSONUtil.toBean(cachedValue, Page.class);
            return ResultUtils.success(cachedPage);
        }
        // 2. not in Caffeine, query distributed cache -- Redis
        ValueOperations<String, String> valueOps = stringRedisTemplate.opsForValue();
        cachedValue = valueOps.get(cacheKey);
        if (cachedValue != null) {
            // find in Redis, cache in local, and return
            LOCAL_CACHE.put(cacheKey, cachedValue);
            Page<PictureVO> cachedPage = JSONUtil.toBean(cachedValue, Page.class);
            return ResultUtils.success(cachedPage);
        }
        // 3. query database
        Page<Picture> picturePage = pictureService.page(new Page<>(current, size),
                pictureService.getQueryWrapper(pictureQueryRequest));
        Page<PictureVO> pictureVOPage = pictureService.getPictureVOPage(picturePage, request);

        // 4. update cache
        String cacheValue = JSONUtil.toJsonStr(pictureVOPage);
        // update Caffeine
        LOCAL_CACHE.put(cacheKey, cacheValue);
        // update Redis
        // 5-10 minutes of random expiration to prevent avalanches
        int cacheExpireTime = 300 +  RandomUtil.randomInt(0, 300);
        valueOps.set(cacheKey, cacheValue, cacheExpireTime, TimeUnit.SECONDS);

        return ResultUtils.success(pictureVOPage);
    }

    /**
     * (user) edit picture
     */
    @PostMapping("/edit")
    public BaseResponse<Boolean> editPicture(@RequestBody PictureEditRequest pictureEditRequest, HttpServletRequest request) {
        if (pictureEditRequest == null || pictureEditRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // dto -> picture
        Picture picture = new Picture();
        BeanUtils.copyProperties(pictureEditRequest, picture);
        picture.setTags(JSONUtil.toJsonStr(pictureEditRequest.getTags()));
        // manually set edit time (updateTime automatically)
        picture.setEditTime(new Date());
        // valid picture
        pictureService.validPicture(picture);
        User loginUser = userService.getLoginUser(request);
        // fill review
        pictureService.fillReviewParams(picture, loginUser);
        // check exist
        long id = pictureEditRequest.getId();
        Picture oldPicture = pictureService.getById(id);
        ThrowUtils.throwIf(oldPicture == null, ErrorCode.NOT_FOUND_ERROR);
        // self or admin can edit
        if (!oldPicture.getUserId().equals(loginUser.getId()) && !userService.isAdmin(loginUser)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        // update in database
        boolean result = pictureService.updateById(picture);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }

    /**
     * show Picture & Tag Category in menu
     */
    @GetMapping("/tag_category")
    public BaseResponse<PictureTagCategory> listPictureTagCategory() {
        PictureTagCategory pictureTagCategory = new PictureTagCategory();
        List<String> tagList = Arrays.asList("hot", "life", "funny", "creativity", "green", "campus", "background", "resume", "high-definition");
        List<String> categoryList = Arrays.asList("Wallpapers", "People", "Animals", "Nature", "Food & Drink", "Default");
        pictureTagCategory.setTagList(tagList);
        pictureTagCategory.setCategoryList(categoryList);
        return ResultUtils.success(pictureTagCategory);
    }

    /**
     * admin do review picture
     */
    @PostMapping("/review")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> doPictureReview(@RequestBody PictureReviewRequest pictureReviewRequest,
                                                 HttpServletRequest request) {
        ThrowUtils.throwIf(pictureReviewRequest == null, ErrorCode.PARAMS_ERROR);
        User loginUser = userService.getLoginUser(request);
        pictureService.doPictureReview(pictureReviewRequest, loginUser);
        return ResultUtils.success(true);
    }

    /**
     * crawler picture
     */
    @PostMapping("/upload/batch")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Integer> uploadPictureByBatch(
            @RequestBody PictureUploadByBatchRequest pictureUploadByBatchRequest,
            HttpServletRequest request) {
        ThrowUtils.throwIf(pictureUploadByBatchRequest == null, ErrorCode.PARAMS_ERROR);
        User loginUser = userService.getLoginUser(request);
        int uploadCount = pictureService.uploadPictureByBatch(pictureUploadByBatchRequest, loginUser);
        return ResultUtils.success(uploadCount);
    }

}
