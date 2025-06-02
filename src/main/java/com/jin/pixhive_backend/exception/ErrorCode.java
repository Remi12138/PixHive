package com.jin.pixhive_backend.exception;

import lombok.Getter;

@Getter
public enum ErrorCode {

    SUCCESS(0, "ok"),
    PARAMS_ERROR(40000, "Request with Error Params"),
    NOT_LOGIN_ERROR(40100, "Not Login"),
    NO_AUTH_ERROR(40101, "No Authentication"),
    NOT_FOUND_ERROR(40400, "Not Found"),
    FORBIDDEN_ERROR(40300, "Access Forbidden"),
    SYSTEM_ERROR(50000, "System Internal Exception"),
    OPERATION_ERROR(50001, "Operation Error");

    /**
     * error code
     */
    private final int code;

    /**
     * message
     */
    private final String message;

    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

}
