package com.jin.pixhive_backend.model.dto.picture;

import com.jin.pixhive_backend.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * query picture request
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class PictureQueryRequest extends PageRequest implements Serializable {

    private Long id;

    private String name;

    private String introduction;

    private String category;

    private List<String> tags;

    private Long picSize;

    private Integer picWidth;

    private Integer picHeight;

    private Double picScale;

    private String picFormat;

    /**
     * searchText (name ,introduction,... at the same time)
     */
    private String searchText;

    private Long userId;

    /**
     * review status：0-reviewing; 1-pass; 2-reject
     */
    private Integer reviewStatus;

    private String reviewMessage;

    private Long reviewerId;

    private Date reviewTime;

    private Long spaceId;

    /**
     * query only the data whose spaceId is null -> public
     */
    private boolean nullSpaceId;

    private Date startEditTime;

    private Date endEditTime;

    private static final long serialVersionUID = 1L;
}
