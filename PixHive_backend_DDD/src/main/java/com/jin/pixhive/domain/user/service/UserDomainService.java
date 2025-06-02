package com.jin.pixhive.domain.user.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jin.pixhive.domain.user.entity.User;
import com.jin.pixhive.interfaces.dto.user.UserQueryRequest;
import com.jin.pixhive.interfaces.vo.user.LoginUserVO;
import com.jin.pixhive.interfaces.vo.user.UserVO;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Set;

public interface UserDomainService {
    /**
     * user register
     *
     * @param userAccount   account
     * @param userPassword  password
     * @param checkPassword check_password
     * @return new user id
     */
    long userRegister(String userAccount, String userPassword, String checkPassword);

    /**
     * user login
     *
     * @param userAccount  account
     * @param userPassword password
     * @param request HttpServletRequest
     * @return Logged in User view (Desensitization)
     */
    LoginUserVO userLogin(String userAccount, String userPassword, HttpServletRequest request);

    /**
     * get encrypt password
     * @param userPassword ori password
     * @return encrypt password
     */
    String getEncryptPassword(String userPassword);

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
    public UserVO getUserVO(User user);

    /**
     * for query: get UserVO list
     */
    public List<UserVO> getUserVOList(List<User> userList);

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

    Long addUser(User user);
    Boolean removeById(Long id);
    boolean updateById(User user);
    User getById(long id);
    Page<User> page(Page<User> userPage, QueryWrapper<User> queryWrapper);
    List<User> listByIds(Set<Long> userIdSet);
}
