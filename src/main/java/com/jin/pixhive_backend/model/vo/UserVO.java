package com.jin.pixhive_backend.model.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * User view (Desensitization)
 * For query display
 */
@Data
public class UserVO implements Serializable {
    private Long id;

    private String userAccount;

    private String userName;

    private String userAvatar;

    private String userProfile;

    private String userRole;

    private Date createTime;

    private static final long serialVersionUID = 1L;
}
