package cn.xz.reggie.common;

/**
 * 自定义一个异常类
 */
public class CustomException extends RuntimeException {
    public CustomException(String message) {
        super(message);
    }
}
