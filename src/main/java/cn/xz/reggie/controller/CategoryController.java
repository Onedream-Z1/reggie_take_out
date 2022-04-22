package cn.xz.reggie.controller;

import cn.xz.reggie.common.R;
import cn.xz.reggie.entity.Category;
import cn.xz.reggie.service.CategoryService;
import cn.xz.reggie.service.impl.CategoryServiceImpl;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/category")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @PostMapping
    public R<String> saveCategory(@RequestBody Category category){
        return categoryService.saveCategory(category);
    }

    //分页展示数据
    @GetMapping("/page")
    public R<Page<Category>> showPage(int page, int pageSize){
        return categoryService.pageList(page,pageSize);
    }

    /**
     * 删除菜品/套餐
     */
    @DeleteMapping
    public R<String> delCategory(Long ids){
        log.info("ids:,{}",ids);
        return categoryService.deleteCategory(ids);
    }

    /**
     * 修改套餐/菜品
     */
    @PutMapping
    public R<String> updateCategory(@RequestBody Category category){
        log.info("category={}",category.toString());
        return categoryService.updateCategory(category);
    }

    /**
     * 添加菜品中的菜品分类下拉框业务实现
     */

    @GetMapping("/list")
    public R<List<Category>> listCategory(Category category){
        return categoryService.listCategory(category);
    }

}
