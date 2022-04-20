package cn.xz.reggie.dto;

import cn.xz.reggie.entity.Setmeal;
import cn.xz.reggie.entity.SetmealDish;
import lombok.Data;
import java.util.List;

@Data
public class SetmealDto extends Setmeal {

    private List<SetmealDish> setmealDishes;

    private String categoryName;
}
