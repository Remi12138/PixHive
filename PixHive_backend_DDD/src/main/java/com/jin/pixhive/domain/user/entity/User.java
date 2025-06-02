package com.jin.pixhive.domain.user.entity;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.util.Date;

import com.jin.pixhive.domain.user.valueobject.UserRoleEnum;
import com.jin.pixhive.infrastructure.exception.BusinessException;
import com.jin.pixhive.infrastructure.exception.ErrorCode;
import lombok.Data;

/**
 * user
 * @TableName user
 */
@TableName(value ="user")
@Data
public class User implements Serializable {
    /**
     * id
     * type = IdType.AUTO: 1,2,3,... continuous generation, accessible to crawlers
     * type = IdType.AUTO: longer ID, Snowflake ID
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    private String userAccount;

    private String userPassword;

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

    @TableLogic
    private Integer isDelete;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

    /**
     * valid User Register
     */
    public static void validUserRegister(String userAccount, String userPassword, String checkPassword) {
        if (StrUtil.hasBlank(userAccount, userPassword, checkPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "Parameter is blank");
        }
        if (userAccount.length() < 4) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "User account length is less than 4");
        }
        if (userPassword.length() < 8 || checkPassword.length() < 8) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "User password length is less than 8");
        }
        if (!userPassword.equals(checkPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "Password and check password are different");
        }
    }

    /**
     * valid User Login
     */
    public static void validUserLogin(String userAccount, String userPassword) {
        if (StrUtil.hasBlank(userAccount, userPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "Parameter is blank");
        }
        if (userAccount.length() < 4) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "Account error");
        }
        if (userPassword.length() < 8) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "Password error");
        }
    }

    /**
     * isAdmin
     */
    public boolean isAdmin() {
        return UserRoleEnum.ADMIN.getValue().equals(this.getUserRole());
    }

}