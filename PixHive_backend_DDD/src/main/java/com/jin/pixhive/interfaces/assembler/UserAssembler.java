package com.jin.pixhive.interfaces.assembler;

import com.jin.pixhive.domain.user.entity.User;
import com.jin.pixhive.interfaces.dto.user.UserAddRequest;
import com.jin.pixhive.interfaces.dto.user.UserUpdateRequest;
import org.springframework.beans.BeanUtils;

public class UserAssembler {

    public static User toUserEntity(UserAddRequest request) {
        User user = new User();
        BeanUtils.copyProperties(request, user);
        return user;
    }

    public static User toUserEntity(UserUpdateRequest request) {
        User user = new User();
        BeanUtils.copyProperties(request, user);
        return user;
    }
}

