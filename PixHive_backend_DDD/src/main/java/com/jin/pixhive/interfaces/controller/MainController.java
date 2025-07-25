package com.jin.pixhive.interfaces.controller;

import com.jin.pixhive.infrastructure.common.BaseResponse;
import com.jin.pixhive.infrastructure.common.ResultUtils;
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

