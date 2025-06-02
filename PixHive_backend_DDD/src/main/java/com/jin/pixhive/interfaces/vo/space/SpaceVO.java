package com.jin.pixhive.interfaces.vo.space;

import com.jin.pixhive.interfaces.vo.user.UserVO;
import com.jin.pixhive.domain.space.entity.Space;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * SpaceVO
 * show on frontend
 */
@Data
public class SpaceVO implements Serializable {
    private Long id;

    private String spaceName;

    private Integer spaceLevel;

    /**
     * 0-private, 1-team
     */
    private Integer spaceType;

    private Long maxSize;

    private Long maxCount;

    private Long totalSize;

    private Long totalCount;

    private Long userId;

    private Date createTime;

    private Date editTime;

    private Date updateTime;

    /**
     * create user info
     */
    private UserVO user;

    private List<String> permissionList = new ArrayList<>();

    private static final long serialVersionUID = 1L;

    /**
     * spaceVO -> space
     */
    public static Space voToObj(SpaceVO spaceVO) {
        if (spaceVO == null) {
            return null;
        }
        Space space = new Space();
        BeanUtils.copyProperties(spaceVO, space);
        return space;
    }

    /**
     * space -> spaceVO
     */
    public static SpaceVO objToVo(Space space) {
        if (space == null) {
            return null;
        }
        SpaceVO spaceVO = new SpaceVO();
        BeanUtils.copyProperties(space, spaceVO);
        return spaceVO;
    }
}

