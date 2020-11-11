package com.cus.gf_work.common;

/**
 * @author zhaojiejun
 * @version V1.0
 * @date 2020/6/7 12:59 上午
 * @description 自定义错误码
 **/
public enum ResultCodeEnum {
    /**
     * 系统异常
     */
    OK("00000","success");

    private final String errorCode;
    private final String errorMsg;

    ResultCodeEnum(String errorCode, String errorMsg) {
        this.errorCode = errorCode;
        this.errorMsg = errorMsg;
    }


    public String getErrorCode() {
        return errorCode;
    }

    public String getErrorMsg() {
        return errorMsg;
    }
}
