package com.rinneohara.ssyx.common.exception;

import com.rinneohara.ssyx.common.result.Result;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @PROJECT_NAME: rinne-ssyx-parent
 * @DESCRIPTION:
 * @USER: Administrator
 * @DATE: 2023/7/31 16:50
 */
//ControllerAdvice用于处理全局的数据，经常与ExceptionHandler来处理全局异常
@ControllerAdvice
public class GlobalException {
    //处理全局异常的方法
    @ExceptionHandler(Exception.class)
    @ResponseBody
    public Result dealGlobalException(Exception e){
        e.printStackTrace();
        return Result.fail(e);
    }

    //处理自定义异常的方法
    @ExceptionHandler(MyException.class)
    @ResponseBody
    public Result dealMyException(MyException e){
        e.printStackTrace();
        return Result.fail(e);
    }

}
