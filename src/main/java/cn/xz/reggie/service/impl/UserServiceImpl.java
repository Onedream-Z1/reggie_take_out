package cn.xz.reggie.service.impl;

import cn.xz.reggie.common.R;
import cn.xz.reggie.entity.User;
import cn.xz.reggie.mapper.UserMapper;
import cn.xz.reggie.service.UserService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.math.RandomUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    //注入StringRedisTemplate
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    private static final String LOGIN="login:code:";



    @Override
    public R<String> sendMsg(String phone) {
        String code = String.valueOf((int)(Math.random() * 1000000));
        log.info("random={}",code);

        stringRedisTemplate.opsForValue().set(LOGIN+phone,code,5L, TimeUnit.MINUTES);

        return R.success("获取验证码成功！");
    }

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
        //获取用户的手机号和用户传过来的验证码
        String phone = (String) map.get("phone");
        String code = (String) map.get("code");

        //从redis中获取验证码
        String redisCode = stringRedisTemplate.opsForValue().get(LOGIN + phone);

        //判断验证码是否为空
        if(StringUtils.isNotBlank(redisCode) && redisCode.equals(code)){
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

            //登录成功之后，删除redis中的验证码
            stringRedisTemplate.delete(LOGIN + phone);

            return R.success(user);
        }else{
            return R.error("验证码错误");
        }
    }
}
