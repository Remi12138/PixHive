package com.jin.pixhive_backend.model.dto.user;

import lombok.Data;

import java.io.Serializable;

/**
 * admin can update user
 */
@Data
public class UserUpdateRequest implements Serializable {

    private Long id;

    private String userName;

    private String userAvatar;

    private String userProfile;

    private String userRole;

    private static final long serialVersionUID = 1L;
}
