package cn.xz.reggie.controller;

import cn.xz.reggie.common.R;
import cn.xz.reggie.entity.User;
import cn.xz.reggie.service.UserService;
import cn.xz.reggie.service.impl.UserServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@RestController
@Slf4j
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;

    /**
     * 验证码获取
     */
    @PostMapping("/sendMsg")
    public R<String> sendMessage(@RequestBody String phone){
        return userService.sendMsg(phone);
    }

    /**
     * 用户登录
     */
    @PostMapping("/login")
    public R<User> login(@RequestBody Map map, HttpServletRequest request){
        return userService.login(map,request);
    }

    @PostMapping("/logout")
    public R<String> logout(HttpServletRequest request){
        request.getSession().removeAttribute("user");
        return R.success("退出成功");
    }


}
