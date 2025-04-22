package com.jin.pixhive_backend.manage.auth.model;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class SpaceUserAuthConfig implements Serializable {

    /**
     * permission
     */
    private List<SpaceUserPermission> permissions;

    /**
     * role
     */
    private List<SpaceUserRole> roles;

    private static final long serialVersionUID = 1L;
}
