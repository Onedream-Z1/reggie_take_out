package cn.xz.reggie.controller;

import cn.xz.reggie.common.R;
import cn.xz.reggie.dto.SetmealDto;
import cn.xz.reggie.entity.Setmeal;
import cn.xz.reggie.service.impl.SetmealServiceImpl;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 套餐管理
 */
@RestController
@Slf4j
@RequestMapping("/setmeal")
public class SetmealDishController {
    @Autowired
    private SetmealServiceImpl setmealService;

    /**
     * 新增套餐
     * @param setmealDto
     * @return
     */
    @PostMapping
    public R<String> save(@RequestBody SetmealDto setmealDto){
        return setmealService.saveSetmeal(setmealDto);
    }

    /**
     * 页面展示
     */
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String name){
        return setmealService.pageList(page,pageSize,name);
    }

    /**
     * 删除套餐
     */
    @DeleteMapping
    public R<String> delete(@RequestParam List<Long> ids){
//        log.info("ids为{}",ids);
        return setmealService.deleteWithDish(ids);
    }

    /**
     * 停售/启售
     */
    @PostMapping("/status/{code}")
    public R<String> stopSelling(@PathVariable int code,@RequestParam List<Long> ids){
        //log.info("code={},ids={}",code,ids);
        return setmealService.stopSell(code,ids);
    }

    /**
     * 套餐修改回显功能
     */
    @GetMapping("/{id}")
    public R<SetmealDto> get(@PathVariable Long id){
        log.info("id={}",id);
        return setmealService.getSetmeal(id);
    }

    /**
     * 修改套餐
     * @param setmealDto
     * @return
     */
    @PutMapping
    public R<String> update(@RequestBody SetmealDto setmealDto){
        return setmealService.updateSetmeal(setmealDto);
    }

    /**
     * 移动端套餐展示
     */
    @GetMapping("/list")
    public R<List<Setmeal>> list(Setmeal setmeal){
        //log.info("setmeal={}",setmeal.toString());
        return setmealService.listSetMeal(setmeal);
    }


}
