package cn.xz.reggie.common;

import cn.xz.reggie.entity.Employee;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * Mybatis Plus提供了公共字段填充，也就是在插入或者更新的时候未指定字段赋予指定的值，使用它的好处就是可以统一字段进行处理，从而避免的代码重复
 * 其实现逐步为：
 * 1、在实体类属性上加入@TableField注解，指定自动填充策略
 * 2、按照框架的编写要求编写元数据处理器，在此类中为公共字段统一进行赋值，因此我们需要是实现MetaObjectHandler接口，并实现其中的两个方法
 */

@Component
@Slf4j
public class MyMateObjectHandler implements MetaObjectHandler {
    @Override
    public void insertFill(MetaObject metaObject) {

//        Employee empObj = (Employee) metaObject.getOriginalObject();
        //从线程池中当前线程所对应的线程局部变量的值（用户id)
        metaObject.setValue("createTime",LocalDateTime.now());
        metaObject.setValue("updateTime",LocalDateTime.now());
        metaObject.setValue("createUser",BaseThreadLocalContext.getCurrentId());
        metaObject.setValue("updateUser",BaseThreadLocalContext.getCurrentId());
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        log.info("metaObject值为:"+metaObject.toString());
        metaObject.setValue("updateTime",LocalDateTime.now());
        metaObject.setValue("updateUser",BaseThreadLocalContext.getCurrentId());
    }
}
