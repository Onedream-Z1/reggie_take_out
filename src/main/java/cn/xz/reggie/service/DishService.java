package cn.xz.reggie.service;

import cn.xz.reggie.common.R;
import cn.xz.reggie.dto.DishDto;
import cn.xz.reggie.entity.Dish;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface DishService extends IService<Dish> {
    R<String> saveDishWithFlavor(DishDto dishDto);

    R<Page<DishDto>> pageDish(int page,int pageSize,String name);

    R<DishDto> getDishWithFlavorAndCategory(Long id);

    R<String> updateDishWithFlavor(DishDto dishDto);

    R<String> deleteBatch(Long[] ids);

    R<String> updateDishStatus(int code, Long[] ids);

    R<List<DishDto>> listDish(Dish dish);
}
