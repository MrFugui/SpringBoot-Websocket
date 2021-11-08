package com.wangfugui.apprentice.common.constant;


public enum CodeEnums {
    SUCCESS_CODE("0000", "操作成功"),
    ERROR_CODE("0500", "异常操作失败，不提示用户"),
    NOT_LOGIN("0405", "未登录"),
    NO_RESULT("0404", "找不到接口"),
    NO_USER("0101", "用户不存在"),
    PASSWORD_ERROR("0102", "密码错误"),
    AUTH_ERROR("0103", "认证失败"),

    NO_CORRECT("0401", "请求不符，提示用户"),
    NOT_SAFE("0402", "您的账户存在安全隐患，请重新登陆"),
    ERROR_MSG("0410", "异常失败，提示用户"),
    SUCCESS_CODE_NO_DATA("0001", "未查询到数据"),
    PARAMETER_NOT_EMPTY("0311", "参数不能为空"),
    IS_LOCK("0120", "用户已被锁定，请10分钟后再重试！");

    private String code;
    private String msg;

    private CodeEnums(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public String getCode() {
        return this.code;
    }

    public String getMsg() {
        return this.msg;
    }
}
