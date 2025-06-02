package com.jin.pixhive_backend.model.dto.user;

import lombok.Data;

import java.io.Serializable;

/**
 * self update name/avatar/password
 */
@Data
public class UserProfileUpdateRequest implements Serializable {

    private String userName;

    private String userAvatar;

    private String userPassword;

    private static final long serialVersionUID = 1L;
}
