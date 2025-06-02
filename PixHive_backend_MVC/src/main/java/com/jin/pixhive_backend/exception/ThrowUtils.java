package com.jin.pixhive_backend.exception;

/**
 * exception utils
 */
public class ThrowUtils {

    /**
     * If the condition is true, throw an exception
     *
     * @param condition        condition
     * @param runtimeException exception
     */
    public static void throwIf(boolean condition, RuntimeException runtimeException) {
        if (condition) {
            throw runtimeException;
        }
    }

    /**
     * If the condition is true, throw an exception
     *
     * @param condition condition
     * @param errorCode error code
     */
    public static void throwIf(boolean condition, ErrorCode errorCode) {
        throwIf(condition, new BusinessException(errorCode));
    }

    /**
     * If the condition is true, throw an exception
     *
     * @param condition condition
     * @param errorCode error code
     * @param message   error message
     */
    public static void throwIf(boolean condition, ErrorCode errorCode, String message) {
        throwIf(condition, new BusinessException(errorCode, message));
    }
}

