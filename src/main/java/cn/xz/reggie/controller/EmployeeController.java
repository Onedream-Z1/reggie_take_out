package cn.xz.reggie.controller;


import cn.xz.reggie.common.R;
import cn.xz.reggie.entity.Employee;
import cn.xz.reggie.service.impl.EmployeeServiceImpl;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.net.http.HttpRequest;

@Slf4j
@RestController
@RequestMapping("/employee")
public class EmployeeController {

    @Autowired
    private EmployeeServiceImpl employeeService;

    @PostMapping("/login")
    public R<Employee> userLogin(HttpServletRequest request, @RequestBody Employee employee){
        return employeeService.userLoginCheck(request,employee);
    }

    @PostMapping("/logout")
    public R<String> userLogout(HttpServletRequest request){
        //清空Session中的UserId来实现退出用户的功能
        request.getSession().removeAttribute("userInfo");
        return R.success("退出成功！");

    }

    @PostMapping
    public R<String> saveEmployee(HttpServletRequest request,@RequestBody Employee employee){
        return employeeService.createEmployee(request,employee);
    }

    //员工分页
    @GetMapping("/page")
    public R<Page<Employee>> employeePageList(int page,int pageSize,String name){
        log.info("page = {},pageSize = {},name = {}",page,pageSize,name);
        return employeeService.pageList(page,pageSize,name);
    }

    @PutMapping
    public R<String> updateEmployee(HttpServletRequest request,@RequestBody Employee employee){
        return employeeService.modifyEmployee(request,employee);
    }

    //擦护心单个员工，用于表单的回显
    @GetMapping("/{id}")
    public R<Employee> getEmployeeById(@PathVariable Long id){
        return employeeService.getEmpById(id);
    }

}
