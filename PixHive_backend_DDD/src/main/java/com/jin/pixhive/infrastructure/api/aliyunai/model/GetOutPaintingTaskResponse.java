package com.jin.pixhive.infrastructure.api.aliyunai.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * Response of query expanded image task
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GetOutPaintingTaskResponse {

    private String requestId;

    private Output output;

    /**
     * Output info
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

        /**
         * submitTime
         * YYYY-MM-DD HH:mm:ss.SSS
         */
        private String submitTime;

        /**
         * scheduledTime
         * YYYY-MM-DD HH:mm:ss.SSS
         */
        private String scheduledTime;

        /**
         * endTime
         * YYYY-MM-DD HH:mm:ss.SSS
         */
        private String endTime;

        /**
         * only success, return outputImageUrl
         */
        private String outputImageUrl;

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
         * Statistical info of the task
         */
        private TaskMetrics taskMetrics;
    }

    /**
     * Statistical info of the task
     */
    @Data
    public static class TaskMetrics {

        /**
         * total task count
         */
        private Integer total;

        /**
         * success task count
         */
        private Integer succeeded;

        /**
         * failed task count
         */
        private Integer failed;
    }
}
