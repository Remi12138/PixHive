package com.jin.pixhive.domain.picture.entity;

import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.util.Date;

import com.jin.pixhive.infrastructure.exception.ErrorCode;
import com.jin.pixhive.infrastructure.exception.ThrowUtils;
import lombok.Data;

/**
 * picture
 * @TableName picture
 */
@TableName(value ="picture")
@Data
public class Picture implements Serializable {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    private String url;

    private String thumbnailUrl;

    private String name;

    private String introduction;

    private String category;

    private String tags;

    private Long picSize;

    private Integer picWidth;

    private Integer picHeight;

    private Double picScale;

    private String picFormat;

    private String picColor;

    private Long userId;

    private Long spaceId;

    private Date createTime;

    private Date editTime;

    private Date updateTime;

    @TableLogic
    private Integer isDelete;

    /**
     * review status：0-reviewing; 1-pass; 2-reject
     */
    private Integer reviewStatus;

    private String reviewMessage;

    private Long reviewerId;

    private Date reviewTime;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

    public void validPicture() {
        // get value from picture
        Long id = this.getId();
        String url = this.getUrl();
        String introduction = this.getIntroduction();
        // check update params
        ThrowUtils.throwIf(ObjUtil.isNull(id), ErrorCode.PARAMS_ERROR, "id cannot be null");
        if (StrUtil.isNotBlank(url)) {
            ThrowUtils.throwIf(url.length() > 1024, ErrorCode.PARAMS_ERROR, "url is too long");
        }
        if (StrUtil.isNotBlank(introduction)) {
            ThrowUtils.throwIf(introduction.length() > 800, ErrorCode.PARAMS_ERROR, "Introduction is too long");
        }
    }
}