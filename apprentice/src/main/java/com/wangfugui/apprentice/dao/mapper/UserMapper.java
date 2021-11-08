package com.wangfugui.apprentice.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wangfugui.apprentice.dao.domain.User;
import org.springframework.stereotype.Repository;

@Repository
public interface UserMapper extends BaseMapper<User> {

    User getIdByUserName(String username);

    User getpwdbyname(String name);

    User getnamebyid(long id);
}