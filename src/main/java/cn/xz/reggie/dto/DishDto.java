package cn.xz.reggie.dto;

import cn.xz.reggie.entity.Dish;
import cn.xz.reggie.entity.DishFlavor;
import lombok.Data;
import java.util.ArrayList;
import java.util.List;

/**
 * DTO的全称为数据传输对象，如果我们的实体类不能与我们的请求参数所对应，我们就可以封装一个用于接收这样的实体的类
 * DTO一般一般用于展示层与服务层之间的数据传输
 * 此类继承了Dish，所以Dish有的此类也有，而且还封装了一个DishFlavor的List集合，用于接收我们口味选择
 * categoryName与copies没有用到，暂时不用管
 */
@Data
public class DishDto extends Dish {

    private List<DishFlavor> flavors = new ArrayList<>();

    private String categoryName;

    private Integer copies;
}
