package cn.xz.reggie.service;

import cn.xz.reggie.common.R;
import cn.xz.reggie.entity.Orders;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

public interface OrdersService extends IService<Orders> {
    R<String> submit(Orders orders);

    R<Page> pageOrder(int page, int pageSize);
}
