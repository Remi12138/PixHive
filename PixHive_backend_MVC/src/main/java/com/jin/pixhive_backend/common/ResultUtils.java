package com.jin.pixhive_backend.common;

import com.jin.pixhive_backend.exception.ErrorCode;

/**
 * result utils
 */
public class ResultUtils {

    /**
     * success
     *
     * @param data data
     * @param <T>  data type
     * @return BaseResponse
     */
    public static <T> BaseResponse<T> success(T data) {
        return new BaseResponse<>(0, data, "ok");
    }

    /**
     * fail
     *
     * @param errorCode error code
     * @return BaseResponse
     */
    public static BaseResponse<?> error(ErrorCode errorCode) {
        return new BaseResponse<>(errorCode);
    }

    /**
     * fail
     *
     * @param code    error code
     * @param message error message
     * @return BaseResponse
     */
    public static BaseResponse<?> error(int code, String message) {
        return new BaseResponse<>(code, null, message);
    }

    /**
     * fail
     *
     * @param errorCode ErrorCode
     * @param message error message
     * @return BaseResponse
     */
    public static BaseResponse<?> error(ErrorCode errorCode, String message) {
        return new BaseResponse<>(errorCode.getCode(), null, message);
    }
}

