package cn.xz.reggie.controller;


import cn.xz.reggie.common.R;
import cn.xz.reggie.dto.DishDto;
import cn.xz.reggie.entity.Dish;
import cn.xz.reggie.service.DishService;
import cn.xz.reggie.service.impl.DishFlavorServiceImpl;
import cn.xz.reggie.service.impl.DishServiceImpl;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/dish")
public class DishController {

    @Autowired
    private DishService dishService;
//    @Autowired
//    private DishFlavorServiceImpl dishFlavorService;

    /**
     * 保存菜品，用DTO类来接收
     * @return 操作结果是否成功
     */
    @PostMapping
    public R<String> saveDish(@RequestBody DishDto dishDto){
        return dishService.saveDishWithFlavor(dishDto);
    }

    @GetMapping("/page")
    public R<Page<DishDto>> pageDish(int page,int pageSize,String name){
        return dishService.pageDish(page,pageSize,name);
    }

    /**
     * 根据Id查询对应的菜品和菜品分类信息
     */
    @GetMapping("/{id}")
    public R<DishDto> get(@PathVariable Long id){
        return dishService.getDishWithFlavorAndCategory(id);
    }

    /**
     * 修改菜品信息
     */
    @PutMapping
    public R<String> update(@RequestBody DishDto dishDto){
        return dishService.updateDishWithFlavor(dishDto);
    }

    /**
     * 删除菜品
     */
    @DeleteMapping
    public R<String> delete(@RequestParam Long[] ids){
        return dishService.deleteBatch(ids);
    }

    /**
     * 停售/批量停售
     */
    @PostMapping("/status/{code}")
    public R<String> stopSales(@PathVariable int code,@RequestParam Long[] ids){
        return dishService.updateDishStatus(code,ids);
    }

    /**
     * 套餐展示窗口的查询
     */
    @GetMapping("/list")
    public R<List<DishDto>> listDish(Dish dish){
        return dishService.listDish(dish);
    }

}
