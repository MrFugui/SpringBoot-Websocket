package com.wangfugui.apprentice.dao.domain;

import lombok.Data;

@Data
public class Role {
    private Long id;
    private String roleName;
    private Integer sort;
    private String roleDesc;
}