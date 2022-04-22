package cn.xz.reggie.controller;

import cn.xz.reggie.common.R;
import cn.xz.reggie.entity.ShoppingCart;
import cn.xz.reggie.service.ShoppingCartService;
import cn.xz.reggie.service.impl.ShoppingCartServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Service
@RequestMapping("/shoppingCart")
public class ShoppingCartController {

    @Autowired
    private ShoppingCartService shoppingCartService;

    /**
     * 购物车添加功能
     */
    @PostMapping("/add")
    public R<ShoppingCart> add(@RequestBody ShoppingCart shoppingCart){
        return shoppingCartService.add(shoppingCart);
    }

    /**
     * 购物车展示功能
     */
    @GetMapping("/list")
    public R<List<ShoppingCart>> list(){
        return shoppingCartService.listShopppingCart();
    }

    /**
     * 购物车清空功能
     */
    @DeleteMapping("/clean")
    public R<String> clean(){
        return shoppingCartService.deleteCart();
    }

    /**
     * 商品减按钮
     */
    @PostMapping("/sub")
    public R<ShoppingCart> sub(@RequestBody ShoppingCart shoppingCart){
        return shoppingCartService.subCart(shoppingCart);
    }


}
