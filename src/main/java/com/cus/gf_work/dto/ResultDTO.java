package com.cus.gf_work.dto;

import com.cus.gf_work.common.ResultCodeEnum;
import lombok.Builder;
import lombok.Data;

/**
 * @author zhaojiejun
 * @date 2020/11/7 11:45 下午
 **/
@Data
@Builder
public class ResultDTO<T> {
    /**
     * 错误编码
     */
    private String errorCode;
    /**
     * 错误信息
     */
    private String errorMsg;
    /**
     * 是否成功
     */
    private Boolean success;
    /**
     * 返回结果
     */
    private T data;

    public static  <T> ResultDTO<T> success(T data){
        return ResultDTO.<T>builder().success(true).data(data).build();
    }

    public static <T> ResultDTO<T> unSuccess(ResultCodeEnum errorCodeEnum){
        return ResultDTO.<T>builder().success(false).errorCode(errorCodeEnum.getErrorCode())
                .errorMsg(errorCodeEnum.getErrorMsg()).build();
    }

    public static <T> ResultDTO<T> unSuccess(String errorCode, String errorMsg){
        return ResultDTO.<T>builder().success(false).errorCode(errorCode)
                .errorMsg(errorMsg).build();
    }

    public static <T> ResultDTO<T> unSuccess(String errorCode, String errorMsg, Throwable throwable){
        return ResultDTO.<T>builder().success(false).errorCode(errorCode)
                .errorMsg(errorMsg).build();
    }

    public static <T> ResultDTO<T> unSuccess(T data , String errorCode, String errorMsg){
        return ResultDTO.<T>builder().data(data).success(false)
                .errorCode(errorCode).errorMsg(errorMsg).build();
    }
}
