package com.rinneohara.ssyx.common.exception;

import com.rinneohara.ssyx.common.result.ResultCodeEnum;
import io.swagger.models.auth.In;
import lombok.ToString;

/**
 * @PROJECT_NAME: rinne-ssyx-parent
 * @DESCRIPTION:
 * @USER: Administrator
 * @DATE: 2023/7/31 16:53
 */
@ToString
public class MyException extends RuntimeException{

    private Integer code;

    public MyException(String message, Integer code){
        super(message);
        this.code=code;
    }

    public MyException(ResultCodeEnum resultCodeEnum) {
        super(resultCodeEnum.getMessage());
        this.code = resultCodeEnum.getCode();
    }
}
