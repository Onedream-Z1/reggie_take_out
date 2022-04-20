package cn.xz.reggie.service.impl;

import cn.xz.reggie.entity.OrderDetail;
import cn.xz.reggie.mapper.OrderDetailMapper;
import cn.xz.reggie.service.OrderDetailService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class OrderDetailServiceImpl extends ServiceImpl<OrderDetailMapper, OrderDetail> implements OrderDetailService {
}
