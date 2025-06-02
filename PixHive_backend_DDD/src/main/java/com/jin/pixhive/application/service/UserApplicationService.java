package com.jin.pixhive.application.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jin.pixhive.infrastructure.common.DeleteRequest;
import com.jin.pixhive.interfaces.dto.user.UserLoginRequest;
import com.jin.pixhive.interfaces.dto.user.UserQueryRequest;
import com.jin.pixhive.domain.user.entity.User;
import com.jin.pixhive.interfaces.dto.user.UserRegisterRequest;
import com.jin.pixhive.interfaces.vo.user.LoginUserVO;
import com.jin.pixhive.interfaces.vo.user.UserVO;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Set;

public interface UserApplicationService {
    /**
     * user register
     */
    long userRegister(UserRegisterRequest userRegisterRequest);

    /**
     * user login
     */
    LoginUserVO userLogin(UserLoginRequest userLoginRequest, HttpServletRequest request);

    /**
     * Get user information after desensitization
     * @param user original user
     * @return desensitized user
     */
    LoginUserVO getLoginUserVO(User user);

    /**
     * get current login user
     *
     * @param request
     * @return User
     */
    User getLoginUser(HttpServletRequest request);

    /**
     * for query: get UserVO
     */
    UserVO getUserVO(User user);

    /**
     * for query: get UserVO list
     */
    List<UserVO> getUserVOList(List<User> userList);

    /**
     * logout
     *
     * @param request
     * @return
     */
    boolean userLogout(HttpServletRequest request);

    /**
     * get query condition
     * @param userQueryRequest
     * @return
     */
    QueryWrapper<User> getQueryWrapper(UserQueryRequest userQueryRequest);

    long addUser(User user);
    User getUserById(long id);
    UserVO getUserVOById(long id);
    boolean deleteUser(DeleteRequest deleteRequest);
    void updateUser(User user);
    Page<UserVO> listUserVOByPage(UserQueryRequest userQueryRequest);
    List<User> listByIds(Set<Long> userIdSet);
    String getEncryptPassword(String userPassword);
}
