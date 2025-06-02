package com.jin.pixhive.interfaces.vo.user;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * Logged in User view (Desensitization)
 * return to frontend
 */
@Data
public class LoginUserVO implements Serializable {
    private Long id;

    private String userAccount;

    private String userName;

    private String userAvatar;

    private String userProfile;

    /**
     * role: user/admin
     */
    private String userRole;

    private Date editTime;

    private Date createTime;

    private Date updateTime;

    private static final long serialVersionUID = 1L;
}