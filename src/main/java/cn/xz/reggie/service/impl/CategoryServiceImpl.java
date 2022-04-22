package cn.xz.reggie.service.impl;

import cn.xz.reggie.common.CustomException;
import cn.xz.reggie.common.R;
import cn.xz.reggie.entity.Category;
import cn.xz.reggie.entity.Dish;
import cn.xz.reggie.entity.Setmeal;
import cn.xz.reggie.mapper.CategoryMapper;
import cn.xz.reggie.service.CategoryService;
import cn.xz.reggie.service.DishService;
import cn.xz.reggie.service.SetmealService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {

    @Autowired
    private DishService dishService;
    @Autowired
    private SetmealService setmealService;

    @Override
    @Transactional
    public R<String> saveCategory(Category category) {
        log.info("category：{}",category.toString());
        boolean isSave = save(category);
        return R.success("新增菜品/套餐成功");
    }

    @Override
    public R<Page<Category>> pageList(int page, int pageSize) {
        Page<Category> categoryPage = new Page<>(page, pageSize);
        LambdaQueryWrapper<Category> wrapper=new LambdaQueryWrapper<>();
        //根据我们的sort字段来进来排序
        wrapper.orderByAsc(Category::getSort);
        page(categoryPage,wrapper);
        return R.success(categoryPage);
    }

    /**
     * 删除套餐/菜品的逻辑就是判断一下其是否有关联的菜品，如果有就无法删除，否则就可以直接删除
     * @param id
     * @return
     */
    @Override
    public R<String> deleteCategory(Long id) {
        //添加查询条件，根据分类ID进行查询dish
        LambdaQueryWrapper<Dish> dishLambdaQueryWrapper=new LambdaQueryWrapper<>();
        dishLambdaQueryWrapper.eq(Dish::getCategoryId,id);
        //SELECT COUNT( * ) FROM setmeal WHERE (category_id = ?)
        long dishCount = dishService.count(dishLambdaQueryWrapper);
        //查询当前分类是否关联的菜品如果已经关联就抛出一个异常
        if (dishCount>0) {
            //说明关联了菜品
            throw new CustomException("当前分类关联了菜品，不能删除");
        }

        //添加查询条件，根据分类ID进行查询setmeal
        LambdaQueryWrapper<Setmeal> setmealLambdaQueryWrapper=new LambdaQueryWrapper<>();
        setmealLambdaQueryWrapper.eq(Setmeal::getCategoryId,id);
        long setmealCount = setmealService.count(setmealLambdaQueryWrapper);
        //查询当前分类是否关联了套餐如果已经关联就抛出一个异常
        if (setmealCount>0) {
            //说明关联了菜品
            throw new CustomException("当前套餐关联了菜品，不能删除");

        }
        //正常删除分类
        removeById(id);
        return R.success("删除成功");
    }

    @Override
    public R<String> updateCategory(Category category) {
        //UPDATE category SET name=?, sort=?, update_time=?, update_user=? WHERE id=?
        boolean isUpdated = updateById(category);
        if(!isUpdated){
            R.error("修改失败");
        }
        return R.success("修改成功");
    }

    @Override
    public R<List<Category>> listCategory(Category category) {
        //构造天剑构造器
        var wrapper=new LambdaQueryWrapper<Category>();
        //根据type来查询category
        wrapper.eq(category.getType()!=null,Category::getType,category.getType());
        //进行sort升序，updateTime降序排序
        wrapper.orderByAsc(Category::getSort).orderByDesc(Category::getUpdateTime);
        //返回最后查询的结构
        return R.success(list(wrapper));
    }
}
