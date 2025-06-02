package com.jin.pixhive.infrastructure.common;

import lombok.Data;

import java.io.Serializable;

/**
 * Generic delete request class
 */
@Data
public class DeleteRequest implements Serializable {

    private Long id;

    private static final long serialVersionUID = 1L;
}
