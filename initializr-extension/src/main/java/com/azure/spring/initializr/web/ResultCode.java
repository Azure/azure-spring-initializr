package com.azure.spring.initializr.web;

public enum ResultCode {

    CODE_SUCCESS("0","OK."),
    INVALID_PARAM("201", "Param is invalid."),
    CODE_REPO_ALREADY_EXISTS("202", "Repo already exists."),
    ACCESSTOKEN_EMPTY("203","Can not get access token."),
    OAUTHAPP_EXCEPTION("204", "There is an OAuthAppException."),
    CODE_404("404","There was an unexpected error (type=Not Found, status=404).."),
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
