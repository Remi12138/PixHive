package com.jin.pixhive.interfaces.vo.space;

import com.jin.pixhive.interfaces.vo.user.UserVO;
import com.jin.pixhive.domain.space.entity.SpaceUser;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;
import java.util.Date;

/**
 * SpaceUserVO
 * show on frontend
 */
@Data
public class SpaceUserVO implements Serializable {

    private Long id;

    private Long spaceId;

    private Long userId;

    /**
     * spaceRole: viewer/editor/admin
     */
    private String spaceRole;

    private Date createTime;

    private Date updateTime;

    private UserVO user;

    private SpaceVO space;

    private static final long serialVersionUID = 1L;

    public static SpaceUser voToObj(SpaceUserVO spaceUserVO) {
        if (spaceUserVO == null) {
            return null;
        }
        SpaceUser spaceUser = new SpaceUser();
        BeanUtils.copyProperties(spaceUserVO, spaceUser);
        return spaceUser;
    }


    public static SpaceUserVO objToVo(SpaceUser spaceUser) {
        if (spaceUser == null) {
            return null;
        }
        SpaceUserVO spaceUserVO = new SpaceUserVO();
        BeanUtils.copyProperties(spaceUser, spaceUserVO);
        return spaceUserVO;
    }
}
