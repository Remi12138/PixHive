package com.jin.pixhive.domain.picture.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.jin.pixhive.domain.picture.entity.Picture;
import com.jin.pixhive.domain.picture.repository.PictureRepository;
import com.jin.pixhive.domain.picture.service.PictureDomainService;
import com.jin.pixhive.domain.picture.valueobject.PictureReviewStatusEnum;
import com.jin.pixhive.domain.user.entity.User;
import com.jin.pixhive.infrastructure.api.aliyunai.AliYunAiApi;
import com.jin.pixhive.infrastructure.api.aliyunai.model.CreateOutPaintingTaskRequest;
import com.jin.pixhive.infrastructure.api.aliyunai.model.CreateOutPaintingTaskResponse;
import com.jin.pixhive.infrastructure.exception.BusinessException;
import com.jin.pixhive.infrastructure.exception.ErrorCode;
import com.jin.pixhive.infrastructure.exception.ThrowUtils;
import com.jin.pixhive.infrastructure.utils.ColorHexExpanderUtils;
import com.jin.pixhive.infrastructure.utils.ColorSimilarUtils;
import com.jin.pixhive.interfaces.dto.picture.*;
import com.jin.pixhive.interfaces.vo.picture.PictureVO;
import com.jin.pixhive.infrastructure.manage.delete.PictureFileCleaner;
import com.jin.pixhive.infrastructure.manage.upload.FilePictureUpload;
import com.jin.pixhive.infrastructure.manage.upload.PictureUploadTemplate;
import com.jin.pixhive.infrastructure.manage.upload.UrlPictureUpload;
import com.jin.pixhive.infrastructure.manage.upload.model.dto.file.UploadPictureResult;
import com.jin.pixhive.domain.space.entity.Space;
import com.jin.pixhive.application.service.SpaceApplicationService;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.Resource;
import java.awt.*;
import java.io.IOException;
import java.util.List;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class PictureDomainServiceImpl implements PictureDomainService {

    @Resource
    private PictureRepository pictureRepository;

    @Resource
    private FilePictureUpload filePictureUpload;

    @Resource
    private UrlPictureUpload urlPictureUpload;

    @Resource
    private PictureFileCleaner pictureFileCleaner;

    @Resource
    private SpaceApplicationService spaceApplicationService;

    @Resource
    private TransactionTemplate transactionTemplate;

    @Resource
    private AliYunAiApi aliYunAiApi;

    @Override
    public PictureVO uploadPicture(Object inputSource, PictureUploadRequest pictureUploadRequest, User loginUser) {
        ThrowUtils.throwIf(loginUser == null, ErrorCode.NO_AUTH_ERROR);
        // check space
        Long spaceId = pictureUploadRequest.getSpaceId();
        if (spaceId != null) {
            // check if space exist
            Space space = spaceApplicationService.getById(spaceId);
            ThrowUtils.throwIf(space == null, ErrorCode.NOT_FOUND_ERROR, "Space not found!");
            // only space creator(admin) can upload
            // ! has been changed to use annotation authentication
//            if (!loginUser.getId().equals(space.getUserId())) {
//                throw new BusinessException(ErrorCode.NO_AUTH_ERROR, "No permission to upload picture to this space!");
//            }
            // check quota
            if (space.getTotalCount() >= space.getMaxCount()) {
                throw new BusinessException(ErrorCode.OPERATION_ERROR, "Space max count exceeded!");
            }
            if (space.getTotalSize() >= space.getMaxSize()) {
                throw new BusinessException(ErrorCode.OPERATION_ERROR, "Space max size exceeded!");
            }
        }

        // determine update/new picture
        Long pictureId = null;
        if (pictureUploadRequest != null) {
            pictureId = pictureUploadRequest.getId();
        }
        // already has pictureId -> update picture，need to check if picture exists
        if (pictureId != null) {
            Picture oldPicture = pictureRepository.getById(pictureId);
            ThrowUtils.throwIf(oldPicture == null, ErrorCode.NOT_FOUND_ERROR, "Picture not exist. pictureId: " + pictureId);
            // self & admin can update
            // ! has been changed to use annotation authentication
//            if (!oldPicture.getUserId().equals(loginUser.getId()) && !userService.isAdmin(loginUser)) {
//                throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
//            }
            // Check whether the space is the same as when created
            if (spaceId == null) {
                // no pass spaceId, use old one (also works for public)
                spaceId = oldPicture.getSpaceId();
            } else {
                if (ObjUtil.notEqual(spaceId, oldPicture.getSpaceId())) {
                    throw new BusinessException(ErrorCode.PARAMS_ERROR, "Updated SpaceId and Original spaceId diff!");
                }
            }
            // delete old picture in COS
            pictureFileCleaner.clearPictureFile(oldPicture);
            // only when store/delete in database, re-compute quote.
            // no need to compute here
        }
        // set path by userId and spaceId
        String uploadPathPrefix;
        if (spaceId == null) {
            // to public
            uploadPathPrefix = String.format("public/%s", loginUser.getId());
        } else {
            // to space
            uploadPathPrefix = String.format("space/%s", spaceId);
        }

        // inputSource -> different upload function
        PictureUploadTemplate pictureUploadTemplate = filePictureUpload;
        if (inputSource instanceof String) {
            pictureUploadTemplate = urlPictureUpload;
        }
        // upload picture
        UploadPictureResult uploadPictureResult = pictureUploadTemplate.uploadPicture(inputSource, uploadPathPrefix);

        // constructs the picture information to be stored
        Picture picture = new Picture();
        picture.setSpaceId(spaceId);
        picture.setUrl(uploadPictureResult.getUrl());
        picture.setThumbnailUrl(uploadPictureResult.getThumbnailUrl());

        // support pictureUploadRequest pass picture name
        String picName = uploadPictureResult.getPicName();
        if (pictureUploadRequest != null && StrUtil.isNotBlank(pictureUploadRequest.getPicName())) {
            picName = pictureUploadRequest.getPicName();
        }
        picture.setName(picName);

        picture.setPicSize(uploadPictureResult.getPicSize());
        picture.setPicWidth(uploadPictureResult.getPicWidth());
        picture.setPicHeight(uploadPictureResult.getPicHeight());
        picture.setPicScale(uploadPictureResult.getPicScale());
        picture.setPicFormat(uploadPictureResult.getPicFormat());
//        picture.setPicColor(uploadPictureResult.getPicColor());
        // to standard RGB
        picture.setPicColor(ColorHexExpanderUtils.expandHexColor(uploadPictureResult.getPicColor()));
        picture.setUserId(loginUser.getId());
        // fill review
        fillReviewParams(picture, loginUser);

        // if pictureId != null, -> update,  else -> new picture
        if (pictureId != null) {
            // update, assign id, editTime
            picture.setId(pictureId);
            picture.setEditTime(new Date());
        }
        // if no assign id -> insert (new one)
        // assign id -> update

        // execute transaction
        Long finalSpaceId = spaceId;
        transactionTemplate.execute(status -> {
            boolean result = pictureRepository.saveOrUpdate(picture);
            ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR, "Upload Error, database operation failed!");
            if (finalSpaceId != null) {
                boolean update = spaceApplicationService.lambdaUpdate()
                        .eq(Space::getId, finalSpaceId)
                        .setSql("totalSize = totalSize + " + picture.getPicSize())
                        .setSql("totalCount = totalCount + 1")
                        .update();
                ThrowUtils.throwIf(!update, ErrorCode.OPERATION_ERROR, "Space size & count update failed!");
            }
            return picture;
        });

        return PictureVO.objToVo(picture);
    }

    @Override
    public QueryWrapper<Picture> getQueryWrapper(PictureQueryRequest pictureQueryRequest) {
        QueryWrapper<Picture> queryWrapper = new QueryWrapper<>();
        if (pictureQueryRequest == null) {
            return queryWrapper;
        }
        // get value from obj
        Long id = pictureQueryRequest.getId();
        String name = pictureQueryRequest.getName();
        String introduction = pictureQueryRequest.getIntroduction();
        String category = pictureQueryRequest.getCategory();
        List<String> tags = pictureQueryRequest.getTags();
        Long picSize = pictureQueryRequest.getPicSize();
        Integer picWidth = pictureQueryRequest.getPicWidth();
        Integer picHeight = pictureQueryRequest.getPicHeight();
        Double picScale = pictureQueryRequest.getPicScale();
        String picFormat = pictureQueryRequest.getPicFormat();
        String searchText = pictureQueryRequest.getSearchText();
        Long userId = pictureQueryRequest.getUserId();
        Integer reviewStatus = pictureQueryRequest.getReviewStatus();
        String reviewMessage = pictureQueryRequest.getReviewMessage();
        Long reviewerId = pictureQueryRequest.getReviewerId();
        String sortField = pictureQueryRequest.getSortField();
        String sortOrder = pictureQueryRequest.getSortOrder();
        Long spaceId = pictureQueryRequest.getSpaceId();
        boolean nullSpaceId = pictureQueryRequest.isNullSpaceId();
        Date startEditTime = pictureQueryRequest.getStartEditTime();
        Date endEditTime = pictureQueryRequest.getEndEditTime();
        // Search from multiple fields
        if (StrUtil.isNotBlank(searchText)) {
            // Concatenate query condition
            queryWrapper.and(qw -> qw.like("name", searchText)
                    .or()
                    .like("introduction", searchText)
            );
        }
        queryWrapper.eq(ObjUtil.isNotEmpty(id), "id", id);
        queryWrapper.eq(ObjUtil.isNotEmpty(userId), "userId", userId);
        queryWrapper.eq(ObjUtil.isNotEmpty(spaceId), "spaceId", spaceId);
        queryWrapper.isNull(nullSpaceId, "spaceId");
        queryWrapper.like(StrUtil.isNotBlank(name), "name", name);
        queryWrapper.like(StrUtil.isNotBlank(introduction), "introduction", introduction);
        queryWrapper.like(StrUtil.isNotBlank(picFormat), "picFormat", picFormat);
        queryWrapper.eq(StrUtil.isNotBlank(category), "category", category);
        queryWrapper.eq(ObjUtil.isNotEmpty(picWidth), "picWidth", picWidth);
        queryWrapper.eq(ObjUtil.isNotEmpty(picHeight), "picHeight", picHeight);
        queryWrapper.eq(ObjUtil.isNotEmpty(picSize), "picSize", picSize);
        queryWrapper.eq(ObjUtil.isNotEmpty(picScale), "picScale", picScale);
        queryWrapper.eq(ObjUtil.isNotEmpty(reviewStatus), "reviewStatus", reviewStatus);
        queryWrapper.eq(ObjUtil.isNotEmpty(reviewerId), "reviewerId", reviewerId);
        queryWrapper.like(StrUtil.isNotBlank(reviewMessage), "reviewMessage", reviewMessage);
        // startEditTime <= .. < endEditTime
        queryWrapper.ge(ObjUtil.isNotEmpty(startEditTime), "editTime", startEditTime);
        queryWrapper.lt(ObjUtil.isNotEmpty(endEditTime), "editTime", endEditTime);

        // JSON query
        if (CollUtil.isNotEmpty(tags)) {
            for (String tag : tags) {
                queryWrapper.like("tags", "\"" + tag + "\"");
            }
        }
        // sort
        queryWrapper.orderBy(StrUtil.isNotEmpty(sortField), sortOrder.equals("ascend"), sortField);
        return queryWrapper;
    }

    @Override
    public void validPicture(Picture picture) {
        ThrowUtils.throwIf(picture == null, ErrorCode.PARAMS_ERROR);
        picture.validPicture();
    }

    @Override
    public void doPictureReview(PictureReviewRequest pictureReviewRequest, User loginUser) {
        // check params
        ThrowUtils.throwIf(pictureReviewRequest == null, ErrorCode.PARAMS_ERROR);
        Long id = pictureReviewRequest.getId();
        Integer reviewStatus = pictureReviewRequest.getReviewStatus();
        PictureReviewStatusEnum reviewStatusEnum = PictureReviewStatusEnum.getEnumByValue(reviewStatus);
        // pass in pictureID should be in status pass/reject
        // should not pass in reviewing
        if (id == null || reviewStatusEnum == null || PictureReviewStatusEnum.REVIEWING.equals(reviewStatusEnum)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // check exist
        Picture oldPicture = pictureRepository.getById(id);
        ThrowUtils.throwIf(oldPicture == null, ErrorCode.NOT_FOUND_ERROR);
        // duplicate check (same status)
        if (oldPicture.getReviewStatus().equals(reviewStatus)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "duplicate check picture");
        }
        // update status
        Picture updatePicture = new Picture();
        BeanUtils.copyProperties(pictureReviewRequest, updatePicture);
        updatePicture.setReviewerId(loginUser.getId());
        updatePicture.setReviewTime(new Date());
        boolean result = pictureRepository.updateById(updatePicture);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
    }

    @Override
    public void fillReviewParams(Picture picture, User loginUser) {
        if (loginUser.isAdmin()) {
            // admin -> always pass
            picture.setReviewStatus(PictureReviewStatusEnum.PASS.getValue());
            picture.setReviewerId(loginUser.getId());
            picture.setReviewMessage("Uploaded by the administrator, it automatically passes!");
            picture.setReviewTime(new Date());
        } else {
            // non-admin, create/update -> reviewing
            picture.setReviewStatus(PictureReviewStatusEnum.REVIEWING.getValue());
        }
    }

    @Override
    public Integer uploadPictureByBatch(PictureUploadByBatchRequest pictureUploadByBatchRequest, User loginUser) {
        String searchText = pictureUploadByBatchRequest.getSearchText();
        // limit count
        Integer count = pictureUploadByBatchRequest.getCount();
        ThrowUtils.throwIf(count > 30, ErrorCode.PARAMS_ERROR, "batch size max: 30");
        String namePrefix = pictureUploadByBatchRequest.getNamePrefix();
        if (StrUtil.isBlank(namePrefix)) {
            log.info("namePrefix is blank");
            namePrefix = searchText + "_";
        }
        // fetch url
        String fetchUrl = String.format("https://cn.bing.com/images/async?q=%s&mmasync=1", searchText);
        Document document;
        try {
            document = Jsoup.connect(fetchUrl).get();
        } catch (IOException e) {
            log.error("Fetch Error", e);
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "Fetch Error");
        }
        // parsing content
        Element div = document.getElementsByClass("dgControl").first();
        if (ObjUtil.isNull(div)) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "Fetch Error");
        }
        Elements imgElementList = div.select("img.mimg");
        int uploadCount = 0;
        // traverse, upload in turn
        for (Element imgElement : imgElementList) {
            String fileUrl = imgElement.attr("src");
            if (StrUtil.isBlank(fileUrl)) {
                log.info("This url is blank, pass: {}", fileUrl);
                continue;
            }
            // remove url after ? to avoid escape problems
            int questionMarkIndex = fileUrl.indexOf("?");
            if (questionMarkIndex > -1) {
                fileUrl = fileUrl.substring(0, questionMarkIndex);
            }
            // upload
            PictureUploadRequest pictureUploadRequest = new PictureUploadRequest();
            pictureUploadRequest.setFileUrl(fileUrl);
            log.info("namePrefix = {}", namePrefix);
            pictureUploadRequest.setPicName(namePrefix + (1 + uploadCount));
            try {
                PictureVO pictureVO = this.uploadPicture(fileUrl, pictureUploadRequest, loginUser);
                log.info("Upload Success, id = {}", pictureVO.getId());
                uploadCount++;
            } catch (Exception e) {
                log.error("Upload Error", e);
                continue;
            }
            if (uploadCount >= count) {
                break;
            }
        }
        return uploadCount;
    }

    /**
     * delete picture in database (include delete in COS)
     */
    @Override
    public void deletePicture(long pictureId, User loginUser) {
        ThrowUtils.throwIf(pictureId <= 0, ErrorCode.PARAMS_ERROR);
        ThrowUtils.throwIf(loginUser == null, ErrorCode.NO_AUTH_ERROR);
        // check picture exist
        Picture oldPicture = pictureRepository.getById(pictureId);
        ThrowUtils.throwIf(oldPicture == null, ErrorCode.NOT_FOUND_ERROR);
        // check auth
        // ! has been changed to use annotation authentication
        // checkPictureAuth(loginUser, oldPicture);

        // execute transaction
        transactionTemplate.execute(status -> {
            // remove from database
            boolean result = pictureRepository.removeById(pictureId);
            ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR, "Delete Error, database operation failed!");

            if (oldPicture.getSpaceId() != null) {
                boolean update = spaceApplicationService.lambdaUpdate()
                        .eq(Space::getId, oldPicture.getSpaceId())
                        .setSql("totalSize = totalSize - " + oldPicture.getPicSize())
                        .setSql("totalCount = totalCount - 1")
                        .update();
                ThrowUtils.throwIf(!update, ErrorCode.OPERATION_ERROR, "Space size & count update failed!");
            }
            return true;
        });
        // delete in COS
        pictureFileCleaner.clearPictureFile(oldPicture);
    }

    @Override
    public void checkPictureAuth(User loginUser, Picture picture) {
        Long spaceId = picture.getSpaceId();
        if (spaceId == null) {
            // public, only self and admin
            if (!picture.getUserId().equals(loginUser.getId()) && !loginUser.isAdmin()) {
                throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
            }
        } else {
            // private space, only space admin
            if (!picture.getUserId().equals(loginUser.getId())) {
                throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
            }
        }
    }

    @Override
    public void editPicture(Picture picture, User loginUser) {
        // manually set edit time (updateTime automatically)
        picture.setEditTime(new Date());
        // valid picture
        picture.validPicture();
        // check exist
        long id = picture.getId();
        Picture oldPicture = pictureRepository.getById(id);
        ThrowUtils.throwIf(oldPicture == null, ErrorCode.NOT_FOUND_ERROR);
        // check auth
        // ! has been changed to use annotation authentication
        // checkPictureAuth(loginUser, oldPicture);
        // fill review
        this.fillReviewParams(picture, loginUser);
        // update in database
        boolean result = pictureRepository.updateById(picture);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
    }

    @Override
    public List<PictureVO> searchPictureByColor(Long spaceId, String picColor, User loginUser) {
        // 1. check params
        ThrowUtils.throwIf(spaceId == null || StrUtil.isBlank(picColor), ErrorCode.PARAMS_ERROR);
        ThrowUtils.throwIf(loginUser == null, ErrorCode.NO_AUTH_ERROR);
        // 2. check space auth
        Space space = spaceApplicationService.getById(spaceId);
        ThrowUtils.throwIf(space == null, ErrorCode.NOT_FOUND_ERROR, "The space does not exist!");
        if (!loginUser.getId().equals(space.getUserId())) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR, "No permission to access this space!");
        }
        // 3. fetch space pictureList(must contain picColor)
        List<Picture> pictureList = pictureRepository.lambdaQuery()
                .eq(Picture::getSpaceId, spaceId)
                .isNotNull(Picture::getPicColor)
                .list();
        // if no pic, return empty list
        if (CollUtil.isEmpty(pictureList)) {
            return Collections.emptyList();
        }
        // picColor -> Color
        Color targetColor = Color.decode(picColor);
        // 4. compute and sort color similarity
        List<Picture> sortedPictures = pictureList.stream()
                .sorted(Comparator.comparingDouble(picture -> {
                    String hexColor = picture.getPicColor();
                    // if no picColor, put in the end
                    if (StrUtil.isBlank(hexColor)) {
                        return Double.MAX_VALUE;
                    }
                    Color pictureColor = Color.decode(hexColor);
                    // big -> similar
                    return -ColorSimilarUtils.calculateSimilarity(targetColor, pictureColor);
                }))
                // top 12
                .limit(12)
                .collect(Collectors.toList());

        // Picture -> PictureVO
        return sortedPictures.stream()
                .map(PictureVO::objToVo)
                .collect(Collectors.toList());
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public void editPictureByBatch(PictureEditByBatchRequest pictureEditByBatchRequest, User loginUser) {
        List<Long> pictureIdList = pictureEditByBatchRequest.getPictureIdList();
        Long spaceId = pictureEditByBatchRequest.getSpaceId();
        String category = pictureEditByBatchRequest.getCategory();
        List<String> tags = pictureEditByBatchRequest.getTags();

        // 1. check params
        ThrowUtils.throwIf(spaceId == null || CollUtil.isEmpty(pictureIdList), ErrorCode.PARAMS_ERROR);
        ThrowUtils.throwIf(loginUser == null, ErrorCode.NO_AUTH_ERROR);
        // 2. check space auth
        Space space = spaceApplicationService.getById(spaceId);
        ThrowUtils.throwIf(space == null, ErrorCode.NOT_FOUND_ERROR, "Space does not exist!");
        if (!loginUser.getId().equals(space.getUserId())) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR, "No permission to access this space!");
        }

        // 3. query picture (only select picture Id and spaceId)
        List<Picture> pictureList = pictureRepository.lambdaQuery()
                .select(Picture::getId, Picture::getSpaceId)
                .eq(Picture::getSpaceId, spaceId)
                .in(Picture::getId, pictureIdList)
                .list();

        if (pictureList.isEmpty()) {
            return;
        }
        // 4. update category, tag
        pictureList.forEach(picture -> {
            if (StrUtil.isNotBlank(category)) {
                picture.setCategory(category);
            }
            if (CollUtil.isNotEmpty(tags)) {
                picture.setTags(JSONUtil.toJsonStr(tags));
            }
        });

        // 5. rename by batch
        String nameRule = pictureEditByBatchRequest.getNameRule();
        fillPictureWithNameRule(pictureList, nameRule);

        // 6. update batch
        boolean result = pictureRepository.updateBatchById(pictureList);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
    }


    /**
     * nameRule e.g., PictureName{index}, {index}PictureName
     *
     * @param pictureList
     * @param nameRule
     */
    private void fillPictureWithNameRule(List<Picture> pictureList, String nameRule) {
        if (CollUtil.isEmpty(pictureList) || StrUtil.isBlank(nameRule)) {
            return;
        }
        long count = 1;
        try {
            for (Picture picture : pictureList) {
                String pictureName = nameRule.replaceAll("\\{index}", String.valueOf(count++));
                picture.setName(pictureName);
            }
        } catch (Exception e) {
            log.error("Name resolution error", e);
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "Name resolution error");
        }
    }

    @Override
    public CreateOutPaintingTaskResponse createPictureOutPaintingTask(CreatePictureOutPaintingTaskRequest createPictureOutPaintingTaskRequest, User loginUser) {
        // fetch picture
        Long pictureId = createPictureOutPaintingTaskRequest.getPictureId();
        Picture picture = Optional.ofNullable(pictureRepository.getById(pictureId))
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_ERROR));
        // check auth
        // ! has been changed to use annotation authentication
        // checkPictureAuth(loginUser, picture);
        // Construct the request parameters
        CreateOutPaintingTaskRequest taskRequest = new CreateOutPaintingTaskRequest();
        CreateOutPaintingTaskRequest.Input input = new CreateOutPaintingTaskRequest.Input();
        input.setImageUrl(picture.getUrl());
        taskRequest.setInput(input);
        BeanUtil.copyProperties(createPictureOutPaintingTaskRequest, taskRequest);
        // create task
        return aliYunAiApi.createOutPaintingTask(taskRequest);
    }
}
