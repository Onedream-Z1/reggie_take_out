package cn.xz.reggie.service.impl;

import cn.xz.reggie.common.BaseThreadLocalContext;
import cn.xz.reggie.common.CustomException;
import cn.xz.reggie.common.R;
import cn.xz.reggie.dto.OrdersDto;
import cn.xz.reggie.entity.*;
import cn.xz.reggie.mapper.OrdersMapper;
import cn.xz.reggie.service.OrdersService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
@Slf4j
public class OrdersServiceImpl extends ServiceImpl<OrdersMapper, Orders> implements OrdersService {

    @Autowired
    private OrderDetailServiceImpl orderDetailService;

    @Autowired
    private UserServiceImpl userService;

    @Autowired
    private AddressBookServiceImpl addressBookService;

    @Autowired
    private ShoppingCartServiceImpl shoppingCartService;

    /**
     * 用户下单
     * @param orders
     * @return
     */
    @Override
    @Transactional
    public R<String> submit(Orders orders) {
        //log.info("orders={}",orders);

        //获取当前用户id
        Long userId = BaseThreadLocalContext.getCurrentId();
        //查询当前用户的购物车信息
        LambdaQueryWrapper<ShoppingCart> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ShoppingCart::getUserId,userId);
        List<ShoppingCart> shoppingCarts = shoppingCartService.list(wrapper);

        //查询用户信息
        User user = userService.getById(userId);
        //查询地址信息
        Long addressBookId = orders.getAddressBookId();
        AddressBook addressBook = addressBookService.getById(addressBookId);
        if(addressBook == null){
            throw new CustomException("用户地址信息有误，不能下单");
        }

        //动态生成订单号
        long orderId = IdWorker.getId();

        //原子操作，可以保证我们计算的准确性，同时也是线程安全的
        AtomicInteger amout=new AtomicInteger(0);

        List<OrderDetail> orderDetails = shoppingCarts.stream().map((itme)->{
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setOrderId(orderId);
            orderDetail.setNumber(itme.getNumber());
            orderDetail.setDishFlavor(itme.getDishFlavor());
            orderDetail.setDishId(itme.getDishId());
            orderDetail.setSetmealId(itme.getSetmealId());
            orderDetail.setName(itme.getName());
            orderDetail.setImage(itme.getImage());
            orderDetail.setAmount(itme.getAmount());
            amout.addAndGet(itme.getAmount().multiply(new BigDecimal(itme.getNumber())).intValue());
            return orderDetail;
        }).collect(Collectors.toList());

        //设置orders中的相关数据
        orders.setId(orderId);
        orders.setOrderTime(LocalDateTime.now());
        orders.setCheckoutTime(LocalDateTime.now());
        orders.setUserId(userId);
        orders.setNumber(String.valueOf(orderId));
        orders.setAmount(BigDecimal.valueOf(amout.get()));
        orders.setUserName(user.getName());
        orders.setStatus(2);
        orders.setConsignee(addressBook.getConsignee());
        orders.setPhone(addressBook.getPhone());
//        orders.setAddress(addressBook.getProvinceName()==null?"":addressBook.getProvinceName()
//                        +addressBook.getCityName()==null?"":addressBook.getCityName()
//                        +addressBook.getDetail()==null?"":addressBook.getDetail());
        orders.setAddress(addressBook.getDetail());
        //向订单表插入数据，一条数据
        this.save(orders);

        //向订单明细表插入数据，多条数据
        orderDetailService.saveBatch(orderDetails);
        //清空该用户的购物车信息
        shoppingCartService.remove(wrapper);

        return R.success("下单成功!");
    }

    @Override
    public R<Page> pageOrder(int page, int pageSize) {
        //获取当前用户的id
        Long userId = BaseThreadLocalContext.getCurrentId();
        //构造两个分页对象
        Page<Orders> ordersPage = new Page<>(page, pageSize);
        Page<OrdersDto> ordersDtoPage = new Page<>(page,pageSize);
        //构造一个queryWrapper，根据用户id查询订单
        LambdaQueryWrapper<Orders> ordersWrapper = new LambdaQueryWrapper<>();
        ordersWrapper.eq(Orders::getUserId,userId);
        //查询最新的订单
        ordersWrapper.orderByDesc(Orders::getCheckoutTime);
        //分页查询Order订单表
        this.page(ordersPage,ordersWrapper);
        //将order的page订单中的数据拷贝给orderDto中的page
        BeanUtils.copyProperties(ordersPage,ordersDtoPage,"records");
        //填充orderDTO中的内容
//        String userName = userService.getById(userId).getName();
        R<AddressBook> deaultAddr = addressBookService.getDeaultAddr();

        List<Orders> ordersRecords = ordersPage.getRecords();
        List<OrdersDto> records=ordersRecords.stream().map((item)->{
            OrdersDto ordersDto = new OrdersDto();
            //ordersDto是我们自己new出来的，其他的值都没有
            BeanUtils.copyProperties(item,ordersDto);

            ordersDto.setUserName(deaultAddr.getData().getConsignee());
            ordersDto.setPhone(deaultAddr.getData().getPhone());
            ordersDto.setAddress(deaultAddr.getData().getDetail());
            ordersDto.setConsignee(deaultAddr.getData().getConsignee());

//            Integer number = Integer.getInteger(item.getNumber());
            log.info("-------------------number={}",item.getNumber());
            //获取订单详情orderDetail
            LambdaQueryWrapper<OrderDetail> orderDetailLambdaQueryWrapper = new LambdaQueryWrapper<>();
            orderDetailLambdaQueryWrapper.eq(OrderDetail::getOrderId,item.getNumber());
            List<OrderDetail> orderDetails = orderDetailService.list(orderDetailLambdaQueryWrapper);

            ordersDto.setOrderDetails(orderDetails);
            return ordersDto;

        }).collect(Collectors.toList());

        ordersDtoPage.setRecords(records);
        //返回OrderDetail的page对象

        return R.success(ordersDtoPage);
    }
}
