package cn.xz.reggie.service;

import cn.xz.reggie.common.R;
import cn.xz.reggie.dto.SetmealDto;
import cn.xz.reggie.entity.Setmeal;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface SetmealService extends IService<Setmeal> {
    R<String> saveSetmeal(SetmealDto setmealDto);


    R<Page> pageList(int page, int pageSize, String name);

    R<String> deleteWithDish(List<Long> ids);

    R<String> stopSell(int code, List<Long> ids);

    R<SetmealDto> getSetmeal(Long id);

    R<String> updateSetmeal(SetmealDto setmealDto);

    R<List<Setmeal>> listSetMeal(Setmeal setmeal);
}
