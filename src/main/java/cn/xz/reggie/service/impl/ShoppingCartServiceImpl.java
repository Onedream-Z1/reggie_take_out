package cn.xz.reggie.service.impl;

import cn.xz.reggie.common.BaseThreadLocalContext;
import cn.xz.reggie.common.R;
import cn.xz.reggie.entity.ShoppingCart;
import cn.xz.reggie.mapper.ShoppingCartMapper;
import cn.xz.reggie.service.ShoppingCartService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
public class ShoppingCartServiceImpl extends ServiceImpl<ShoppingCartMapper, ShoppingCart> implements ShoppingCartService {

    /**
     * 购物车添加功能
     * @param shoppingCart
     * @return
     */
    @Override
    public R<ShoppingCart> add(ShoppingCart shoppingCart) {
        //log.info("shopingCart={}",shoppingCart.toString());

        //设置用户id，指定当前是哪个用户的购物车数据
        Long userID = BaseThreadLocalContext.getCurrentId();
        shoppingCart.setUserId(userID);
        //查询当前菜品或者套餐是否在购物车中
        LambdaQueryWrapper<ShoppingCart> wrapper = new LambdaQueryWrapper<>();
        Long dishId = shoppingCart.getDishId();
        Long setmealId = shoppingCart.getSetmealId();
        if(dishId!=null){
            wrapper.eq(ShoppingCart::getDishId,dishId);
        }else {
            wrapper.eq(ShoppingCart::getSetmealId,setmealId);
        }
        ShoppingCart shoppingCartTwo = this.getOne(wrapper);
        if (shoppingCartTwo!=null) {
            //如果已经存在，则在原来的数量上加一
            Integer num = shoppingCartTwo.getNumber();
            shoppingCartTwo.setNumber(num+1);
            this.updateById(shoppingCartTwo);
        }else{
            //如果不存在，则添加到购物车，数量默认就是一
            shoppingCart.setNumber(1);
            shoppingCart.setCreateTime(LocalDateTime.now());
            this.save(shoppingCart);
            shoppingCartTwo=shoppingCart;
        }

        return R.success(shoppingCartTwo);
    }

    /**
     * 用户购物车展示
     * @return
     */
    @Override
    public R<List<ShoppingCart>> listShopppingCart() {

        //根据用户Id来查询购物车
        LambdaQueryWrapper<ShoppingCart> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ShoppingCart::getUserId,BaseThreadLocalContext.getCurrentId())
                .orderByDesc(ShoppingCart::getCreateTime);
        List<ShoppingCart> shoppingCarts = this.list(wrapper);
        return R.success(shoppingCarts);
    }

    /**
     * 清空购物车功能
     * @return
     */
    @Override
    public R<String> deleteCart() {

        LambdaQueryWrapper<ShoppingCart> wrapper = new LambdaQueryWrapper<>();
        //根据用户id来删除
        wrapper.eq(ShoppingCart::getUserId,BaseThreadLocalContext.getCurrentId());
        this.remove(wrapper);
        return R.success("清空购物车成功");
    }

    /**
     * 商品减功能
     * @param shoppingCart
     * @return
     */
    @Override
    public R<ShoppingCart> subCart(ShoppingCart shoppingCart) {

        //根据用户id来查询
        shoppingCart.setUserId(BaseThreadLocalContext.getCurrentId());
        //判断是dish_di还是setmeal_id
        Long dishId = shoppingCart.getDishId();
        LambdaQueryWrapper<ShoppingCart> wrapper = new LambdaQueryWrapper<>();
        if(dishId!=null){
            wrapper.eq(ShoppingCart::getDishId,dishId);
        }else {
            wrapper.eq(ShoppingCart::getSetmealId,shoppingCart.getSetmealId());
        }
        wrapper.eq(ShoppingCart::getUserId,shoppingCart.getUserId());
        ShoppingCart shoppingCart2 = this.getOne(wrapper);
        //判断商品中的数量是否等于一
        Integer number = shoppingCart2.getNumber();
        if(number>1){
            shoppingCart2.setNumber(number-1);
            this.updateById(shoppingCart2);
            shoppingCart=shoppingCart2;
        }else{
            this.removeById(shoppingCart2);
        }
        return R.success(shoppingCart);
    }
}
