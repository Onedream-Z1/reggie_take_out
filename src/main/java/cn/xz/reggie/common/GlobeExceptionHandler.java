package cn.xz.reggie.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLIntegrityConstraintViolationException;

/**
 * 全局异常处理器
 */
@ControllerAdvice(annotations = {RestController.class, Service.class, Controller.class})
@Slf4j
@ResponseBody
public class GlobeExceptionHandler {

    @ExceptionHandler(value = SQLIntegrityConstraintViolationException.class)
    public R<String> exceptionHandler(SQLIntegrityConstraintViolationException ex){
        log.error(ex.getMessage());
        if(ex.getMessage().contains("Duplicate entry")){
            String[] s = ex.getMessage().split(" ");
            String message=s[2]+" 已存在!";
            return R.error(message);
        }
        return R.error("未知错误发生了");
    }

    /**
     * 捕获一下我们自己定义的异常
     */
    @ExceptionHandler(value = CustomException.class)
    public R<String> customException(CustomException ex){
        //字节返回在删除菜品，套餐中的异常信息
        return R.error(ex.getMessage());
    }

}
