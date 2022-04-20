package cn.xz.reggie.service;

import cn.xz.reggie.common.R;
import cn.xz.reggie.entity.Employee;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import javax.servlet.http.HttpServletRequest;

public interface EmployeeService extends IService<Employee>{
    R<Employee> userLoginCheck(HttpServletRequest request, Employee employee);

    R<String> createEmployee(HttpServletRequest request, Employee employee);

    R<Page<Employee>> pageList(int page, int pageSize, String name);

    R<String> modifyEmployee(HttpServletRequest request,Employee employee);

    R<Employee> getEmpById(Long id);
}
