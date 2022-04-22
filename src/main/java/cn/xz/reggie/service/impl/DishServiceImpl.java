package cn.xz.reggie.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.json.JSONUtil;
import cn.xz.reggie.common.R;
import cn.xz.reggie.dto.DishDto;
import cn.xz.reggie.entity.Category;
import cn.xz.reggie.entity.Dish;
import cn.xz.reggie.entity.DishFlavor;
import cn.xz.reggie.mapper.DishMapper;
import cn.xz.reggie.service.CategoryService;
import cn.xz.reggie.service.DishFlavorService;
import cn.xz.reggie.service.DishService;
import com.alibaba.druid.support.json.JSONUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@Slf4j
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {


    @Autowired
    private DishFlavorService dishFlavorService;

    @Lazy
    @Autowired
    private CategoryService categoryService;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;


    @Override
    @Transactional
    public R<String> saveDishWithFlavor(DishDto dishDto) {
        log.info("dishDTO={}",dishDto.toString());

        //保存菜品
        save(dishDto);
        Long dishID = dishDto.getId();

        //遍历口味表List集合，为里面的dishId赋值
        List<DishFlavor> flavors = dishDto.getFlavors();
        flavors=flavors.stream().map((item)->{
            item.setDishId(dishID);
            return item;
        }).collect(Collectors.toList());
        //保存口味表，注意保存口味表中有我们对应的dishID别忘记即赋值，不然白忙活一场，找不到数据
        dishFlavorService.saveBatch(flavors);

        return R.success("新增菜品成功");
    }

    @Override
    public R<Page<DishDto>> pageDish(int page,int pageSize,String name) {
        //分页构造器
        Page<Dish> pageInfo = new Page<>(page,pageSize);
        //添加我们的另一个分页条件，查询的是我们的DishDTO对象，因为这个DTO类中有我们的categoryName字段，可以将我们的菜品分类查询出来
        Page<DishDto> pageDishDto = new Page<>(page,pageSize);
        //构造条件构造器
        LambdaQueryWrapper<Dish> wrapper=new LambdaQueryWrapper<>();

        //添加分页条件
        wrapper.like(StringUtils.isNotBlank(name),Dish::getName,name)
                .orderByAsc(Dish::getSort)
                .orderByDesc(Dish::getPrice);
        page(pageInfo,wrapper);
        //对象拷贝，将pageInfo中的数据拷贝开pageDishDto。除了page中records之外，其他的都拷贝
        //因为我们需要处理一下record字段中的categoryName字段
        BeanUtils.copyProperties(pageInfo,pageDishDto,"records");
        //取出PageInfo中的records字段，用stream流方式遍历
        List<Dish> records = pageInfo.getRecords();
        List<DishDto> listDto= records.stream().map((items)->{
            DishDto dishDto = new DishDto();
            //因为dishDto是我们自己new出来来的因此除了有一个categoryName之外，其他的值都没有
            BeanUtils.copyProperties(items,dishDto);
            //获得categoryId让我们去查询分类表中的categoryName字段
            Long categoryId = items.getCategoryId();
            Category category = categoryService.getById(categoryId);
            if(category!=null){
                String categoryName = category.getName();
                dishDto.setCategoryName(categoryName);
            }
            return dishDto;
        }).collect(Collectors.toList());

        //将处理后的List<pageDishDto装进去>
        pageDishDto.setRecords(listDto);


        return R.success(pageDishDto);
    }

    @Override
    public R<DishDto> getDishWithFlavorAndCategory(Long id) {
        //1、先将dish中的对应的菜品信息查询出来
        Dish dish = getById(id);
        //2、构造一个DishDto对象，用来封装查询到的信息
        DishDto dishDto = new DishDto();
        //3、将Dish中的信息用对象拷贝到DishDto中
        BeanUtils.copyProperties(dish,dishDto);
        //4、根据dish菜品Id查询口味表，并赋值到DishDto中去
        LambdaQueryWrapper<DishFlavor> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DishFlavor::getDishId,id);
        List<DishFlavor> dishFlavorList = dishFlavorService.list(wrapper);
        //5、将DIshDto返回
        dishDto.setFlavors(dishFlavorList);
        return R.success(dishDto);
    }

    /**
     * 跟新菜品信息
     * @param dishDto
     * @return
     */
    @Override
    @Transactional
    public R<String> updateDishWithFlavor(DishDto dishDto) {
        //更新Dish的基本信息
        updateById(dishDto);
        Long dishId = dishDto.getId();
        //从Dish中获取flavors的信息
        List<DishFlavor> flavors = dishDto.getFlavors();
        //遍历flavors中的信息设置其中的dish_id
        flavors=flavors.stream().map((item)->{
            item.setDishId(dishId);
            return item;
        }).collect(Collectors.toList());
        //删除DishFlavor中对应的菜品信息
        LambdaQueryWrapper<DishFlavor> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DishFlavor::getDishId,dishId);
        dishFlavorService.remove(wrapper);
        //将上面处理的flavors插入到DishFlavor
        dishFlavorService.saveBatch(flavors);

        //根据categoryId来清理Redis缓存
        //获取categoryId
        String key="dish:"+dishDto.getCategoryId()+":"+dishDto.getStatus();
        stringRedisTemplate.delete(key);


        return R.success("修成菜品信息成功");
    }

    /**
     * 删除菜品信息
     * @param ids
     * @return
     */
    @Override
    @Transactional
    public R<String> deleteBatch(Long[] ids) {
        //删除菜品基础信息
        List<Long> dishIds = Arrays.stream(ids).collect(Collectors.toList());
        for (Long id : ids) {
            Dish dish = getById(id);
            if (dish.getStatus()==1) {
                return R.error("请停售后再删除");
            }
        }
        boolean b = removeBatchByIds(dishIds);
        //删除口味信息
        boolean b1 = dishFlavorService.removeBatchByIds(dishIds);
        return R.success("删除成功");
    }

    /**
     * 批量停售/单个停售
     * @param code
     * @param ids
     * @return
     */
    @Override
    @Transactional
    public R<String> updateDishStatus(int code, Long[] ids) {
        log.info("code={},ids={}",code,ids);
        List<Long> dishIds = Arrays.stream(ids).collect(Collectors.toList());
        List<Dish> dishList = listByIds(dishIds);
        dishList=dishList.stream().map((item)->{
            item.setStatus(code);
            return item;
        }).collect(Collectors.toList());
        boolean b = updateBatchById(dishList);
        return R.success("修改状态成功");
    }

