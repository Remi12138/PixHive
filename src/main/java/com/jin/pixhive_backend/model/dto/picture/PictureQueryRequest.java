package com.jin.pixhive_backend.model.dto.picture;

import com.jin.pixhive_backend.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
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

    private static final long serialVersionUID = 1L;
}
