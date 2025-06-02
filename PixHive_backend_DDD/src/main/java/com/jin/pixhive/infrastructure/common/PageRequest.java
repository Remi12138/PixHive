package com.jin.pixhive.infrastructure.common;

import lombok.Data;

/**
 * Generic page request class
 */
@Data
public class PageRequest {

    /**
     * current page
     */
    private int current = 1;

    /**
     * page size
     */
    private int pageSize = 10;

    /**
     * sort field
     */
    private String sortField;

    /**
     * sort order（default: descend）
     */
    private String sortOrder = "descend";
}