//    @Override
//    public R<List<Dish>> listDish(Dish dish) {
//        //构造条件器
//        LambdaQueryWrapper<Dish> wrapper=new LambdaQueryWrapper<>();
//        //执行查询条件  停售的不查询出来  添加两个排序条件
//        wrapper.eq(dish.getCategoryId()!=null,Dish::getCategoryId,dish.getCategoryId());
//        wrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);
//        wrapper.eq(Dish::getStatus,1);
//        //执行查询
//        List<Dish> dishList = list(wrapper);
//        //返回查询结果
//        return R.success(dishList);
//    }

    @Override
    public R<List<DishDto>> listDish(Dish dish) {
        List<DishDto> dishDtoList=null;
        //设计一个key
        String key="dish:"+dish.getCategoryId()+":"+dish.getStatus();
        //先从Redis获取缓存数据
        String dishJSon = stringRedisTemplate.opsForValue().get(key);
        dishDtoList = JSONUtil.toList(dishJSon, DishDto.class);
        //判断redis中是否有数据
        if (dishDtoList.size()>0){
            //有则返回
            return R.success(dishDtoList);
        }
        //没有则继续向下

        //构造条件器
        LambdaQueryWrapper<Dish> wrapper=new LambdaQueryWrapper<>();
        //执行查询条件  停售的不查询出来  添加两个排序条件
        wrapper.eq(dish.getCategoryId()!=null,Dish::getCategoryId,dish.getCategoryId());
        wrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);
        wrapper.eq(Dish::getStatus,1);
        //执行查询
        List<Dish> dishList = list(wrapper);

        dishDtoList= dishList.stream().map((items)->{
            DishDto dishDto = new DishDto();
            //因为dishDto是我们自己new出来来的因此除了有一个categoryName之外，其他的值都没有
            BeanUtils.copyProperties(items,dishDto);
            //获得categoryId让我们去查询分类表中的categoryName字段
            Long categoryId = items.getCategoryId();
            Category category = categoryService.getById(categoryId);
            if(category!=null){
                String categoryName = category.getName();
                dishDto.setCategoryName(categoryName);
            }

            Long dishID = items.getId();
            LambdaQueryWrapper<DishFlavor> flavorWrapper=new LambdaQueryWrapper<>();
            flavorWrapper.eq(DishFlavor::getDishId,dishID);
            List<DishFlavor> flavorList = dishFlavorService.list(flavorWrapper);
            dishDto.setFlavors(flavorList);

            return dishDto;
        }).collect(Collectors.toList());

        String dishJsonStr = JSONUtil.toJsonStr(dishDtoList);

        //将查询出来的数据重新缓存到Redis中
        stringRedisTemplate.opsForValue().set(key,dishJsonStr,60L, TimeUnit.MINUTES);

        //返回查询结果
        return R.success(dishDtoList);
    }

}
