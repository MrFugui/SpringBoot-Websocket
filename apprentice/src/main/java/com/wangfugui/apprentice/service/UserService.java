package com.wangfugui.apprentice.service;

import com.wangfugui.apprentice.common.util.ResponseUtils;
import com.wangfugui.apprentice.dao.domain.User;

/**
 * @author MaSiyi
 * @version 1.0.0 2021/10/23
 * @since JDK 1.8.0
 */
public interface UserService {
    ResponseUtils listUser();

    User getUserInfo(String username);

    ResponseUtils insertUser(User userInfo);

    ResponseUtils updatePwd(String oldPwd, String newPwd);

    User getIdByUserName(String username);

    String getpwdbyname(String name);

    Long getUidbyname(String name);

    String getnamebyid(long id);
}
