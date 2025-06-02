package com.jin.pixhive_backend.model.dto.user;

import lombok.Data;

import java.io.Serializable;

/**
 * Vip Redeem Request
 */
@Data
public class VipRedeemRequest implements Serializable {

    private static final long serialVersionUID = 8735650154179439661L;

    // redeem code
    private String vipCode;
}