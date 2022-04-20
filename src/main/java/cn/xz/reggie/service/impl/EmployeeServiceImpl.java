package cn.xz.reggie.service.impl;

import cn.xz.reggie.common.R;
import cn.xz.reggie.entity.Employee;
import cn.xz.reggie.mapper.EmployeeMapper;
import cn.xz.reggie.service.EmployeeService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

@Slf4j
@Service
public class EmployeeServiceImpl extends ServiceImpl<EmployeeMapper, Employee> implements EmployeeService {
    @Override
    public R<Employee> userLoginCheck(HttpServletRequest request, Employee employee) {
        //1、将页面提交的密码进行MD5加密处理
        String password = employee.getPassword();
        password=DigestUtils.md5DigestAsHex(password.getBytes());
        //2、根据用户提交的用户名查询数据库
        LambdaQueryWrapper<Employee> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(Employee::getUsername,employee.getUsername());
        Employee empRes = getOne(queryWrapper);
        //3、如果没有查询到则返回登录失败的结果
        if (empRes==null) {
            return R.error("登录失败！");
        }
        //4、密码对比，如果不一致，返回失败结果
        if (!empRes.getPassword().equals(password)) {
            return R.error("密码错误！");
        }
        //5、查看员工状态，如果是已经禁用的状态，则返回员工禁用状态
        if (empRes.getStatus()==0) {
            return R.error("此账号被管理员禁用！");
        }
        //6、登录成功，将员工id存入Session并返回登录成功结果
        request.getSession().setAttribute("employee",empRes.getId());
        return R.success(empRes);
    }

    @Override
    @Transactional
    public R<String> createEmployee(HttpServletRequest request, Employee employee) {
        //设置一个处理密码，并进行MD5加密
        employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));
        //设置创建时间和更新时间
        //employee.setCreateTime(LocalDateTime.now());
        //employee.setUpdateTime(LocalDateTime.now());
        //设置创建人和更新人
        //Long empID = (Long) request.getSession().getAttribute("employee");
        //employee.setCreateUser(empID);
        //employee.setUpdateUser(empID);

        boolean success = save(employee);

        return R.success("保存员工成功！");
    }

    @Override
    public R<Page<Employee>> pageList(int page, int pageSize, String name) {

        LambdaQueryWrapper<Employee> wrapper=new LambdaQueryWrapper<>();

        wrapper.like(StringUtils.isNotBlank(name),Employee::getUsername,name)
                .orderByDesc(Employee::getUpdateTime);
        Page<Employee> pageInfo = new Page<>(page, pageSize);
        page(pageInfo,wrapper);

        return R.success(pageInfo);
    }

    @Override
    @Transactional
    public R<String> modifyEmployee(HttpServletRequest request,Employee employee) {
        log.info("员工信息：{}",employee.toString());

        //Long empID = (Long) request.getSession().getAttribute("employee");
        //设置更新执行人个更新时间
        //employee.setUpdateTime(LocalDateTime.now());
        //employee.setUpdateUser(empID);
        boolean b = updateById(employee);

        return R.success("员工信息修改成功!");
    }

    @Override
    public R<Employee> getEmpById(Long id) {
        Employee employee = getById(id);
        if(employee==null){
            return R.error("查询员工信息失败！");
        }
        return R.success(employee);
    }
}
