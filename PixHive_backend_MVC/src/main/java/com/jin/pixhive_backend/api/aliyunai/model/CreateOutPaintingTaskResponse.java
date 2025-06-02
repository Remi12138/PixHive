package com.jin.pixhive_backend.api.aliyunai.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * Response of create expanded image task
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateOutPaintingTaskResponse {

    private Output output;

    /**
     * output info
     */
    @Data
    public static class Output {

        private String taskId;

        /**
         * taskStatus:
         *    PENDING
         *    RUNNING
         *    SUSPENDED
         *    SUCCEEDED
         *    FAILED
         *    UNKNOWN
         */
        private String taskStatus;
    }

    /**
     * error code
     * if success, will not return "code"
     */
    private String code;

    /**
     * error message。
     * if success, will not return "message"
     */
    private String message;

    /**
     * Request's unique identifier
     * used for detailed trace and problem investigation
     */
    private String requestId;

}
