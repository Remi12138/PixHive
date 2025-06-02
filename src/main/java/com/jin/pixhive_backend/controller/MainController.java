package com.jin.pixhive_backend.controller;

import com.jin.pixhive_backend.common.BaseResponse;
import com.jin.pixhive_backend.common.ResultUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
public class MainController {

    /**
     * health check
     */
    @GetMapping("/health")
    public BaseResponse<String> health() {
        return ResultUtils.success("health check ok!");
    }
}

