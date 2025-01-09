package com.waerwak.service.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.waerwak.entity.Employee;
import com.waerwak.mapper.EmplopeeMapper;
import com.waerwak.service.EmployeeService;
import org.springframework.stereotype.Service;

@Service
public class EmployeeServiceImpl
        extends ServiceImpl<EmplopeeMapper, Employee>
        implements EmployeeService {
}
