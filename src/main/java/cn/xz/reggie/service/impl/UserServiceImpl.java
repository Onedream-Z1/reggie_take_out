package cn.xz.reggie.service.impl;

import cn.xz.reggie.common.R;
import cn.xz.reggie.entity.User;
import cn.xz.reggie.mapper.UserMapper;
import cn.xz.reggie.service.UserService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Map;

@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
    /**
     * 用户登录
     * @param map
     * @param request
     * @return
     */
    @Override
    public R<User> login(Map map, HttpServletRequest request) {
        //log.info("mayValue={}",map.get("phone"));
        //判断以及验证验证码部分省略

        //获取用户的手机号
        String phone = (String) map.get("phone");

        //判断验证码是否为空……

        //构造一个条件构造器
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getPhone,phone);
        User user = getOne(wrapper);
        if (user==null) {
            //代表用户是新用户，直接注册
            user=new User();
            user.setPhone(phone);
            user.setStatus(1);
            save(user);
        }
        HttpSession session = request.getSession();
        session.setAttribute("user",user.getId());

        return R.success(user);
    }
}
