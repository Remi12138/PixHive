package com.jin.pixhive.shared.auth.model;

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
