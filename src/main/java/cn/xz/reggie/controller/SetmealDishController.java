package cn.xz.reggie.controller;

import cn.xz.reggie.common.R;
import cn.xz.reggie.dto.SetmealDto;
import cn.xz.reggie.entity.Setmeal;
import cn.xz.reggie.service.impl.SetmealServiceImpl;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
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
    //将一条或多条数据从缓存中删除
    @CacheEvict(value = "setmealCache",allEntries = true)
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
    //将一条或多条数据从缓存中删除
    @CacheEvict(value = "setmealCache",allEntries = true)
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
    //@Cacheable注解：Spring cache的注解 -- 在方法执行前先查看缓存中是否有数据，如果有数据，则直接返回缓存的数据；
    //               若没有数据，调用方法并将此方法的返回值存放到缓存中
    //               注意：Cacheable中缓存的数据(对象),必须要实现序列化接口
    @Cacheable(value = "setmealCache",key = "#setmeal.categoryId+':1'")
    public R<List<Setmeal>> list(Setmeal setmeal){
        //log.info("setmeal={}",setmeal.toString());
        return setmealService.listSetMeal(setmeal);
    }


}
