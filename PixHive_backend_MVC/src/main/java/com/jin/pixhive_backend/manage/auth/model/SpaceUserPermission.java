package com.jin.pixhive_backend.manage.auth.model;

import lombok.Data;

import java.io.Serializable;

@Data
public class SpaceUserPermission implements Serializable {

    private String key;

    private String name;

    private String description;

    private static final long serialVersionUID = 1L;

}
