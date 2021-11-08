package com.wangfugui.apprentice.dao.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.util.Date;

@Data
public class User {

    @TableId(type = IdType.AUTO)
    private Integer id;
    private String email;
    private String username;
    private String password;
    private String photo;
    private Integer status;
    private Date registerDate;
    private String contactNo;
    private Integer delFlag;
    private Date createTime;
    private Date updateTime;
    private String nickname;
    private Integer roleId;

}