package com.smile.skype.learnmybatis.dao;

import com.smile.skype.learnmybatis.bean.Employee;

public interface EmployeeMapperPlus {
    Employee getEmpById(Integer id);

    Employee getEmpAndDeptById(Integer id);

    Employee getEmpAndDeptStepById(Integer id);

    Employee getEmpByDeptId(Integer deptId);
}
