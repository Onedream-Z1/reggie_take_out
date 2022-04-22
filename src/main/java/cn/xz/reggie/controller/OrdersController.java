package cn.xz.reggie.controller;

import cn.xz.reggie.common.R;
import cn.xz.reggie.entity.Orders;
import cn.xz.reggie.service.OrdersService;
import cn.xz.reggie.service.impl.OrdersServiceImpl;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequestMapping("/order")
public class OrdersController {
    @Autowired
    private OrdersService ordersService;

    @PostMapping("/submit")
    public R<String> submit(@RequestBody Orders orders){
        return ordersService.submit(orders);
    }

    /**
     * 最新订单查询
     */
    @GetMapping("/userPage")
    private R<Page> pageOrder(int page,int pageSize){
        return ordersService.pageOrder(page,pageSize);
    }

}
