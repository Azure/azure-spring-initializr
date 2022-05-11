package com.azure.spring.initializr.web.connector;

public enum ResultCode {

    CODE_SUCCESS("200","Push to github repo successfully."),
    INVALID_PARAM("201", "Param is invalid."),
    CODE_REPO_ALREADY_EXISTS("202", "Repo already exists"),
    ;

    private String code;
    private String msg;

    ResultCode(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
