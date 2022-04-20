package cn.xz.reggie.common;
/**
 *  关于ThreadLocal的定义？
 *  ThreadLocal并不是一个线程，而是Thread的一个局部变量。当使用ThreadLocal维护变量时，ThreadLocal为每个使用该变量的线程提供独立的变量副本，
 *  所以每一个线程都可以独立地改变自己的副本，而不会影响其它线程所对应的副本。ThreadLocal为每个线程提供单独一份存储空间，具有线程隔离的效果，
 *  只有在线程内才能获取到对应的值，线程外则不能访问。
 *
 *  常用的方法就是get和set
 *
 *  使用步骤：
 *  我们可以在LoginCheckFilter的doFilter方法中获取当前登录用户id，并调用ThreadLocal的set方法来设置当前线程的线程局部变量的值（用户id)，
 *  然后在MyMetaObjectHandler的updateFill方法中调用ThreadLocal的get方法来获得当前线程所对应的线程局部变量的值（用户id)。
 *
 */

/**
 * 基于ThreadLocal封装工具类，用户保存和获取当前用户ID
 */
public class BaseThreadLocalContext {

    private static ThreadLocal<Long> threadLocal=new ThreadLocal<>();

    public static void setCurrentId(Long id){
        //为线程池设置值
        threadLocal.set(id);
    }

    public static Long getCurrentId(){
        //获取线程池的值
        return threadLocal.get();
    }

}
