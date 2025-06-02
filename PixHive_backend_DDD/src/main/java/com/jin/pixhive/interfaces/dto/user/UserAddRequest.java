package com.jin.pixhive.interfaces.dto.user;

import lombok.Data;

import java.io.Serializable;

/**
 * admin can add user
 */
@Data
public class UserAddRequest implements Serializable {

    private String userName;

    private String userAccount;

    private String userAvatar;

    private String userProfile;

    private String userRole;

    private static final long serialVersionUID = 1L;
}


