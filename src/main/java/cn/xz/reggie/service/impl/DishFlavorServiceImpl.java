package cn.xz.reggie.service.impl;

import cn.xz.reggie.entity.DishFlavor;
import cn.xz.reggie.mapper.DishFlavorMapper;
import cn.xz.reggie.service.DishFlavorService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@Service
public class DishFlavorServiceImpl extends ServiceImpl<DishFlavorMapper, DishFlavor> implements DishFlavorService {
}
