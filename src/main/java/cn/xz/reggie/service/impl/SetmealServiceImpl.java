package cn.xz.reggie.service.impl;

import cn.xz.reggie.common.CustomException;
import cn.xz.reggie.common.R;
import cn.xz.reggie.dto.SetmealDto;
import cn.xz.reggie.entity.Category;
import cn.xz.reggie.entity.Setmeal;
import cn.xz.reggie.entity.SetmealDish;
import cn.xz.reggie.mapper.SetmealMapper;
import cn.xz.reggie.service.CategoryService;
import cn.xz.reggie.service.SetmealDishService;
import cn.xz.reggie.service.SetmealService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal> implements SetmealService {

    @Autowired
    private SetmealDishService setmealDishService;

    @Lazy
    @Autowired
    private CategoryService categoryService;

    /**
     * 新增套餐，以及同时需要保存套餐和菜品的关联关系
     * @param setmealDto
     * @return
     */
    @Override
    @Transactional
    public R<String> saveSetmeal(SetmealDto setmealDto) {
        log.info("setmealDto={}",setmealDto);
        //保存套餐基本信息，操作setmeal，执行insert操作
        save(setmealDto);
        //由于我们的套餐和菜品管理表没有id所以我们需要处理
        Long setmealDtoId = setmealDto.getId();
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        setmealDishes = setmealDishes.stream().map((item)->{
            item.setSetmealId(setmealDtoId);
            return item;
        }).collect(Collectors.toList());
        //保存套餐和菜品的关联信息，操作setmeal_dish，执行insert操作
        setmealDishService.saveBatch(setmealDishes);
        return R.success("新增套餐成功");
    }

    @Override
    public R<Page> pageList(int page, int pageSize, String name) {
        //构造一个分页对象
        //此分页对象用来获取除records之外的信息
        Page<Setmeal> pageInfo = new Page<>(page, pageSize);
        //此分页对象为最后返回的对象
        Page<SetmealDto> pageInfoDto = new Page<>(page, pageSize);

        //构造一个条件构造器
        LambdaQueryWrapper<Setmeal> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.isNotBlank(name), Setmeal::getName,name);
        wrapper.orderByDesc(Setmeal::getUpdateTime);
        page(pageInfo,wrapper);

        //将PageInfo查询出来的信息除了records之外都拷贝进pageInfoDto
        BeanUtils.copyProperties(pageInfo,pageInfoDto,"records");
        //取出records记录
        List<Setmeal> setmealList = pageInfo.getRecords();
        //Setmeal中只有对应的categoryId并没有categoryName，所以需要把categoryName封装进setmealDTO中去
        List<SetmealDto> setmealDtoList = setmealList.stream().map((item) -> {
            //new出SetmealDto保存信息
            SetmealDto setmealDto = new SetmealDto();
            Long categoryId = item.getCategoryId();
            //查询categoryName根据ID
            Category category = categoryService.getById(categoryId);
            if (category != null) {
                String categoryName = category.getName();
                //将category设置
                setmealDto.setCategoryName(categoryName);
            }
            //此时setmealDto还是空的，我们需要对象拷贝
            BeanUtils.copyProperties(item, setmealDto);
            return setmealDto;
        }).collect(Collectors.toList());
        //将处理完成的结果赋值给pageInfoDto
        pageInfoDto.setRecords(setmealDtoList);

        return R.success(pageInfoDto);
    }

    /**
     * 删除套餐和跟套餐的关联关系
     * @param ids
     * @return
     */
    @Override
    public R<String> deleteWithDish(List<Long> ids) {
        //查询套餐售卖状态
        LambdaQueryWrapper<Setmeal> wrapper=new LambdaQueryWrapper<>();
        wrapper.eq(Setmeal::getStatus,1).in(Setmeal::getId,ids);
        long count = count(wrapper);
        //如果不能删除，则抛出异常
        if(count>0L){
            throw new CustomException("套餐正在售卖中，无法删除");
        }
        //可以删除，先删除套餐表中的数据
        removeBatchByIds(ids);
        //删除关联表中的数据
        LambdaQueryWrapper<SetmealDish> dishLambdaQueryWrapper=new LambdaQueryWrapper<>();
        dishLambdaQueryWrapper.in(SetmealDish::getSetmealId,ids);
        setmealDishService.remove(dishLambdaQueryWrapper);

        return R.success("删除套餐成功");
    }

    /**
     * 停售，启售
     * @param code
     * @param ids
     * @return
     */
    @Override
    public R<String> stopSell(int code, List<Long> ids) {
        //更改套餐的售卖状态
        LambdaQueryWrapper<Setmeal> wrapper=new LambdaQueryWrapper<>();
        wrapper.in(Setmeal::getId,ids);
        List<Setmeal> list = list(wrapper);
        list=list.stream().map((item)->{
            item.setStatus(code);
            return item;
        }).collect(Collectors.toList());
        //批量更改
        boolean isUpdate = updateBatchById(list);
        if (!isUpdate) {
            return R.error("更改售卖状态失败");
        }
        return R.success("更改售卖状态成功");
    }

    /**
     * 修改页面的套餐回显
     * @param id
     * @return
     */
    @Override
    public R<SetmealDto> getSetmeal(Long id) {
        //数理化一个SetmealDto
        SetmealDto setmealDto = new SetmealDto();
        //查询套餐基础信息
        Setmeal setmeal = getById(id);

        //根据Setmeal中的套餐分类Id查询套餐分类信息中的套餐名
        Long categoryId = setmeal.getCategoryId();
        Category category = categoryService.getById(categoryId);
        String categoryName = category.getName();

        //根据SetmealID查询套餐和菜品的关系表
        LambdaQueryWrapper<SetmealDish> wrapper=new LambdaQueryWrapper<>();
        wrapper.eq(SetmealDish::getSetmealId,id);
        List<SetmealDish> setmealDishes = setmealDishService.list(wrapper);

        //为setmealDto中的属性赋值
        BeanUtils.copyProperties(setmeal,setmealDto);
        setmealDto.setCategoryName(categoryName);
        setmealDto.setSetmealDishes(setmealDishes);

        return R.success(setmealDto);
    }

    @Override
    public R<String> updateSetmeal(SetmealDto setmealDto) {
        //log.info("setmealDto={}",setmealDto.toString());
        //修改基础信息
        updateById(setmealDto);
        //修改套餐和菜品的关系表
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        setmealDishService.updateBatchById(setmealDishes);
        return R.success("修改套餐信息成功");
    }

    /**
     * 移动端套餐展示
     * @param setmeal
     * @return
     */
    @Override
    public R<List<Setmeal>> listSetMeal(Setmeal setmeal) {
        LambdaQueryWrapper<Setmeal> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(setmeal.getCategoryId()!=null, Setmeal::getCategoryId,setmeal.getCategoryId())
                .eq(setmeal.getStatus()!=null,Setmeal::getStatus,setmeal.getStatus())
                .orderByDesc(Setmeal::getUpdateTime);
        List<Setmeal> setmealList = this.list(wrapper);
        return R.success(setmealList);
    }


}
