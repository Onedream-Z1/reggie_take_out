package cn.xz.reggie.service;

import cn.xz.reggie.common.R;
import cn.xz.reggie.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

public interface UserService extends IService<User> {
    R<User> login(Map map, HttpServletRequest request);
}
