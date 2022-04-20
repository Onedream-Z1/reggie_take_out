package cn.xz.reggie.service;


import cn.xz.reggie.common.R;
import cn.xz.reggie.entity.ShoppingCart;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface ShoppingCartService extends IService<ShoppingCart> {
    R<ShoppingCart> add(ShoppingCart shoppingCart);

    R<List<ShoppingCart>> listShopppingCart();

    R<String> deleteCart();

    R<ShoppingCart> subCart(ShoppingCart shoppingCart);
}
