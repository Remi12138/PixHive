package com.jin.pixhive_backend.manage.auth;

import cn.dev33.satoken.stp.StpLogic;
import cn.dev33.satoken.stp.StpUtil;
import org.springframework.stereotype.Component;

/**
 * StpLogic facade class:
 * manages all the StpLogic account systems in the project
 * adding @Component: ensure the static properties DEFAULT and SPACE are initialized
 */
@Component
public class StpKit {
    // Here only use Sa-Token to manage [Space](team) auth
    // [User] role still use previous code and table to auth

    public static final String SPACE_TYPE = "space";

    /**
     * Default native session object
     */
    public static final StpLogic DEFAULT = StpUtil.stpLogic;

    /**
     * Space session
     * manages the [login] and [permission authentication] in the Space table
     */
    public static final StpLogic SPACE = new StpLogic(SPACE_TYPE);
}
