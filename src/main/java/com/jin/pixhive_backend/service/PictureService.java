package com.jin.pixhive_backend.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.jin.pixhive_backend.model.dto.picture.PictureQueryRequest;
import com.jin.pixhive_backend.model.dto.picture.PictureReviewRequest;
import com.jin.pixhive_backend.model.dto.picture.PictureUploadByBatchRequest;
import com.jin.pixhive_backend.model.dto.picture.PictureUploadRequest;
import com.jin.pixhive_backend.model.entity.Picture;
import com.jin.pixhive_backend.model.entity.User;
import com.jin.pixhive_backend.model.vo.PictureVO;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;

/**
* @author xianjinghuang
* @description database operation service for table [picture]
* @createDate 2025-03-16 17:37:00
*/
public interface PictureService extends IService<Picture> {

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
     * (single) desensitization: picture -> VO
     * @param picture
     * @param request
     * @return
     */
    PictureVO getPictureVO(Picture picture, HttpServletRequest request);

    /**
     * (page) desensitization: picture -> VO
     * @param picturePage
     * @param request
     * @return
     */
    Page<PictureVO> getPictureVOPage(Page<Picture> picturePage, HttpServletRequest request);

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
}
