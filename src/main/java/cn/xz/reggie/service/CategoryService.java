package cn.xz.reggie.service;

import cn.xz.reggie.common.R;
import cn.xz.reggie.entity.Category;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface CategoryService extends IService<Category> {
    R<String> saveCategory(Category category);

    R<Page<Category>> pageList(int page, int pageSize);

    R<String> deleteCategory(Long id);

    R<String> updateCategory(Category category);

    R<List<Category>> listCategory(Category category);
}
