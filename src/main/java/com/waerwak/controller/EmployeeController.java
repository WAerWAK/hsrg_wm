package com.waerwak.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.waerwak.common.R;
import com.waerwak.entity.Employee;
import com.waerwak.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

/**
 * @FileName EmployeeController
 * @Date 2025/1/3
 * @Description 关于员工模块的接口
 */
@RestController
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    /**
     * 员工登录
     */
    @PostMapping("/employee/login")
    public R<Employee> empLogin(HttpServletRequest request, @RequestBody Employee employee) {
        System.out.println("登录接收的数据, 用户名:" + employee.getUsername() + ",密码:" + employee.getPassword());

        // 构建条件
        QueryWrapper<Employee> wrapper = new QueryWrapper<>();
        wrapper.eq("username", employee.getUsername());

        // 对密码加密后再查数据库
        String md5Pass = DigestUtils.md5DigestAsHex(employee.getPassword().getBytes());
        System.out.println("加密后的密码: " + md5Pass);
        wrapper.eq("password", md5Pass);

        Employee emp = employeeService.getOne(wrapper);

        if (emp == null) {
            return R.error("用户名或密码错误");
        }

        if (emp.getStatus() == 0) {
            return R.error("账户已禁用!");
        }

        //将用户id作为employee存储在session中
        request.getSession().setAttribute("employee", emp.getId());

        return R.success(emp);
    }

    /**
     * 分页+模糊查询全部员工
     */
    @GetMapping("/employee/page")
    public R<Page<Employee>> page(Integer pageSize, Integer page, Employee employee) {

        Page<Employee> pageInfo = new Page<>(page, pageSize);

        QueryWrapper<Employee> wrapper = new QueryWrapper<>();
        String name = employee.getName();

        // 如果模糊搜索的用户名为空,那就不拼接,直接查全表
        // 如果模糊搜索的用户名不为空,有值,那就拼接上去模糊搜索
        wrapper.like(StringUtils.hasText(name), "name", employee.getName());

        pageInfo = employeeService.page(pageInfo, wrapper);

        System.out.println("总页数  = " + pageInfo.getPages());
        System.out.println("总条数  = " + pageInfo.getTotal());
        System.out.println("查询数据集合  = " + pageInfo.getRecords());

        return R.success(pageInfo);// 注意此处一定要返回pageInfo对象
    }

    /**
     * 员工退出
     */
    @PostMapping("/employee/logout")
    public R<String> empLogout(HttpServletRequest request) {

        //要清空session
        request.getSession().removeAttribute("employee");

        return R.success("退出");
    }

    /**
     * 添加员工
     */
    @PostMapping("/employee")
    public R<String> addEmployee(HttpServletRequest request, @RequestBody Employee employee) {
        // 设置初始密码并加密保存
        employee.setPassword("123456");
        String md5Pass = DigestUtils.md5DigestAsHex(employee.getPassword().getBytes());
        employee.setPassword(md5Pass);

        employee.setCreateTime(new Date());
        employee.setUpdateTime(new Date());

        // 从session中拿到当前登陆的用户id，将其存放到CreateUser中
        Long employee1 = (Long) request.getSession().getAttribute("employee");
        employee.setCreateUser(employee1);
        employee.setUpdateUser(employee1);

        System.out.println(employee.toString());
        employeeService.save(employee);
        return R.success("保存成功");
    }

    /**
     * 根据ID查询员工详情
     */
    @GetMapping("/employee/{id}")
    public R<Employee> getOne(@PathVariable("id") Long id) {
        System.out.println("进入到getOne方法，获取的id为" + id);
        return R.success(employeeService.getById(id));
    }

    /**
     * 修改员工信息（包括禁用/启用）
     */
    @PutMapping("/employee")
    public R<String> editEmployee(HttpServletRequest request, @RequestBody Employee employee) {
        System.out.println("进入到getOne方法，获取的employee为:" + employee);
        Long employee1 = (Long) request.getSession().getAttribute("employee");
        employee.setUpdateUser(employee1);
        employee.setUpdateTime(new Date());
        boolean update = employeeService.updateById(employee);
        if (update) {
            return R.success("修改成功");
        }
        return R.error("修改失败");
    }

}