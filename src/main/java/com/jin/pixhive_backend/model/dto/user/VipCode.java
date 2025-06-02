package com.jin.pixhive_backend.model.dto.user;

import lombok.Data;

@Data
public class VipCode {
    // redeem code
    private String code;

    private boolean hasUsed;
}
