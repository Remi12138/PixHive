package com.jin.pixhive.domain.picture.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.jin.pixhive.domain.picture.entity.Picture;
import com.jin.pixhive.domain.user.entity.User;
import com.jin.pixhive.infrastructure.api.aliyunai.model.CreateOutPaintingTaskResponse;
import com.jin.pixhive.interfaces.dto.picture.*;
import com.jin.pixhive.interfaces.vo.picture.PictureVO;

import java.util.List;

public interface PictureDomainService {

    /**
     * upload picture
     *
     * @param InputSource file source
     * @param pictureUploadRequest
     * @param loginUser
     * @return
     */
    PictureVO uploadPicture(Object InputSource,
                            PictureUploadRequest pictureUploadRequest,
                            User loginUser);

    /**
     * get Query Wrapper
     * @param pictureQueryRequest
     * @return
     */
    QueryWrapper<Picture> getQueryWrapper(PictureQueryRequest pictureQueryRequest);

    /**
     * id cannot be null;
     * url.length() <= 1024
     * introduction.length() <= 800
     * @param picture
     */
    void validPicture(Picture picture);

    /**
     * Picture Review
     *
     * @param pictureReviewRequest
     * @param loginUser
     */
    void doPictureReview(PictureReviewRequest pictureReviewRequest, User loginUser);

    /**
     * Fill Review
     * @param picture
     * @param loginUser
     */
    void fillReviewParams(Picture picture, User loginUser);

    /**
     * crawler picture
     * @param pictureUploadByBatchRequest
     * @param loginUser
     * @return created picture amount
     */
    Integer uploadPictureByBatch(
            PictureUploadByBatchRequest pictureUploadByBatchRequest,
            User loginUser
    );

    /**
     * check picture Auth
     * @param loginUser
     * @param picture
     */
    void checkPictureAuth(User loginUser, Picture picture);

    void deletePicture(long pictureId, User loginUser);

    void editPicture(Picture picture, User loginUser);

    /**
     * search Picture By Color
     * @param spaceId
     * @param picColor
     * @param loginUser
     * @return
     */
    List<PictureVO> searchPictureByColor(Long spaceId, String picColor, User loginUser);

    void editPictureByBatch(PictureEditByBatchRequest pictureEditByBatchRequest, User loginUser);

    CreateOutPaintingTaskResponse createPictureOutPaintingTask(CreatePictureOutPaintingTaskRequest createPictureOutPaintingTaskRequest, User loginUser);
}
